package net.jaftsun.fromtheskies.takeover.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.jaftsun.fromtheskies.takeover.TakeoverLifecycleState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * Persistent takeover runtime state for a single server level.
 */
public class TakeoverSavedData extends SavedData {
    // Persistence Metadata
    public static final String DATA_NAME = "fromtheskies_takeover";
    public static final SavedData.Factory<TakeoverSavedData> FACTORY = new SavedData.Factory<>(
            TakeoverSavedData::new,
            TakeoverSavedData::load);

    // NBT Key Constants
    private static final String STATE_KEY = "state";
    private static final String TAKEOVER_LOCKED_KEY = "takeoverLocked";
    private static final String ARMED_AT_GAME_TIME_KEY = "armedAtGameTime";
    private static final String SCHEDULED_METEOR_GAME_TIME_KEY = "scheduledMeteorGameTime";
    private static final String SCHEDULER_RETRY_TICKS_REMAINING_KEY = "schedulerRetryTicksRemaining";
    private static final String LAST_SPREAD_TICK_GAME_TIME_KEY = "lastSpreadTickGameTime";
    private static final String CORE_POS_KEY = "corePos";
    private static final String GENERATED_CHUNKS_KEY = "generatedChunks";
    private static final String INFECTED_SURFACE_BLOCKS_KEY = "infectedSurfaceBlocks";
    private static final String CONVERTED_CHUNKS_KEY = "convertedChunks";
    private static final String ELIGIBLE_SURFACE_COUNTS_KEY = "eligibleSurfaceCounts";
    private static final String INFECTED_SURFACE_COUNTS_KEY = "infectedSurfaceCounts";
    private static final String CHUNK_KEY = "chunk";
    private static final String COUNT_KEY = "count";
    private static final String BLOCKED_FRONTIER_EDGES_KEY = "blockedFrontierEdges";

    // Shared Helper Constants
    private static final long NO_GAME_TIME = -1L;
    private static final String FRONTIER_EDGE_SEPARATOR = "->";

    // Lifecycle And Scheduler State
    private TakeoverLifecycleState state = TakeoverLifecycleState.DORMANT;
    private boolean takeoverLocked;
    private long armedAtGameTime = NO_GAME_TIME;
    private long scheduledMeteorGameTime = NO_GAME_TIME;
    private int schedulerRetryTicksRemaining;
    private long lastSpreadTickGameTime = NO_GAME_TIME;
    private BlockPos corePos;

    // Packed Position/Chunk Tracking
    private final Set<Long> generatedChunkLongs = new HashSet<>();
    private final Set<Long> infectedSurfaceBlockLongs = new HashSet<>();
    private final Set<Long> convertedChunkLongs = new HashSet<>();

    // Per-Chunk Spread/Conversion Counters
    private final Map<Long, Integer> eligibleSurfaceCountsByChunk = new HashMap<>();
    private final Map<Long, Integer> infectedSurfaceCountsByChunk = new HashMap<>();

    // Frontier Diagnostics
    private final Set<String> blockedFrontierEdges = new HashSet<>();

    public static TakeoverSavedData get(ServerLevel level) {
        // Canonical access point: load existing state or initialize a new one.
        return level.getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    public static TakeoverSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        // Read takeover state from NBT (Minecraft's typed binary save format).
        TakeoverSavedData data = new TakeoverSavedData();
        data.state = parseState(tag.getString(STATE_KEY));
        data.takeoverLocked = tag.getBoolean(TAKEOVER_LOCKED_KEY);
        data.armedAtGameTime = tag.getLong(ARMED_AT_GAME_TIME_KEY);
        data.scheduledMeteorGameTime = tag.getLong(SCHEDULED_METEOR_GAME_TIME_KEY);
        data.schedulerRetryTicksRemaining = tag.getInt(SCHEDULER_RETRY_TICKS_REMAINING_KEY);
        data.lastSpreadTickGameTime = tag.getLong(LAST_SPREAD_TICK_GAME_TIME_KEY);
        data.corePos = readBlockPos(tag, CORE_POS_KEY);

        loadLongSet(tag, GENERATED_CHUNKS_KEY, data.generatedChunkLongs);
        loadLongSet(tag, INFECTED_SURFACE_BLOCKS_KEY, data.infectedSurfaceBlockLongs);
        loadLongSet(tag, CONVERTED_CHUNKS_KEY, data.convertedChunkLongs);
        loadCountMap(tag, ELIGIBLE_SURFACE_COUNTS_KEY, data.eligibleSurfaceCountsByChunk);
        loadCountMap(tag, INFECTED_SURFACE_COUNTS_KEY, data.infectedSurfaceCountsByChunk);
        loadStringSet(tag, BLOCKED_FRONTIER_EDGES_KEY, data.blockedFrontierEdges);
        data.setDirty(false);
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        // Serialize current takeover state back into NBT for disk persistence.
        tag.putString(STATE_KEY, this.state.name());
        tag.putBoolean(TAKEOVER_LOCKED_KEY, this.takeoverLocked);
        tag.putLong(ARMED_AT_GAME_TIME_KEY, this.armedAtGameTime);
        tag.putLong(SCHEDULED_METEOR_GAME_TIME_KEY, this.scheduledMeteorGameTime);
        tag.putInt(SCHEDULER_RETRY_TICKS_REMAINING_KEY, this.schedulerRetryTicksRemaining);
        tag.putLong(LAST_SPREAD_TICK_GAME_TIME_KEY, this.lastSpreadTickGameTime);
        writeBlockPos(tag, CORE_POS_KEY, this.corePos);

        tag.putLongArray(GENERATED_CHUNKS_KEY, toLongArray(this.generatedChunkLongs));
        tag.putLongArray(INFECTED_SURFACE_BLOCKS_KEY, toLongArray(this.infectedSurfaceBlockLongs));
        tag.putLongArray(CONVERTED_CHUNKS_KEY, toLongArray(this.convertedChunkLongs));
        tag.put(ELIGIBLE_SURFACE_COUNTS_KEY, writeCountMap(this.eligibleSurfaceCountsByChunk));
        tag.put(INFECTED_SURFACE_COUNTS_KEY, writeCountMap(this.infectedSurfaceCountsByChunk));
        tag.put(BLOCKED_FRONTIER_EDGES_KEY, writeStringSet(this.blockedFrontierEdges));
        return tag;
    }

    public TakeoverLifecycleState getState() {
        return this.state;
    }

    public void setState(TakeoverLifecycleState state) {
        this.state = Objects.requireNonNull(state, "state");
        // SavedData dirty flag: mark as changed so Minecraft writes it on next save.
        this.setDirty();
    }

    public boolean isTakeoverLocked() {
        return this.takeoverLocked;
    }

    public void setTakeoverLocked(boolean takeoverLocked) {
        this.takeoverLocked = takeoverLocked;
        this.setDirty();
    }

    public long getArmedAtGameTime() {
        return this.armedAtGameTime;
    }

    public void setArmedAtGameTime(long armedAtGameTime) {
        this.armedAtGameTime = armedAtGameTime;
        this.setDirty();
    }

    public long getScheduledMeteorGameTime() {
        return this.scheduledMeteorGameTime;
    }

    public void setScheduledMeteorGameTime(long scheduledMeteorGameTime) {
        this.scheduledMeteorGameTime = scheduledMeteorGameTime;
        this.setDirty();
    }

    public int getSchedulerRetryTicksRemaining() {
        return this.schedulerRetryTicksRemaining;
    }

    public void setSchedulerRetryTicksRemaining(int schedulerRetryTicksRemaining) {
        this.schedulerRetryTicksRemaining = Math.max(0, schedulerRetryTicksRemaining);
        this.setDirty();
    }

    public long getLastSpreadTickGameTime() {
        return this.lastSpreadTickGameTime;
    }

    public void setLastSpreadTickGameTime(long lastSpreadTickGameTime) {
        this.lastSpreadTickGameTime = lastSpreadTickGameTime;
        this.setDirty();
    }

    public BlockPos getCorePos() {
        return this.corePos;
    }

    public void setCorePos(BlockPos corePos) {
        this.corePos = Objects.requireNonNull(corePos, "corePos");
        this.setDirty();
    }

    public void clearCorePos() {
        this.corePos = null;
        this.setDirty();
    }

    public Set<ChunkPos> getGeneratedChunks() {
        return unpackChunkSet(this.generatedChunkLongs);
    }

    public int getGeneratedChunkCount() {
        return this.generatedChunkLongs.size();
    }

    public boolean hasGeneratedChunk(ChunkPos chunkPos) {
        return containsPackedChunk(this.generatedChunkLongs, chunkPos);
    }

    public void addGeneratedChunk(ChunkPos chunkPos) {
        markDirtyIf(addPackedChunk(this.generatedChunkLongs, chunkPos));
    }

    public Set<BlockPos> getInfectedSurfaceBlocks() {
        return unpackBlockPosSet(this.infectedSurfaceBlockLongs);
    }

    public void addInfectedSurfaceBlock(BlockPos pos) {
        markDirtyIf(addPackedBlockPos(this.infectedSurfaceBlockLongs, pos));
    }

    public boolean hasInfectedSurfaceBlock(BlockPos pos) {
        return this.infectedSurfaceBlockLongs.contains(pack(pos));
    }

    public int getInfectedSurfaceBlockCount() {
        return this.infectedSurfaceBlockLongs.size();
    }

    public Set<ChunkPos> getConvertedChunks() {
        return unpackChunkSet(this.convertedChunkLongs);
    }

    public void addConvertedChunk(ChunkPos chunkPos) {
        markDirtyIf(addPackedChunk(this.convertedChunkLongs, chunkPos));
    }

    public boolean hasConvertedChunk(ChunkPos chunkPos) {
        return containsPackedChunk(this.convertedChunkLongs, chunkPos);
    }

    public int getConvertedChunkCount() {
        return this.convertedChunkLongs.size();
    }

    public void addBlockedFrontierEdge(ChunkPos sourceChunk, ChunkPos targetChunk) {
        String edgeKey = toFrontierEdgeKey(sourceChunk, targetChunk);
        if (this.blockedFrontierEdges.add(edgeKey)) {
            this.setDirty();
        }
    }

    public boolean hasBlockedFrontierEdge(ChunkPos sourceChunk, ChunkPos targetChunk) {
        return this.blockedFrontierEdges.contains(toFrontierEdgeKey(sourceChunk, targetChunk));
    }

    public int getBlockedFrontierEdgeCount() {
        return this.blockedFrontierEdges.size();
    }

    public int getEligibleSurfaceCount(ChunkPos chunkPos) {
        return getChunkCount(this.eligibleSurfaceCountsByChunk, chunkPos);
    }

    public void setEligibleSurfaceCount(ChunkPos chunkPos, int count) {
        setChunkCount(this.eligibleSurfaceCountsByChunk, chunkPos, count);
    }

    public int getInfectedSurfaceCount(ChunkPos chunkPos) {
        return getChunkCount(this.infectedSurfaceCountsByChunk, chunkPos);
    }

    public void setInfectedSurfaceCount(ChunkPos chunkPos, int count) {
        setChunkCount(this.infectedSurfaceCountsByChunk, chunkPos, count);
    }

    public void resetForTesting() {
        // GameTests use this to guarantee deterministic state between scenarios.
        this.state = TakeoverLifecycleState.DORMANT;
        this.takeoverLocked = false;
        this.armedAtGameTime = NO_GAME_TIME;
        this.scheduledMeteorGameTime = NO_GAME_TIME;
        this.schedulerRetryTicksRemaining = 0;
        this.lastSpreadTickGameTime = NO_GAME_TIME;
        this.corePos = null;
        this.generatedChunkLongs.clear();
        this.infectedSurfaceBlockLongs.clear();
        this.convertedChunkLongs.clear();
        this.eligibleSurfaceCountsByChunk.clear();
        this.infectedSurfaceCountsByChunk.clear();
        this.blockedFrontierEdges.clear();
        this.setDirty();
    }

    private void setChunkCount(Map<Long, Integer> map, ChunkPos chunkPos, int count) {
        long packedChunk = pack(chunkPos);
        // Zero/negative values are represented as missing entries to keep serialization compact.
        if (count <= 0) {
            if (map.remove(packedChunk) != null) {
                this.setDirty();
            }
            return;
        }
        Integer previous = map.put(packedChunk, count);
        // Map.put returns the previous value (or null when absent) so we can avoid false dirty writes.
        if (previous == null || previous != count) {
            this.setDirty();
        }
    }

    private static int getChunkCount(Map<Long, Integer> map, ChunkPos chunkPos) {
        return map.getOrDefault(pack(chunkPos), 0);
    }

    private static void loadLongSet(CompoundTag tag, String key, Set<Long> target) {
        for (long value : tag.getLongArray(key)) {
            target.add(value);
        }
    }

    private static BlockPos readBlockPos(CompoundTag tag, String key) {
        // Tag.TAG_LONG guards type safety before reading packed BlockPos payload.
        if (!tag.contains(key, Tag.TAG_LONG)) {
            return null;
        }
        return BlockPos.of(tag.getLong(key));
    }

    private static void writeBlockPos(CompoundTag tag, String key, BlockPos pos) {
        if (pos != null) {
            tag.putLong(key, pack(pos));
        }
    }

    private static boolean containsPackedChunk(Set<Long> packedChunks, ChunkPos chunkPos) {
        return packedChunks.contains(pack(chunkPos));
    }

    private static boolean addPackedChunk(Set<Long> packedChunks, ChunkPos chunkPos) {
        return packedChunks.add(pack(chunkPos));
    }

    private static boolean addPackedBlockPos(Set<Long> packedPositions, BlockPos pos) {
        return packedPositions.add(pack(pos));
    }

    private static long pack(ChunkPos chunkPos) {
        return chunkPos.toLong();
    }

    private static long pack(BlockPos pos) {
        return pos.asLong();
    }

    private void markDirtyIf(boolean changed) {
        if (changed) {
            this.setDirty();
        }
    }

    private static long[] toLongArray(Set<Long> source) {
        long[] values = new long[source.size()];
        int index = 0;
        for (Long value : source) {
            values[index++] = value.longValue();
        }
        return values;
    }

    private static ListTag writeCountMap(Map<Long, Integer> source) {
        ListTag listTag = new ListTag();
        for (Map.Entry<Long, Integer> entry : source.entrySet()) {
            CompoundTag countTag = new CompoundTag();
            countTag.putLong(CHUNK_KEY, entry.getKey());
            countTag.putInt(COUNT_KEY, entry.getValue());
            listTag.add(countTag);
        }
        return listTag;
    }

    private static void loadCountMap(CompoundTag tag, String key, Map<Long, Integer> target) {
        // countTag is one CompoundTag entry with shape { chunk: long, count: int }.
        ListTag counts = tag.getList(key, Tag.TAG_COMPOUND);
        for (Tag rawTag : counts) {
            if (rawTag instanceof CompoundTag countTag && countTag.contains(CHUNK_KEY, Tag.TAG_LONG)) {
                // Clamp negatives to zero; only strictly positive counts are stored in memory.
                int count = Math.max(0, countTag.getInt(COUNT_KEY));
                if (count > 0) {
                    target.put(countTag.getLong(CHUNK_KEY), count);
                }
            }
        }
    }

    private static ListTag writeStringSet(Set<String> source) {
        ListTag listTag = new ListTag();
        for (String value : source) {
            listTag.add(StringTag.valueOf(value));
        }
        return listTag;
    }

    private static void loadStringSet(CompoundTag tag, String key, Set<String> target) {
        ListTag values = tag.getList(key, Tag.TAG_STRING);
        for (Tag value : values) {
            if (value instanceof StringTag stringTag) {
                target.add(stringTag.getAsString());
            }
        }
    }

    private static String toFrontierEdgeKey(ChunkPos sourceChunk, ChunkPos targetChunk) {
        // Stable directional key keeps frontier diagnostics deterministic across save/load.
        return pack(sourceChunk) + FRONTIER_EDGE_SEPARATOR + pack(targetChunk);
    }

    private static TakeoverLifecycleState parseState(String savedState) {
        // Defensive enum parsing: any missing/unknown value falls back to DORMANT.
        if (savedState == null || savedState.isBlank()) {
            return TakeoverLifecycleState.DORMANT;
        }
        try {
            return TakeoverLifecycleState.valueOf(savedState);
        } catch (IllegalArgumentException ignored) {
            return TakeoverLifecycleState.DORMANT;
        }
    }

    private static Set<ChunkPos> unpackChunkSet(Set<Long> packedChunks) {
        Set<ChunkPos> chunks = new HashSet<>(packedChunks.size());
        for (long packedChunk : packedChunks) {
            chunks.add(new ChunkPos(packedChunk));
        }
        return Collections.unmodifiableSet(chunks);
    }

    private static Set<BlockPos> unpackBlockPosSet(Set<Long> packedPositions) {
        Set<BlockPos> positions = new HashSet<>(packedPositions.size());
        for (long packedPosition : packedPositions) {
            positions.add(BlockPos.of(packedPosition));
        }
        return Collections.unmodifiableSet(positions);
    }
}
