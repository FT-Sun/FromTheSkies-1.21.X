package net.jaftsun.fromtheskies.datagen;

import net.jaftsun.fromtheskies.FromTheSkies;
import net.jaftsun.fromtheskies.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, FromTheSkies.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
//        blockWithItem(ModBlocks.SAMPLE_BLOCK);
        simpleBlockWithItem(ModBlocks.ALIEN_CORE.get(), models().getExistingFile(modLoc("block/alien_core")));
        simpleBlockWithItem(ModBlocks.BREEM_DIRT.get(), models().getExistingFile(modLoc("block/breem_dirt")));
        simpleBlockWithItem(ModBlocks.BREEM_GRASS.get(), models().getExistingFile(modLoc("block/breem_grass")));
        simpleBlockWithItem(ModBlocks.BREEM_LEAF.get(), models().getExistingFile(modLoc("block/breem_leaf")));
        simpleBlockWithItem(ModBlocks.BREEM_TREESAPLING.get(), models().getExistingFile(modLoc("block/block_breem_treesappling")));

        //logBlock(((RotatedPillarBlock) ModBlocks.BREEM_LOG.get()));
        //logBlock(((RotatedPillarBlock) ModBlocks.BREEM_STRIPPEDLOG.get()));
    }

    private void saplingBlock(DeferredBlock<Block> blockRegistryObject) {
        simpleBlock(blockRegistryObject.get(),
                models().cross(
                        BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get()).getPath(),
                        blockTexture(blockRegistryObject.get())
                ).renderType("cutout"));
    }

    private void leavesBlock(DeferredBlock<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(),
                models().singleTexture(
                        BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get()).getPath(),
                        ResourceLocation.parse("minecraft:block/leaves"),
                        "all",
                        blockTexture(blockRegistryObject.get())
                ).renderType("cutout"));
    }

    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
}
