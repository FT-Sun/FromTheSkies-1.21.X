package net.jaftsun.fromtheskies.takeover.world;

import java.util.ArrayList;
import java.util.List;

import net.jaftsun.fromtheskies.Config;
import net.jaftsun.fromtheskies.takeover.TakeoverLifecycleState;
import net.jaftsun.fromtheskies.takeover.data.TakeoverSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;

public final class MeteorSchedulerService {
    private MeteorSchedulerService() {
    }

    public static TickResult tick(ServerLevel level) {
        if (!Config.TAKEOVER_ENABLED.get() || !isDimensionAllowed(level)) {
            return TickResult.NONE;
        }
        return tick(level, TakeoverSavedData.get(level), SchedulerSettings.fromConfig(), level.random, level.getGameTime());
    }

    public static TickResult tick(
            ServerLevel level,
            TakeoverSavedData data,
            SchedulerSettings settings,
            RandomSource random,
            long gameTime) {
        if (data.isTakeoverLocked()) {
            return TickResult.NONE;
        }

        boolean armedThisTick = false;
        if (data.getState() == TakeoverLifecycleState.DORMANT) {
            if (data.getGeneratedChunkCount() < settings.minGeneratedChunksSeen()) {
                return TickResult.NONE;
            }
            armedThisTick = true;
            data.setState(TakeoverLifecycleState.ARMED);
            data.setArmedAtGameTime(gameTime);
            data.setSchedulerRetryTicksRemaining(0);
            data.setScheduledMeteorGameTime(gameTime + randomDelay(settings, random));
        }

        if (data.getState() != TakeoverLifecycleState.ARMED) {
            return armedThisTick ? new TickResult(true, false, null) : TickResult.NONE;
        }

        if (data.getScheduledMeteorGameTime() > gameTime) {
            return armedThisTick ? new TickResult(true, false, null) : TickResult.NONE;
        }

        ChunkPos targetChunk = pickMeteorTarget(data, random);
        if (targetChunk == null) {
            data.setSchedulerRetryTicksRemaining(settings.retryTicks());
            data.setScheduledMeteorGameTime(gameTime + settings.retryTicks());
            return armedThisTick ? new TickResult(true, false, null) : TickResult.NONE;
        }

        BlockPos landingPos = findLandingPos(level, targetChunk);
        data.setCorePos(landingPos);
        data.setTakeoverLocked(true);
        data.setState(TakeoverLifecycleState.ACTIVE);
        data.setScheduledMeteorGameTime(-1L);
        data.setSchedulerRetryTicksRemaining(0);
        return new TickResult(armedThisTick, true, targetChunk);
    }

    private static boolean isDimensionAllowed(ServerLevel level) {
        String dimensionId = level.dimension().location().toString();
        List<? extends String> allowedDimensions = Config.TAKEOVER_ALLOWED_DIMENSIONS.get();
        for (String allowed : allowedDimensions) {
            if (dimensionId.equals(allowed)) {
                return true;
            }
        }
        return false;
    }

    private static long randomDelay(SchedulerSettings settings, RandomSource random) {
        int min = settings.meteorMinTicks();
        int max = settings.meteorMaxTicks();
        if (max <= min) {
            return min;
        }
        int bound = (max - min) + 1;
        return min + random.nextInt(bound);
    }

    private static ChunkPos pickMeteorTarget(TakeoverSavedData data, RandomSource random) {
        List<ChunkPos> generatedChunks = new ArrayList<>(data.getGeneratedChunks());
        if (generatedChunks.isEmpty()) {
            return null;
        }
        return generatedChunks.get(random.nextInt(generatedChunks.size()));
    }

    private static BlockPos findLandingPos(ServerLevel level, ChunkPos chunkPos) {
        int x = chunkPos.getMiddleBlockX();
        int z = chunkPos.getMiddleBlockZ();
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        if (y < level.getMinBuildHeight()) {
            y = level.getMinBuildHeight();
        }
        return new BlockPos(x, y, z);
    }

    public record TickResult(boolean armedThisTick, boolean triggeredThisTick, ChunkPos targetChunk) {
        public static final TickResult NONE = new TickResult(false, false, null);
    }

    public record SchedulerSettings(int minGeneratedChunksSeen, int meteorMinTicks, int meteorMaxTicks, int retryTicks) {
        public SchedulerSettings {
            if (minGeneratedChunksSeen < 1) {
                throw new IllegalArgumentException("minGeneratedChunksSeen must be >= 1");
            }
            if (meteorMinTicks < 0) {
                throw new IllegalArgumentException("meteorMinTicks must be >= 0");
            }
            if (meteorMaxTicks < meteorMinTicks) {
                throw new IllegalArgumentException("meteorMaxTicks must be >= meteorMinTicks");
            }
            if (retryTicks < 1) {
                throw new IllegalArgumentException("retryTicks must be >= 1");
            }
        }

        public static SchedulerSettings fromConfig() {
            return new SchedulerSettings(
                    Config.TAKEOVER_MIN_GENERATED_CHUNKS_SEEN.get(),
                    Config.TAKEOVER_METEOR_MIN_TICKS.get(),
                    Config.TAKEOVER_METEOR_MAX_TICKS.get(),
                    Config.TAKEOVER_RETRY_TICKS.get());
        }
    }
}
