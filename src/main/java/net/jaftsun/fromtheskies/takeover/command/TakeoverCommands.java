package net.jaftsun.fromtheskies.takeover.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.jaftsun.fromtheskies.Config;
import net.jaftsun.fromtheskies.takeover.TakeoverLifecycleState;
import net.jaftsun.fromtheskies.takeover.data.TakeoverSavedData;
import net.jaftsun.fromtheskies.takeover.world.MeteorSchedulerService;
import net.jaftsun.fromtheskies.takeover.world.SurfaceSpreadService;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

public final class TakeoverCommands {
    private TakeoverCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // Root admin command tree for inspecting and driving takeover state transitions.
        dispatcher.register(Commands.literal("fts")
                // Permission level 2 means operators/command sources with admin-level access.
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("takeover")
                        // ::status is a method reference shorthand for (ctx) -> status(ctx).
                        .then(Commands.literal("status").executes(TakeoverCommands::status))
                        .then(Commands.literal("force_arm").executes(context -> executeWithLevel(context, TakeoverCommands::forceArm)))
                        .then(Commands.literal("force_meteor")
                                .executes(context -> executeWithLevel(context, TakeoverCommands::forceMeteor)))
                        .then(Commands.literal("step")
                                .then(Commands.argument("ticks", IntegerArgumentType.integer(1, 10_000))
                                        .executes(context -> executeWithLevel(context, (level, data) -> stepSpread(
                                                level,
                                                data,
                                                IntegerArgumentType.getInteger(context, "ticks"))))))
                        .then(Commands.literal("stop").executes(context -> executeWithLevel(context, TakeoverCommands::forceStop)))));
    }

    public static int forceArm(ServerLevel level, TakeoverSavedData data) {
        if (data.isTakeoverLocked()) {
            return 0;
        }
        long now = level.getGameTime();
        data.setState(TakeoverLifecycleState.ARMED);
        data.setArmedAtGameTime(now);
        data.setScheduledMeteorGameTime(now);
        data.setSchedulerRetryTicksRemaining(0);
        return 1;
    }

    public static int forceMeteor(ServerLevel level, TakeoverSavedData data) {
        if (data.isTakeoverLocked() && data.getState() != TakeoverLifecycleState.ACTIVE) {
            return 0;
        }
        if (data.getState() == TakeoverLifecycleState.DORMANT) {
            forceArm(level, data);
        }
        data.setScheduledMeteorGameTime(level.getGameTime());
        MeteorSchedulerService.TickResult result = MeteorSchedulerService.tick(
                level,
                data,
                new MeteorSchedulerService.SchedulerSettings(1, 0, 0, 1),
                level.random,
                level.getGameTime());
        return result.triggeredThisTick() ? 1 : 0;
    }

    public static int stepSpread(ServerLevel level, TakeoverSavedData data, int ticks) {
        // Simulate deterministic tick progression for command/debug usage.
        long start = Math.max(level.getGameTime(), data.getLastSpreadTickGameTime() + 1L);
        int totalSpreads = 0;
        for (int i = 0; i < ticks; i++) {
            totalSpreads += SurfaceSpreadService.tick(
                    level,
                    data,
                    1,
                    Config.TAKEOVER_SPREAD_ATTEMPTS_PER_TICK.get(),
                    level.random,
                    start + i);
        }
        return totalSpreads;
    }

    public static int forceStop(ServerLevel level, TakeoverSavedData data) {
        if (data.getState() == TakeoverLifecycleState.STOPPED) {
            return 0;
        }
        data.setState(TakeoverLifecycleState.STOPPED);
        return 1;
    }

    private static int status(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!(source.getLevel() instanceof ServerLevel level)) {
            source.sendFailure(Component.literal("Takeover commands are server-level only."));
            return 0;
        }
        TakeoverSavedData data = TakeoverSavedData.get(level);
        source.sendSuccess(() -> Component.literal(
                "state="
                        + data.getState()
                        + ", locked="
                        + data.isTakeoverLocked()
                        + ", core="
                        + formatCorePos(data)
                        + ", generated="
                        + data.getGeneratedChunkCount()
                        + ", infected="
                        + data.getInfectedSurfaceBlockCount()
                        + ", blockedFrontier="
                        + data.getBlockedFrontierEdgeCount()
                        + ", converted="
                        + data.getConvertedChunkCount()),
                false);
        return 1;
    }

    private static int executeWithLevel(
            CommandContext<CommandSourceStack> context,
            LevelCommandAction action) {
        // Shared command wrapper: resolve level/data once and emit a uniform status line.
        CommandSourceStack source = context.getSource();
        if (!(source.getLevel() instanceof ServerLevel level)) {
            source.sendFailure(Component.literal("Takeover commands are server-level only."));
            return 0;
        }
        TakeoverSavedData data = TakeoverSavedData.get(level);
        int result = action.run(level, data);
        source.sendSuccess(() -> Component.literal("result=" + result + ", state=" + data.getState()), false);
        return result;
    }

    private static String formatCorePos(TakeoverSavedData data) {
        if (data.getCorePos() == null) {
            return "none";
        }
        return data.getCorePos().getX() + "," + data.getCorePos().getY() + "," + data.getCorePos().getZ();
    }

    @FunctionalInterface
    private interface LevelCommandAction {
        // Single-method callback interface so lambdas/method refs can be passed to executeWithLevel.
        int run(ServerLevel level, TakeoverSavedData data);
    }
}
