package net.jaftsun.fromtheskies.worldgen;

import net.jaftsun.fromtheskies.FromTheSkies;
import net.jaftsun.fromtheskies.takeover.world.TakeoverBiomes;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModBiomeModifiers {

    public static final ResourceKey<BiomeModifier> ADD_TREE_BREEMLOG = registerKey("add_tree_breemlog");

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        context.register(ADD_TREE_BREEMLOG, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(TakeoverBiomes.ALIEN_OVERGROWTH)),
                        HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.BREEMLOG_PLACED_KEY)),
                        GenerationStep.Decoration.VEGETAL_DECORATION));



    }

    public static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(
                NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(FromTheSkies.MOD_ID, name));
    }
}
