package net.jaftsun.fromtheskies.takeover.world;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import net.jaftsun.fromtheskies.FromTheSkies;
import net.jaftsun.fromtheskies.takeover.data.TakeoverSavedData;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * Chunk-level biome conversion gate keyed off infected/eligible surface ratios.
 */
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
        Holder<Biome> targetBiome = level.registryAccess()
                .registryOrThrow(Registries.BIOME)
                .getHolderOrThrow(getTargetBiomeKey());
        LevelChunk chunk = level.getChunk(chunkPos.x, chunkPos.z);
        chunk.fillBiomesFromNoise((quartX, quartY, quartZ, sampler) -> targetBiome, level.getChunkSource().randomState().sampler());
        chunk.setUnsaved(true);
        resendChunkBiomes(level.getChunkSource(), chunk);
    }

    private static void resendChunkBiomes(ServerChunkCache chunkSource, LevelChunk chunk) {
        try {
            Field chunkMapField = ServerChunkCache.class.getDeclaredField("chunkMap");
            chunkMapField.setAccessible(true);
            ChunkMap chunkMap = (ChunkMap) chunkMapField.get(chunkSource);
            Method resendBiomes = ChunkMap.class.getDeclaredMethod("resendBiomesForChunks", List.class);
            resendBiomes.setAccessible(true);
            resendBiomes.invoke(chunkMap, List.of(chunk));
        } catch (ReflectiveOperationException exception) {
            FromTheSkies.LOGGER.warn("Failed to resend converted chunk biomes for {}", chunk.getPos(), exception);
        }
    }
}
