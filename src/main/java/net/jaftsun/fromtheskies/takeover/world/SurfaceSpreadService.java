package net.jaftsun.fromtheskies.takeover.world;

import java.util.ArrayList;
import java.util.List;

import net.jaftsun.fromtheskies.Config;
import net.jaftsun.fromtheskies.block.ModBlocks;
import net.jaftsun.fromtheskies.takeover.TakeoverLifecycleState;
import net.jaftsun.fromtheskies.takeover.data.TakeoverSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Local adjacency spread engine plus chunk-level infection accounting.
 */
public final class SurfaceSpreadService {
    // Preferred offsets used to seed first infection around the landed core.
    private static final int[][] INITIAL_SEED_OFFSETS = new int[][] {
            { 1, 0 },
            { -1, 0 },
            { 0, 1 },
            { 0, -1 },
            { 0, 0 },
            { 1, 1 },
            { 1, -1 },
            { -1, 1 },
            { -1, -1 }
    };

    private SurfaceSpreadService() {
    }

    public static int tick(ServerLevel level) {
        return tick(
                level,
                TakeoverSavedData.get(level),
                Config.TAKEOVER_SPREAD_INTERVAL_TICKS.get(),
                Config.TAKEOVER_SPREAD_ATTEMPTS_PER_TICK.get(),
                level.random,
                level.getGameTime());
    }

    public static int tick(
            ServerLevel level,
            TakeoverSavedData data,
            int spreadIntervalTicks,
            int spreadAttemptsPerTick,
            RandomSource random,
            long gameTime) {
        // Spread only runs while takeover is active.
        if (data.getState() != TakeoverLifecycleState.ACTIVE) {
            return 0;
        }
        long lastTick = data.getLastSpreadTickGameTime();
        if (lastTick >= 0 && (gameTime - lastTick) < Math.max(1, spreadIntervalTicks)) {
            return 0;
        }
        data.setLastSpreadTickGameTime(gameTime);
        // Ensure ACTIVE takeover has at least one infected seed near the core before random walk spread.
        seedInitialInfectionIfNeeded(level, data);
        // Returns how many infection attempts actually succeeded this tick.
        int successfulSpreads = 0;
        for (int attempt = 0; attempt < Math.max(1, spreadAttemptsPerTick); attempt++) {
            // Snapshot current infected set so each attempt picks a random existing source.
            List<BlockPos> infectedBlocks = new ArrayList<>(data.getInfectedSurfaceBlocks());
            if (infectedBlocks.isEmpty()) {
                break;
            }
            BlockPos source = infectedBlocks.get(random.nextInt(infectedBlocks.size()));
            Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            if (spreadFromSourceForTesting(level, data, source, direction.getStepX(), direction.getStepZ(), true)) {
                successfulSpreads++;
            }
        }
        return successfulSpreads;
    }

    public static int recalculateChunkSurface(ServerLevel level, TakeoverSavedData data, ChunkPos chunkPos) {
        List<BlockPos> eligiblePositions = collectEligibleSurfacePositions(level, chunkPos);
        return updateChunkCountsFromEligiblePositions(data, chunkPos, eligiblePositions);
    }

    public static List<BlockPos> collectEligibleSurfacePositions(ServerLevel level, ChunkPos chunkPos) {
        List<BlockPos> positions = new ArrayList<>();
        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();
        for (int xOffset = 0; xOffset < 16; xOffset++) {
            for (int zOffset = 0; zOffset < 16; zOffset++) {
                BlockPos surfacePos = findTopEligibleSurface(level, minX + xOffset, minZ + zOffset);
                if (surfacePos != null) {
                    positions.add(surfacePos);
                }
            }
        }
        return positions;
    }

    public static int updateChunkCountsFromEligiblePositions(
            TakeoverSavedData data,
            ChunkPos chunkPos,
            Iterable<BlockPos> eligiblePositions) {
        int eligible = 0;
        int infected = 0;
        for (BlockPos surfacePos : eligiblePositions) {
            eligible++;
            if (data.hasInfectedSurfaceBlock(surfacePos)) {
                infected++;
            }
        }
        data.setEligibleSurfaceCount(chunkPos, eligible);
        data.setInfectedSurfaceCount(chunkPos, infected);
        return eligible;
    }

    public static boolean markInfectedIfEligible(ServerLevel level, TakeoverSavedData data, BlockPos pos) {
        BlockPos topSurface = findTopEligibleSurface(level, pos.getX(), pos.getZ());
        if (topSurface == null || !topSurface.equals(pos) || data.hasInfectedSurfaceBlock(pos)) {
            return false;
        }
        return infectSurfaceBlock(level, data, pos);
    }

    public static boolean spreadFromSourceForTesting(
            ServerLevel level,
            TakeoverSavedData data,
            BlockPos source,
            int offsetX,
            int offsetZ,
            boolean requireSourceInfected) {
        // Test/debug helper that mirrors production rules and returns true only for a real new infection.
        if (data.getState() != TakeoverLifecycleState.ACTIVE) {
            return false;
        }
        if (requireSourceInfected && !data.hasInfectedSurfaceBlock(source)) {
            return false;
        }
        // Enforce grass-like cardinal adjacency; no diagonal or long-range jumps.
        if (Math.abs(offsetX) + Math.abs(offsetZ) != 1) {
            return false;
        }
        BlockPos target = findTopEligibleSurface(level, source.getX() + offsetX, source.getZ() + offsetZ);
        if (target == null || data.hasInfectedSurfaceBlock(target)) {
            return false;
        }
        ChunkPos sourceChunk = new ChunkPos(source);
        ChunkPos targetChunk = new ChunkPos(target);
        // Frontier attempts pause until neighboring chunk has been indexed as generated.
        if (!data.hasGeneratedChunk(targetChunk)) {
            data.addBlockedFrontierEdge(sourceChunk, targetChunk);
            return false;
        }

        return infectSurfaceBlock(level, data, target);
    }

    public static boolean isEligibleSurface(ServerLevel level, BlockPos pos) {
        BlockPos topSurface = findTopEligibleSurface(level, pos.getX(), pos.getZ());
        return topSurface != null && topSurface.equals(pos);
    }

    private static BlockPos findTopEligibleSurface(ServerLevel level, int x, int z) {
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight() - 1;
        // Heightmap returns the first open block above the no-leaves surface, so step down to the surface block.
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;
        if (y < minY || y > maxY) {
            return null;
        }
        BlockPos candidate = new BlockPos(x, y, z);
        BlockState state = level.getBlockState(candidate);
        return isEligibleSurfaceBlock(state) ? candidate : null;
    }

    private static boolean isEligibleSurfaceBlock(BlockState state) {
        return !state.isAir()
                && state.getFluidState().isEmpty()
                && !state.is(ModBlocks.ALIEN_CORE.get());
    }

    private static void seedInitialInfectionIfNeeded(ServerLevel level, TakeoverSavedData data) {
        if (data.getState() != TakeoverLifecycleState.ACTIVE || data.getCorePos() == null || data.getInfectedSurfaceBlockCount() > 0) {
            return;
        }

        BlockPos corePos = data.getCorePos();
        for (int[] offset : INITIAL_SEED_OFFSETS) {
            BlockPos candidate = findTopEligibleSurface(level, corePos.getX() + offset[0], corePos.getZ() + offset[1]);
            if (candidate == null) {
                continue;
            }
            if (!data.hasGeneratedChunk(new ChunkPos(candidate))) {
                continue;
            }
            if (infectSurfaceBlock(level, data, candidate)) {
                return;
            }
        }
    }

    private static boolean infectSurfaceBlock(ServerLevel level, TakeoverSavedData data, BlockPos pos) {
        if (data.hasInfectedSurfaceBlock(pos)) {
            return false;
        }

        data.addInfectedSurfaceBlock(pos);
        applyVisualInfection(level, pos);
        ChunkPos chunkPos = new ChunkPos(pos);
        // Recompute once if this chunk has no baseline counters yet; otherwise apply incremental update.
        if (data.getEligibleSurfaceCount(chunkPos) <= 0) {
            recalculateChunkSurface(level, data, chunkPos);
        } else {
            data.setInfectedSurfaceCount(chunkPos, data.getInfectedSurfaceCount(chunkPos) + 1);
        }
        BiomeConversionService.applyChunkThresholdCheck(level, data, chunkPos, Config.TAKEOVER_CHUNK_BIOME_FLIP_THRESHOLD.get());
        return true;
    }

    private static void applyVisualInfection(ServerLevel level, BlockPos pos) {
        if (!level.getBlockState(pos).is(ModBlocks.BREEM_GRASS.get())) {
            // Flag 3 = notify neighbors (1) + send block update to clients (2).
            level.setBlock(pos, ModBlocks.BREEM_GRASS.get().defaultBlockState(), 3);
        }
    }
}
