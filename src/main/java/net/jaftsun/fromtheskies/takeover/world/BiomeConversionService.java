package net.jaftsun.fromtheskies.takeover.world;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public class BiomeConversionService {
    public ResourceKey<Biome> getTargetBiomeKey() {
        return TakeoverBiomes.ALIEN_OVERGROWTH;
    }

    public void applyChunkThresholdCheck() {
        // Step 10: threshold-based biome conversion logic.
    }
}
