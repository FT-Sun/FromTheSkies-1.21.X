package net.jaftsun.fromtheskies.registry;

import net.jaftsun.fromtheskies.FromTheSkies;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(FromTheSkies.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FromTheSkies.MOD_ID);

    public static final DeferredBlock<Block> ALIEN_CORE = BLOCKS.registerBlock(
            "alien_core",
            Block::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(5.0F, 12.0F)
                    .requiresCorrectToolForDrops());

    public static final DeferredItem<BlockItem> ALIEN_CORE_ITEM = ITEMS.registerSimpleBlockItem("alien_core", ALIEN_CORE);

    private ModBlocks() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
    }
}
