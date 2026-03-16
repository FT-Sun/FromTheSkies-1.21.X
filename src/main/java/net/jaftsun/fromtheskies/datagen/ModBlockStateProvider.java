package net.jaftsun.fromtheskies.datagen;

import net.jaftsun.fromtheskies.FromTheSkies;
import net.jaftsun.fromtheskies.registry.ModBlocks;
import net.minecraft.data.PackOutput;
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

    }

    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
}
