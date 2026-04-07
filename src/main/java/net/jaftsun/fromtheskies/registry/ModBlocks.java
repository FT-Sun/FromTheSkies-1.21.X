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
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(5.0F, 12.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> BREEM_GRASS = registerBlock("breem_grass",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .strength(4.0F, 12.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.GRASS)));

    public static final DeferredBlock<Block> BREEM_DIRT = registerBlock("breem_dirt",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIRT)
                    .strength(4.0F, 12.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.GRAVEL)));

    public static final DeferredBlock<Block> BREEM_LOG = registerBlock("breem_log",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(5.0F, 12.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.WOOD)));

    public static final DeferredBlock<Block> BREEM_LEAF = registerBlock("breem_leaf",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .strength(3.0F, 12.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.GRASS)));


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
