package net.jaftsun.fromtheskies.takeover.world;

import net.jaftsun.fromtheskies.Config;
import net.jaftsun.fromtheskies.takeover.TakeoverLifecycleState;
import net.jaftsun.fromtheskies.takeover.data.TakeoverSavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

public final class SurfaceSpreadService {
    private SurfaceSpreadService() {
    }

    public static int tick(ServerLevel level) {
        return tick(
                level,
                TakeoverSavedData.get(level),
                Config.TAKEOVER_SPREAD_INTERVAL_TICKS.get(),
                Config.TAKEOVER_SPREAD_ATTEMPTS_PER_TICK.get(),
                level.random,
                level.getGameTime());
    }

    public static int tick(
            ServerLevel level,
            TakeoverSavedData data,
            int spreadIntervalTicks,
            int spreadAttemptsPerTick,
            RandomSource random,
            long gameTime) {
        if (data.getState() != TakeoverLifecycleState.ACTIVE) {
            return 0;
        }
        long lastTick = data.getLastSpreadTickGameTime();
        if (lastTick >= 0 && (gameTime - lastTick) < Math.max(1, spreadIntervalTicks)) {
            return 0;
        }
        data.setLastSpreadTickGameTime(gameTime);
        return 0;
    }
}
