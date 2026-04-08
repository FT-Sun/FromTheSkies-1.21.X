package net.jaftsun.fromtheskies.datagen;

import net.jaftsun.fromtheskies.FromTheSkies;
import net.jaftsun.fromtheskies.registry.ModItems;
import net.minecraft.data.PackOutput;
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
        spawnEggItem(ModItems.BREEM_VILLAGER_SPAWN_EGG);
        spawnEggItem(ModItems.BREEM_SOLDIER_SPAWN_EGG);
        spawnEggItem(ModItems.BREEM_BRUTE_SPAWN_EGG);
        spawnEggItem(ModItems.BREEM_SHAMAN_SPAWN_EGG);
    }

    private ItemModelBuilder spawnEggItem(DeferredItem<?> item) {
        return withExistingParent(item.getId().getPath(), mcLoc("item/template_spawn_egg"));
    }
}
