package net.jaftsun.fromtheskies.takeover.world;

import net.jaftsun.fromtheskies.takeover.data.TakeoverSavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public final class GeneratedChunkIndexService {
    private GeneratedChunkIndexService() {
    }

    public static boolean recordGeneratedChunkOnLoad(ServerLevel level, ChunkPos chunkPos) {
        if (!Level.OVERWORLD.equals(level.dimension())) {
            return false;
        }

        TakeoverSavedData data = TakeoverSavedData.get(level);
        if (data.hasGeneratedChunk(chunkPos)) {
            return false;
        }

        data.addGeneratedChunk(chunkPos);
        return true;
    }
}
