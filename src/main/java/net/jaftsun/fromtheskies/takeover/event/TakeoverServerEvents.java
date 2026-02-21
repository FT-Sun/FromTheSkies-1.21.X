package net.jaftsun.fromtheskies.takeover.event;

import net.jaftsun.fromtheskies.takeover.world.GeneratedChunkIndexService;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkEvent;

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
    }

    private static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        GeneratedChunkIndexService.recordGeneratedChunkOnLoad(level, event.getChunk().getPos());
    }
}
