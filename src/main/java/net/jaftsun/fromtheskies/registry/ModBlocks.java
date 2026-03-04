package net.jaftsun.fromtheskies.registry;

import net.jaftsun.fromtheskies.FromTheSkies;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(FromTheSkies.MOD_ID);

    // ModBlock registrations
//    public static final DeferredBlock<Block> ALIEN_CORE = BLOCKS.registerBlock(
//            "alien_core",
//            Block::new,
//            BlockBehaviour.Properties.of()
//                    .mapColor(MapColor.COLOR_GREEN)
//                    .strength(5.0F, 12.0F)
//                    .requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> ALIEN_CORE = registerBlock("alien_core",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(5.0F, 12.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.SCULK)));

    private ModBlocks() {
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block){
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }


    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
