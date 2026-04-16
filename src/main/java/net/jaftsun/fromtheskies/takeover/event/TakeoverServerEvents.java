package net.jaftsun.fromtheskies.takeover.event;

import net.jaftsun.fromtheskies.FromTheSkies;
import net.jaftsun.fromtheskies.takeover.command.TakeoverCommands;
import net.jaftsun.fromtheskies.takeover.data.TakeoverSavedData;
import net.jaftsun.fromtheskies.takeover.world.GeneratedChunkIndexService;
import net.jaftsun.fromtheskies.takeover.world.MeteorSchedulerService;
import net.jaftsun.fromtheskies.takeover.world.SurfaceSpreadService;
import net.jaftsun.fromtheskies.takeover.world.TakeoverCoreService;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = FromTheSkies.MOD_ID)
public final class TakeoverServerEvents {
    private TakeoverServerEvents() {
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        // Chunk load events are the source of truth for the generated chunk index.
        GeneratedChunkIndexService.recordGeneratedChunkOnLoad(level, event.getChunk().getPos());
    }

    @SubscribeEvent
    public static void onLevelTickPost(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        // Order matters: scheduling may activate takeover, core placement materializes it, spread advances it.
        MeteorSchedulerService.tick(level);
        TakeoverCoreService.placeCoreIfNeeded(level, TakeoverSavedData.get(level));
        SurfaceSpreadService.tick(level);
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        TakeoverCoreService.onCoreBroken(level, event.getPos());
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        TakeoverCommands.register(event.getDispatcher());
    }
}
