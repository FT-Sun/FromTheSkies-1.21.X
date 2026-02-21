package net.jaftsun.fromtheskies.takeover.world;

import net.jaftsun.fromtheskies.takeover.data.TakeoverSavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.ChunkPos;

public final class BiomeConversionService {
    private BiomeConversionService() {
    }

    public static ResourceKey<Biome> getTargetBiomeKey() {
        return TakeoverBiomes.ALIEN_OVERGROWTH;
    }

    public static boolean applyChunkThresholdCheck(
            ServerLevel level,
            TakeoverSavedData data,
            ChunkPos chunkPos,
            double threshold) {
        if (data.hasConvertedChunk(chunkPos)) {
            return false;
        }
        int eligible = data.getEligibleSurfaceCount(chunkPos);
        if (eligible <= 0) {
            return false;
        }
        int infected = data.getInfectedSurfaceCount(chunkPos);
        double infectedRatio = (double) infected / (double) eligible;
        if (infectedRatio + 1.0E-9D < threshold) {
            return false;
        }

        data.addConvertedChunk(chunkPos);
        applyBiomeToChunk(level, chunkPos);
        return true;
    }

    private static void applyBiomeToChunk(ServerLevel level, ChunkPos chunkPos) {
        // True biome mutation for all chunk quart positions can be layered in once
        // chunk-biome write APIs are finalized for this mod's runtime path.
        level.getChunk(chunkPos.x, chunkPos.z);
    }
}
