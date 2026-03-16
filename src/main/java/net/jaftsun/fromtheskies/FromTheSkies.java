package net.jaftsun.fromtheskies;

import net.jaftsun.fromtheskies.entity.ModEntities;
import net.jaftsun.fromtheskies.entity.client.GeckoRenderer;
import net.jaftsun.fromtheskies.item.ModCreativeModeTabs;
import net.jaftsun.fromtheskies.registry.ModItems;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.jaftsun.fromtheskies.registry.ModBlocks;
import net.jaftsun.fromtheskies.takeover.TakeoverRegistryGameTests;
import net.jaftsun.fromtheskies.takeover.event.TakeoverServerEvents;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;

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

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

  private void commonSetup(FMLCommonSetupEvent event) {
    event.enqueueWork(TakeoverServerEvents::register);
  }

  private void addCreative(BuildCreativeModeTabContentsEvent event) {
    if (event.getTabKey().equals(CreativeModeTabs.BUILDING_BLOCKS)) {
      event.accept(ModBlocks.ALIEN_CORE);
    }

    if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
        event.accept(ModItems.SAMPLE_PICKAXE);
        event.accept(ModItems.SAMPLE_AXE);
        event.accept(ModItems.SAMPLE_SHOVEL);
        event.accept(ModItems.SAMPLE_HOE);
    }

    if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
        event.accept(ModItems.SAMPLE_SWORD);
    }
  }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntities.GECKO.get(), GeckoRenderer::new);
        }
    }

  private static void registerGameTests(RegisterGameTestsEvent event) {
    event.register(TakeoverRegistryGameTests.class);
  }
}
