package net.jaftsun.fromtheskies.datagen;

import net.jaftsun.fromtheskies.registry.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.ALIEN_CORE.get());
        dropSelf(ModBlocks.BREEM_DIRT.get());
        dropSelf(ModBlocks.BREEM_GRASS.get());
        dropSelf(ModBlocks.BREEM_LOG.get());
        dropSelf(ModBlocks.BREEM_LEAF.get());

//        add(ModBlocks.SAMPLE_ORE.get(),
//                block -> createOreDrop(ModBlocks.SAMPLE_ORE.get(), ModItems.RAW_SAMPLE.get()));
    }


    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
