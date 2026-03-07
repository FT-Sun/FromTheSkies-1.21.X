package net.jaftsun.fromtheskies.util;

import net.jaftsun.fromtheskies.FromTheSkies;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    // Make to add resources/data/block/tag_name.json file with things that should have that tag
    public static class Blocks {

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(FromTheSkies.MOD_ID, name));
        }
    }

    // Make to add resources/data/item/tag_name.json file with things that should have that tag
    public static class Items {
        public static final TagKey<Item> SAMPLE_TAGGED_ITEM = createTag("sample_tagged_item");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(FromTheSkies.MOD_ID, name));
        }
    }
}
