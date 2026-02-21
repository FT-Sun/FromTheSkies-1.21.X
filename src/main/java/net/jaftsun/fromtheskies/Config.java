package net.jaftsun.fromtheskies;

import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final String DEFAULT_ALLOWED_DIMENSION = Level.OVERWORLD.location().toString();
    public static final boolean DEFAULT_TAKEOVER_ENABLED = true;
    public static final int DEFAULT_MIN_GENERATED_CHUNKS_SEEN = 512;
    public static final int DEFAULT_METEOR_MIN_TICKS = 24_000;
    public static final int DEFAULT_METEOR_MAX_TICKS = 72_000;
    public static final int DEFAULT_RETRY_TICKS = 1_200;
    public static final int DEFAULT_SPREAD_INTERVAL_TICKS = 20;
    public static final int DEFAULT_SPREAD_ATTEMPTS_PER_TICK = 8;
    public static final double DEFAULT_CHUNK_BIOME_FLIP_THRESHOLD = 0.35D;
    public static final boolean DEFAULT_DEBUG_LOGGING = false;

    public static final ModConfigSpec.BooleanValue TAKEOVER_ENABLED;
    public static final ModConfigSpec.IntValue TAKEOVER_MIN_GENERATED_CHUNKS_SEEN;
    public static final ModConfigSpec.IntValue TAKEOVER_METEOR_MIN_TICKS;
    public static final ModConfigSpec.IntValue TAKEOVER_METEOR_MAX_TICKS;
    public static final ModConfigSpec.IntValue TAKEOVER_RETRY_TICKS;
    public static final ModConfigSpec.IntValue TAKEOVER_SPREAD_INTERVAL_TICKS;
    public static final ModConfigSpec.IntValue TAKEOVER_SPREAD_ATTEMPTS_PER_TICK;
    public static final ModConfigSpec.DoubleValue TAKEOVER_CHUNK_BIOME_FLIP_THRESHOLD;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> TAKEOVER_ALLOWED_DIMENSIONS;
    public static final ModConfigSpec.BooleanValue TAKEOVER_DEBUG_LOGGING;

    public static final ModConfigSpec SPEC;

    static {
        BUILDER.push("takeover");

        TAKEOVER_ENABLED = BUILDER
                .comment("Master switch for takeover lifecycle logic.")
                .define("enabled", DEFAULT_TAKEOVER_ENABLED);
        TAKEOVER_MIN_GENERATED_CHUNKS_SEEN = BUILDER
                .comment("Generated Overworld chunks required before takeover can arm.")
                .defineInRange("minGeneratedChunksSeen", DEFAULT_MIN_GENERATED_CHUNKS_SEEN, 1, Integer.MAX_VALUE);
        TAKEOVER_METEOR_MIN_TICKS = BUILDER
                .comment("Minimum ticks after arming before meteor can trigger.")
                .defineInRange("meteorMinTicks", DEFAULT_METEOR_MIN_TICKS, 1, Integer.MAX_VALUE);
        TAKEOVER_METEOR_MAX_TICKS = BUILDER
                .comment("Maximum ticks after arming before meteor must trigger. Must be >= meteorMinTicks.")
                .defineInRange("meteorMaxTicks", DEFAULT_METEOR_MAX_TICKS, 1, Integer.MAX_VALUE);
        TAKEOVER_RETRY_TICKS = BUILDER
                .comment("Retry delay when meteor scheduling/placement must reattempt.")
                .defineInRange("retryTicks", DEFAULT_RETRY_TICKS, 1, Integer.MAX_VALUE);
        TAKEOVER_SPREAD_INTERVAL_TICKS = BUILDER
                .comment("Ticks between spread engine updates.")
                .defineInRange("spreadIntervalTicks", DEFAULT_SPREAD_INTERVAL_TICKS, 1, Integer.MAX_VALUE);
        TAKEOVER_SPREAD_ATTEMPTS_PER_TICK = BUILDER
                .comment("Maximum local spread attempts per spread tick.")
                .defineInRange("spreadAttemptsPerTick", DEFAULT_SPREAD_ATTEMPTS_PER_TICK, 1, Integer.MAX_VALUE);
        TAKEOVER_CHUNK_BIOME_FLIP_THRESHOLD = BUILDER
                .comment("Infected-to-eligible ratio needed for biome conversion in a chunk.")
                .defineInRange("chunkBiomeFlipThreshold", DEFAULT_CHUNK_BIOME_FLIP_THRESHOLD, 0.0D, 1.0D);
        TAKEOVER_ALLOWED_DIMENSIONS = BUILDER
                .comment("Dimensions where takeover logic is allowed. v1 default is Overworld-only.")
                .defineListAllowEmpty(
                        "allowedDimensions",
                        List.of(DEFAULT_ALLOWED_DIMENSION),
                        () -> DEFAULT_ALLOWED_DIMENSION,
                        Config::validateDimensionId);
        TAKEOVER_DEBUG_LOGGING = BUILDER
                .comment("Enable verbose takeover diagnostics in logs.")
                .define("debugLogging", DEFAULT_DEBUG_LOGGING);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private Config() {
    }

    private static boolean validateDimensionId(final Object value) {
        if (!(value instanceof String dimensionId)) {
            return false;
        }

        try {
            ResourceLocation.parse(dimensionId);
            return true;
        } catch (RuntimeException ignored) {
            return false;
        }
    }
}
