package net.jaftsun.fromtheskies.takeover.world;

import net.jaftsun.fromtheskies.registry.ModBlocks;
import net.jaftsun.fromtheskies.takeover.TakeoverLifecycleState;
import net.jaftsun.fromtheskies.takeover.data.TakeoverSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Handles alien core placement and core-destruction lifecycle transitions.
 */
public final class TakeoverCoreService {
    private TakeoverCoreService() {
    }

    public static boolean placeCoreIfNeeded(ServerLevel level, TakeoverSavedData data) {
        // Core exists only while ACTIVE and only at the scheduler-selected landing position.
        if (data.getState() != TakeoverLifecycleState.ACTIVE || data.getCorePos() == null) {
            return false;
        }
        if (level.getBlockState(data.getCorePos()).is(ModBlocks.ALIEN_CORE.get())) {
            return false;
        }
        // Flag 3 = notify neighbors and sync block change to clients immediately.
        return level.setBlock(data.getCorePos(), ModBlocks.ALIEN_CORE.get().defaultBlockState(), 3);
    }

    public static boolean onCoreBroken(ServerLevel level, BlockPos brokenPos) {
        return onCoreBroken(level, TakeoverSavedData.get(level), brokenPos);
    }

    public static boolean onCoreBroken(ServerLevel level, TakeoverSavedData data, BlockPos brokenPos) {
        // Breaking the tracked core halts all future spread without rolling back prior changes.
        if (data.getState() != TakeoverLifecycleState.ACTIVE || data.getCorePos() == null) {
            return false;
        }
        if (!data.getCorePos().equals(brokenPos)) {
            return false;
        }
        data.setState(TakeoverLifecycleState.STOPPED);
        return true;
    }
}
