package net.jaftsun.fromtheskies.item;

import net.jaftsun.fromtheskies.util.ModTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

public class ModToolTiers {
  public static final Tier SAMPLE = new SimpleTier(ModTags.Blocks.INCORRECT_FOR_SAMPLE_TOOL,
      1400, 4f, 3f, 13, () -> Ingredient.of(Items.DIAMOND));
}
