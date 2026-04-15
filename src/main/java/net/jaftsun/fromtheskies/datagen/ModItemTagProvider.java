package net.jaftsun.fromtheskies.datagen;

import net.jaftsun.fromtheskies.FromTheSkies;
import net.jaftsun.fromtheskies.block.ModBlocks;
import net.jaftsun.fromtheskies.registry.ModItems;
import net.jaftsun.fromtheskies.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
  public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
      CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
    super(output, lookupProvider, blockTags, FromTheSkies.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    // tag(ModTags.Items.SAMPLE_TAGGED_ITEM)
    // .add(ModItems.SAMPLE_ITEM.get())
    // .add(ModItems.SAMPLE_ITEM2.get());

    tag(ItemTags.SWORDS)
        .add(ModItems.SAMPLE_SWORD.get());
    tag(ItemTags.PICKAXES)
        .add(ModItems.SAMPLE_PICKAXE.get());
    tag(ItemTags.AXES)
        .add(ModItems.SAMPLE_AXE.get());
    tag(ItemTags.SHOVELS)
        .add(ModItems.SAMPLE_SHOVEL.get());
    tag(ItemTags.HOES)
        .add(ModItems.SAMPLE_HOE.get());

    this.tag(ItemTags.LOGS_THAT_BURN)
            .add(ModBlocks.BREEM_LOG.get().asItem());
  }
}
