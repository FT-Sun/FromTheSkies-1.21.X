package net.jaftsun.fromtheskies.takeover.event;

import net.jaftsun.fromtheskies.takeover.world.GeneratedChunkIndexService;
import net.jaftsun.fromtheskies.takeover.world.MeteorSchedulerService;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class TakeoverServerEvents {
    private static boolean registered;

    private TakeoverServerEvents() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        NeoForge.EVENT_BUS.addListener(TakeoverServerEvents::onChunkLoad);
        NeoForge.EVENT_BUS.addListener(TakeoverServerEvents::onLevelTickPost);
    }

    private static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        GeneratedChunkIndexService.recordGeneratedChunkOnLoad(level, event.getChunk().getPos());
    }

    private static void onLevelTickPost(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        MeteorSchedulerService.tick(level);
    }
}
