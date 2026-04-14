package net.jaftsun.fromtheskies.datagen;

import net.jaftsun.fromtheskies.FromTheSkies;
import net.jaftsun.fromtheskies.block.ModBlocks;
import net.jaftsun.fromtheskies.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, FromTheSkies.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

        handheldItem(ModItems.SAMPLE_SWORD);
        handheldItem(ModItems.SAMPLE_PICKAXE);
        handheldItem(ModItems.SAMPLE_AXE);
        handheldItem(ModItems.SAMPLE_SHOVEL);
        handheldItem(ModItems.SAMPLE_HOE);

        spawnEggItem(ModItems.BREEM_VILLAGER_SPAWN_EGG);
        spawnEggItem(ModItems.BREEM_SOLDIER_SPAWN_EGG);
        spawnEggItem(ModItems.BREEM_BRUTE_SPAWN_EGG);
        spawnEggItem(ModItems.BREEM_SHAMAN_SPAWN_EGG);

        saplingItem(ModBlocks.BREEM_TREESAPLING);

    }

    private ItemModelBuilder saplingItem(DeferredBlock<?> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/generated"))
                .texture("layer0",
                        ResourceLocation.fromNamespaceAndPath(FromTheSkies.MOD_ID, "block/" + item.getId().getPath()));
    }

    private ItemModelBuilder spawnEggItem(DeferredItem<?> item) {
        return withExistingParent(item.getId().getPath(), mcLoc("item/template_spawn_egg"));
    }

    private ItemModelBuilder handheldItem(DeferredItem<?> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/handheld")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(FromTheSkies.MOD_ID, "item/" + item.getId().getPath()));
}}
