package net.jaftsun.fromtheskies.datagen;

import net.jaftsun.fromtheskies.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.common.Mod;

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
        dropSelf(ModBlocks.BREEM_STRIPPEDLOG.get());
        dropSelf(ModBlocks.BREEM_TREESAPLING.get());

        this.add(ModBlocks.BREEM_LEAF.get(), block ->
                createLeavesDrops(block, ModBlocks.BREEM_TREESAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));

    }


    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
