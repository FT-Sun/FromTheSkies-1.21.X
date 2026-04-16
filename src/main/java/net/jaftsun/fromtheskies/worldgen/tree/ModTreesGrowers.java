package net.jaftsun.fromtheskies.worldgen.tree;

import net.jaftsun.fromtheskies.FromTheSkies;
import net.jaftsun.fromtheskies.worldgen.ModConfiguredFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class ModTreesGrowers {
    public static final TreeGrower BREEMLOG = new TreeGrower(FromTheSkies.MOD_ID + ":breem_log", Optional.empty(), Optional.of(ModConfiguredFeatures.BREEMLOG_KEY), Optional.empty());

}
