package net.jaftsun.fromtheskies;

import net.jaftsun.fromtheskies.entity.ModEntities;
import net.jaftsun.fromtheskies.entity.client.*;
import net.jaftsun.fromtheskies.item.ModCreativeModeTabs;
import net.jaftsun.fromtheskies.registry.ModItems;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.jaftsun.fromtheskies.registry.ModBlocks;
import net.jaftsun.fromtheskies.takeover.TakeoverRegistryGameTests;
import net.jaftsun.fromtheskies.takeover.event.TakeoverServerEvents;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(FromTheSkies.MOD_ID)
public class FromTheSkies {
  // Define mod id in a common place for everything to reference
  public static final String MOD_ID = "fromtheskiesmcmod";
  // Directly reference a slf4j logger
  public static final Logger LOGGER = LogUtils.getLogger();

  // The constructor for the mod class is the first code that is run when your mod
  // is loaded.
  // FML will recognize some parameter types like IEventBus or ModContainer and
  // pass them in automatically.
  public FromTheSkies(IEventBus modEventBus, ModContainer modContainer) {
    ModBlocks.register(modEventBus);
    ModItems.register(modEventBus);
    ModEntities.register(modEventBus);

    // Register the commonSetup method for modloading
    modEventBus.addListener(this::commonSetup);

    ModCreativeModeTabs.register(modEventBus);

    // Register the item to a creative tab
    modEventBus.addListener(this::addCreative);
    modEventBus.addListener(FromTheSkies::registerGameTests);
    modEventBus.addListener(ClientModEvents::registerItemColors);

    // Register our mod's ModConfigSpec so that FML can create and load the config
    // file for us
    modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
  }

  private void commonSetup(FMLCommonSetupEvent event) {

  }

  private void addCreative(BuildCreativeModeTabContentsEvent event) {
    if (event.getTabKey().equals(CreativeModeTabs.BUILDING_BLOCKS)) {
      event.accept(ModBlocks.ALIEN_CORE);
      event.accept(ModBlocks.BREEM_GRASS);
      event.accept(ModBlocks.BREEM_DIRT);
      event.accept(ModBlocks.BREEM_LOG);
      event.accept(ModBlocks.BREEM_LEAF);
    }

    if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
      event.accept(ModItems.SAMPLE_PICKAXE);
      event.accept(ModItems.SAMPLE_AXE);
      event.accept(ModItems.SAMPLE_SHOVEL);
      event.accept(ModItems.SAMPLE_HOE);
    }

    if (event.getTabKey().equals(CreativeModeTabs.COMBAT)) {
      event.accept(ModItems.SAMPLE_SWORD);
    }

    if (event.getTabKey().equals(CreativeModeTabs.SPAWN_EGGS)) {
      event.accept(ModItems.BREEM_VILLAGER_SPAWN_EGG);
      event.accept(ModItems.BREEM_SOLDIER_SPAWN_EGG);
      event.accept(ModItems.BREEM_BRUTE_SPAWN_EGG);
      event.accept(ModItems.BREEM_SHAMAN_SPAWN_EGG);
    }
  }

  // You can use SubscribeEvent and let the Event Bus discover methods to call
  @SubscribeEvent
  public void onServerStarting(ServerStartingEvent event) {

  }

  // You can use EventBusSubscriber to automatically register all static methods
  // in the class annotated with @SubscribeEvent
  @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
  public static class ClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
      EntityRenderers.register(ModEntities.BREEM.get(), BreemRenderer::new);
      ItemBlockRenderTypes.setRenderLayer(ModBlocks.BREEM_LEAF.get(), RenderType.cutout());
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
      event.registerLayerDefinition(BreemModel.LAYER_LOCATION, BreemModel::createBodyLayer);
      event.registerLayerDefinition(BreemSoldierModel.LAYER_LOCATION, BreemSoldierModel::createBodyLayer);
      event.registerLayerDefinition(BreemBruteModel.LAYER_LOCATION, BreemBruteModel::createBodyLayer);
    }

    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
      event.register((stack, tintIndex) -> FastColor.ARGB32.opaque(((SpawnEggItem) stack.getItem()).getColor(tintIndex)),
              ModItems.BREEM_VILLAGER_SPAWN_EGG.get(),
              ModItems.BREEM_SOLDIER_SPAWN_EGG.get(),
              ModItems.BREEM_BRUTE_SPAWN_EGG.get(),
              ModItems.BREEM_SHAMAN_SPAWN_EGG.get());
    }
  }

  private static void registerGameTests(RegisterGameTestsEvent event) {
    event.register(TakeoverRegistryGameTests.class);
  }
}
