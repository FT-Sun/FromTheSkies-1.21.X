package net.jaftsun.fromtheskies.takeover.world;

import net.jaftsun.fromtheskies.FromTheSkies;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public final class TakeoverBiomes {
    // Registry key used by conversion logic and validation tests.
    public static final ResourceKey<Biome> ALIEN_OVERGROWTH = ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(FromTheSkies.MOD_ID, "alien_overgrowth"));

    private TakeoverBiomes() {
    }
}
