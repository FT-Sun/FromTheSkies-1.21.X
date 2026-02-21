package net.jaftsun.fromtheskies.takeover.world;

import java.util.ArrayList;
import java.util.List;

import net.jaftsun.fromtheskies.Config;
import net.jaftsun.fromtheskies.registry.ModBlocks;
import net.jaftsun.fromtheskies.takeover.TakeoverLifecycleState;
import net.jaftsun.fromtheskies.takeover.data.TakeoverSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class SurfaceSpreadService {
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
        if (data.getState() != TakeoverLifecycleState.ACTIVE) {
            return 0;
        }
        long lastTick = data.getLastSpreadTickGameTime();
        if (lastTick >= 0 && (gameTime - lastTick) < Math.max(1, spreadIntervalTicks)) {
            return 0;
        }
        data.setLastSpreadTickGameTime(gameTime);
        seedInitialInfectionIfNeeded(level, data);
        int successfulSpreads = 0;
        for (int attempt = 0; attempt < Math.max(1, spreadAttemptsPerTick); attempt++) {
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
        if (data.getState() != TakeoverLifecycleState.ACTIVE) {
            return false;
        }
        if (requireSourceInfected && !data.hasInfectedSurfaceBlock(source)) {
            return false;
        }
        if (Math.abs(offsetX) + Math.abs(offsetZ) != 1) {
            return false;
        }
        BlockPos target = findTopEligibleSurface(level, source.getX() + offsetX, source.getZ() + offsetZ);
        if (target == null || data.hasInfectedSurfaceBlock(target)) {
            return false;
        }
        ChunkPos sourceChunk = new ChunkPos(source);
        ChunkPos targetChunk = new ChunkPos(target);
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
        if (data.getEligibleSurfaceCount(chunkPos) <= 0) {
            recalculateChunkSurface(level, data, chunkPos);
        } else {
            data.setInfectedSurfaceCount(chunkPos, data.getInfectedSurfaceCount(chunkPos) + 1);
        }
        BiomeConversionService.applyChunkThresholdCheck(level, data, chunkPos, Config.TAKEOVER_CHUNK_BIOME_FLIP_THRESHOLD.get());
        return true;
    }

    private static void applyVisualInfection(ServerLevel level, BlockPos pos) {
        if (!level.getBlockState(pos).is(Blocks.SCULK)) {
            level.setBlock(pos, Blocks.SCULK.defaultBlockState(), 3);
        }
    }
}
