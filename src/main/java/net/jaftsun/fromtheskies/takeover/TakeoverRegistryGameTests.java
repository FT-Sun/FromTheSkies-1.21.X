package net.jaftsun.fromtheskies.takeover;

import net.jaftsun.fromtheskies.FromTheSkies;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(FromTheSkies.MOD_ID)
@PrefixGameTestTemplate(false)
public final class TakeoverRegistryGameTests {
  private static final ResourceLocation ALIEN_CORE_ID = ResourceLocation.fromNamespaceAndPath(
      FromTheSkies.MOD_ID,
      "alien_core");
  private static final ResourceLocation ALIEN_OVERGROWTH_ID = ResourceLocation.fromNamespaceAndPath(
      FromTheSkies.MOD_ID,
      "alien_overgrowth");

  private TakeoverRegistryGameTests() {
  }

  @GameTest(template = "empty")
  public static void registry_objects_present(GameTestHelper helper) {
    boolean hasAlienCore = BuiltInRegistries.BLOCK.containsKey(ALIEN_CORE_ID);
    ResourceKey<Biome> alienBiomeKey = ResourceKey.create(Registries.BIOME, ALIEN_OVERGROWTH_ID);
    boolean hasAlienBiome = helper.getLevel().registryAccess().registryOrThrow(Registries.BIOME)
        .containsKey(alienBiomeKey);

    helper.assertTrue(hasAlienCore, "Expected block registry to contain fromtheskiesmcmod:alien_core");
    helper.assertTrue(hasAlienBiome, "Expected biome registry to contain fromtheskiesmcmod:alien_overgrowth");
    var alienCore = BuiltInRegistries.BLOCK.get(ALIEN_CORE_ID);
    helper.setBlock(1, 1, 1, alienCore);
    helper.assertBlockPresent(alienCore, 1, 1, 1);
    helper.succeed();
  }
}
