package net.jaftsun.fromtheskies.item;

import net.jaftsun.fromtheskies.FromTheSkies;
import net.jaftsun.fromtheskies.registry.ModBlocks;
import net.jaftsun.fromtheskies.registry.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FromTheSkies.MOD_ID);

    public static final Supplier<CreativeModeTab> FROMTHESKIES_TOOLS_TAB = CREATIVE_MODE_TAB.register("fromtheskies_tools_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.SAMPLE_SWORD.get()))
                    .title(Component.translatable("creativetab.fromtheskiesmcmod.fromtheskies_tools"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.SAMPLE_SWORD.get());
                        output.accept(ModItems.SAMPLE_PICKAXE.get());
                        output.accept(ModItems.SAMPLE_AXE.get());
                        output.accept(ModItems.SAMPLE_SHOVEL.get());
                        output.accept(ModItems.SAMPLE_HOE.get());

                        output.accept(ModItems.GECKO_SPAWN_EGG.get());
                    }).build());

    public static final Supplier<CreativeModeTab> FROMTHESKIES_BLOCKS_TAB = CREATIVE_MODE_TAB.register("fromtheskies_blocks_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.ALIEN_CORE.get()))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(FromTheSkies.MOD_ID, "fromtheskies_tools_tab"))
                    .title(Component.translatable("creativetab.fromtheskiesmcmod.fromtheskies_blocks"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModBlocks.ALIEN_CORE.get());
                    }).build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
