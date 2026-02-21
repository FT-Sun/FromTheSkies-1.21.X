package net.jaftsun.fromtheskies.takeover.world;

import java.util.ArrayList;
import java.util.List;

import net.jaftsun.fromtheskies.Config;
import net.jaftsun.fromtheskies.takeover.TakeoverLifecycleState;
import net.jaftsun.fromtheskies.takeover.data.TakeoverSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class SurfaceSpreadService {
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
        if (data.getState() != TakeoverLifecycleState.ACTIVE) {
            return 0;
        }
        long lastTick = data.getLastSpreadTickGameTime();
        if (lastTick >= 0 && (gameTime - lastTick) < Math.max(1, spreadIntervalTicks)) {
            return 0;
        }
        data.setLastSpreadTickGameTime(gameTime);
        return 0;
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
        data.addInfectedSurfaceBlock(pos);
        ChunkPos chunkPos = new ChunkPos(pos);
        if (data.getEligibleSurfaceCount(chunkPos) <= 0) {
            recalculateChunkSurface(level, data, chunkPos);
        }
        data.setInfectedSurfaceCount(chunkPos, data.getInfectedSurfaceCount(chunkPos) + 1);
        return true;
    }

    public static boolean isEligibleSurface(ServerLevel level, BlockPos pos) {
        BlockPos topSurface = findTopEligibleSurface(level, pos.getX(), pos.getZ());
        return topSurface != null && topSurface.equals(pos);
    }

    private static BlockPos findTopEligibleSurface(ServerLevel level, int x, int z) {
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight() - 1;
        for (int y = maxY; y >= minY; y--) {
            BlockPos candidate = new BlockPos(x, y, z);
            BlockState state = level.getBlockState(candidate);
            if (state.isAir()) {
                continue;
            }
            if (isEligibleSurfaceBlock(state) && (y == maxY || level.getBlockState(candidate.above()).isAir())) {
                return candidate;
            }
            return null;
        }
        return null;
    }

    private static boolean isEligibleSurfaceBlock(BlockState state) {
        return state.is(Blocks.MOSS_BLOCK);
    }
}
