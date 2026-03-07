package net.jaftsun.fromtheskies.datagen;

import net.jaftsun.fromtheskies.FromTheSkies;
import net.jaftsun.fromtheskies.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, FromTheSkies.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
//       basicItem(ModItems.SAMPLE_ITEM.get());

       handheldItem(ModItems.SAMPLE_SWORD);
       handheldItem(ModItems.SAMPLE_PICKAXE);
       handheldItem(ModItems.SAMPLE_AXE);
       handheldItem(ModItems.SAMPLE_SHOVEL);
       handheldItem(ModItems.SAMPLE_HOE);
    }

    private ItemModelBuilder handheldItem(DeferredItem<?> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/handheld")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(FromTheSkies.MOD_ID, "item/" + item.getId().getPath()));
    }
}
