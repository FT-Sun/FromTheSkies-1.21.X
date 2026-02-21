package net.jaftsun.fromtheskies.takeover;

import java.util.List;

import com.electronwill.nightconfig.core.UnmodifiableConfig;

import net.jaftsun.fromtheskies.Config;
import net.jaftsun.fromtheskies.FromTheSkies;
import net.jaftsun.fromtheskies.takeover.data.TakeoverSavedData;
import net.jaftsun.fromtheskies.takeover.world.GeneratedChunkIndexService;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(FromTheSkies.MOD_ID)
@PrefixGameTestTemplate(false)
public final class TakeoverRegistryGameTests {
  private static final List<String> TAKEOVER_CONFIG_KEYS = List.of(
      "enabled",
      "minGeneratedChunksSeen",
      "meteorMinTicks",
      "meteorMaxTicks",
      "retryTicks",
      "spreadIntervalTicks",
      "spreadAttemptsPerTick",
      "chunkBiomeFlipThreshold",
      "allowedDimensions",
      "debugLogging");
  private static final ResourceLocation ALIEN_CORE_ID = ResourceLocation.fromNamespaceAndPath(
      FromTheSkies.MOD_ID,
      "alien_core");
  private static final ResourceLocation ALIEN_OVERGROWTH_ID = ResourceLocation.fromNamespaceAndPath(
      FromTheSkies.MOD_ID,
      "alien_overgrowth");

  private TakeoverRegistryGameTests() {
  }

  @GameTest(template = "empty")
  public static void registry_objects_present(GameTestHelper helper) {
    boolean hasAlienCore = BuiltInRegistries.BLOCK.containsKey(ALIEN_CORE_ID);
    ResourceKey<Biome> alienBiomeKey = ResourceKey.create(Registries.BIOME, ALIEN_OVERGROWTH_ID);
    boolean hasAlienBiome = helper.getLevel().registryAccess().registryOrThrow(Registries.BIOME)
        .containsKey(alienBiomeKey);

    helper.assertTrue(hasAlienCore, "Expected block registry to contain fromtheskiesmcmod:alien_core");
    helper.assertTrue(hasAlienBiome, "Expected biome registry to contain fromtheskiesmcmod:alien_overgrowth");
    var alienCore = BuiltInRegistries.BLOCK.get(ALIEN_CORE_ID);
    helper.setBlock(1, 1, 1, alienCore);
    helper.assertBlockPresent(alienCore, 1, 1, 1);
    helper.succeed();
  }

  @GameTest(template = "empty")
  public static void takeover_config_defaults_valid(GameTestHelper helper) {
    helper.assertTrue(Config.SPEC.isLoaded(), "Expected common config spec to be loaded before GameTests run");

    UnmodifiableConfig spec = Config.SPEC.getSpec();
    UnmodifiableConfig values = Config.SPEC.getValues();
    for (String key : TAKEOVER_CONFIG_KEYS) {
      helper.assertTrue(
          hasTakeoverPath(spec, key),
          "Expected config spec to define key takeover." + key);
      helper.assertTrue(
          hasTakeoverPath(values, key),
          "Expected loaded config values to contain key takeover." + key);
    }

    helper.assertTrue(
        Config.TAKEOVER_ENABLED.get() == Config.DEFAULT_TAKEOVER_ENABLED,
        "Expected default takeover.enabled to be true");
    helper.assertTrue(
        Config.TAKEOVER_MIN_GENERATED_CHUNKS_SEEN.get() == Config.DEFAULT_MIN_GENERATED_CHUNKS_SEEN,
        "Expected default takeover.minGeneratedChunksSeen to be 512");
    helper.assertTrue(
        Config.TAKEOVER_METEOR_MIN_TICKS.get() == Config.DEFAULT_METEOR_MIN_TICKS,
        "Expected default takeover.meteorMinTicks to be 24000");
    helper.assertTrue(
        Config.TAKEOVER_METEOR_MAX_TICKS.get() == Config.DEFAULT_METEOR_MAX_TICKS,
        "Expected default takeover.meteorMaxTicks to be 72000");
    helper.assertTrue(
        Config.TAKEOVER_RETRY_TICKS.get() == Config.DEFAULT_RETRY_TICKS,
        "Expected default takeover.retryTicks to be 1200");
    helper.assertTrue(
        Config.TAKEOVER_SPREAD_INTERVAL_TICKS.get() == Config.DEFAULT_SPREAD_INTERVAL_TICKS,
        "Expected default takeover.spreadIntervalTicks to be 20");
    helper.assertTrue(
        Config.TAKEOVER_SPREAD_ATTEMPTS_PER_TICK.get() == Config.DEFAULT_SPREAD_ATTEMPTS_PER_TICK,
        "Expected default takeover.spreadAttemptsPerTick to be 8");
    helper.assertTrue(
        Math.abs(
            Config.TAKEOVER_CHUNK_BIOME_FLIP_THRESHOLD.get() - Config.DEFAULT_CHUNK_BIOME_FLIP_THRESHOLD) < 1.0E-9D,
        "Expected default takeover.chunkBiomeFlipThreshold to be 0.35");

    List<? extends String> allowedDimensions = Config.TAKEOVER_ALLOWED_DIMENSIONS.get();
    helper.assertTrue(
        allowedDimensions.size() == 1,
        "Expected default takeover.allowedDimensions to contain exactly one entry");
    helper.assertTrue(
        allowedDimensions.contains(Config.DEFAULT_ALLOWED_DIMENSION),
        "Expected default takeover.allowedDimensions to include minecraft:overworld");

    helper.assertTrue(
        Config.TAKEOVER_DEBUG_LOGGING.get() == Config.DEFAULT_DEBUG_LOGGING,
        "Expected default takeover.debugLogging to be false");
    helper.assertTrue(
        Config.TAKEOVER_METEOR_MAX_TICKS.get() >= Config.TAKEOVER_METEOR_MIN_TICKS.get(),
        "Expected takeover.meteorMaxTicks to be greater than or equal to takeover.meteorMinTicks");
    helper.succeed();
  }

  @GameTest(template = "empty")
  public static void persistence_resume_state(GameTestHelper helper) {
    TakeoverSavedData original = new TakeoverSavedData();
    original.setState(TakeoverLifecycleState.ACTIVE);
    original.setTakeoverLocked(true);
    original.setArmedAtGameTime(1234L);
    original.setScheduledMeteorGameTime(5678L);
    original.setSchedulerRetryTicksRemaining(90);
    original.setLastSpreadTickGameTime(9012L);
    original.setCorePos(new BlockPos(10, 80, -12));
    original.addGeneratedChunk(new ChunkPos(3, 4));
    original.addGeneratedChunk(new ChunkPos(-2, 7));
    original.addInfectedSurfaceBlock(new BlockPos(10, 81, -12));
    original.addInfectedSurfaceBlock(new BlockPos(11, 81, -12));
    original.setEligibleSurfaceCount(new ChunkPos(3, 4), 30);
    original.setInfectedSurfaceCount(new ChunkPos(3, 4), 11);
    original.addConvertedChunk(new ChunkPos(3, 4));
    original.addConvertedChunk(new ChunkPos(-2, 7));

    CompoundTag serialized = original.save(new CompoundTag(), helper.getLevel().registryAccess());
    TakeoverSavedData loaded = TakeoverSavedData.load(serialized, helper.getLevel().registryAccess());

    helper.assertTrue(loaded.getState() == TakeoverLifecycleState.ACTIVE, "Expected state to persist");
    helper.assertTrue(loaded.isTakeoverLocked(), "Expected one-time lock flag to persist");
    helper.assertTrue(loaded.getArmedAtGameTime() == 1234L, "Expected armedAtGameTime to persist");
    helper.assertTrue(loaded.getScheduledMeteorGameTime() == 5678L, "Expected scheduledMeteorGameTime to persist");
    helper.assertTrue(loaded.getSchedulerRetryTicksRemaining() == 90, "Expected schedulerRetryTicksRemaining to persist");
    helper.assertTrue(loaded.getLastSpreadTickGameTime() == 9012L, "Expected lastSpreadTickGameTime to persist");

    helper.assertTrue(loaded.getCorePos() != null, "Expected core position to persist");
    helper.assertTrue(
        loaded.getCorePos() != null && loaded.getCorePos().equals(new BlockPos(10, 80, -12)),
        "Expected core position value to persist");

    helper.assertTrue(loaded.getGeneratedChunks().size() == 2, "Expected generated chunk index size to persist");
    helper.assertTrue(
        loaded.getGeneratedChunks().contains(new ChunkPos(3, 4)),
        "Expected generated chunk (3,4) to persist");
    helper.assertTrue(
        loaded.getGeneratedChunks().contains(new ChunkPos(-2, 7)),
        "Expected generated chunk (-2,7) to persist");

    helper.assertTrue(
        loaded.getInfectedSurfaceBlocks().size() == 2,
        "Expected infected surface tracking set to persist");
    helper.assertTrue(
        loaded.getInfectedSurfaceBlocks().contains(new BlockPos(10, 81, -12)),
        "Expected infected surface position (10,81,-12) to persist");
    helper.assertTrue(
        loaded.getInfectedSurfaceBlocks().contains(new BlockPos(11, 81, -12)),
        "Expected infected surface position (11,81,-12) to persist");

    helper.assertTrue(
        loaded.getEligibleSurfaceCount(new ChunkPos(3, 4)) == 30,
        "Expected eligible surface count to persist");
    helper.assertTrue(
        loaded.getInfectedSurfaceCount(new ChunkPos(3, 4)) == 11,
        "Expected infected surface count to persist");

    helper.assertTrue(loaded.getConvertedChunks().size() == 2, "Expected converted chunk set size to persist");
    helper.assertTrue(
        loaded.getConvertedChunks().contains(new ChunkPos(3, 4)),
        "Expected converted chunk (3,4) to persist");
    helper.assertTrue(
        loaded.getConvertedChunks().contains(new ChunkPos(-2, 7)),
        "Expected converted chunk (-2,7) to persist");
    helper.succeed();
  }

  @GameTest(template = "empty")
  public static void generated_index_updates_overworld_only(GameTestHelper helper) {
    ServerLevel overworld = helper.getLevel();
    ServerLevel nether = overworld.getServer().getLevel(Level.NETHER);
    ServerLevel end = overworld.getServer().getLevel(Level.END);
    helper.assertTrue(nether != null, "Expected Nether level to exist during GameTest");
    helper.assertTrue(end != null, "Expected End level to exist during GameTest");

    TakeoverSavedData overworldData = TakeoverSavedData.get(overworld);
    TakeoverSavedData netherData = TakeoverSavedData.get(nether);
    TakeoverSavedData endData = TakeoverSavedData.get(end);

    int overworldBefore = overworldData.getGeneratedChunkCount();
    int netherBefore = netherData.getGeneratedChunkCount();
    int endBefore = endData.getGeneratedChunkCount();
    ChunkPos overworldChunk = nextUnindexedChunk(overworldData, 900_000, 900_000);
    ChunkPos netherChunk = nextUnindexedChunk(netherData, 910_000, 910_000);
    ChunkPos endChunk = nextUnindexedChunk(endData, 920_000, 920_000);

    boolean overworldIndexed = GeneratedChunkIndexService.recordGeneratedChunkOnLoad(overworld, overworldChunk);
    boolean netherIndexed = GeneratedChunkIndexService.recordGeneratedChunkOnLoad(nether, netherChunk);
    boolean endIndexed = GeneratedChunkIndexService.recordGeneratedChunkOnLoad(end, endChunk);

    helper.assertTrue(overworldIndexed, "Expected Overworld chunk load to be indexed");
    helper.assertTrue(!netherIndexed, "Expected Nether chunk load to be ignored");
    helper.assertTrue(!endIndexed, "Expected End chunk load to be ignored");

    helper.assertTrue(
        overworldData.getGeneratedChunkCount() == overworldBefore + 1,
        "Expected Overworld generated chunk index to grow by one");
    helper.assertTrue(
        overworldData.hasGeneratedChunk(overworldChunk),
        "Expected Overworld generated chunk index to contain recorded chunk");
    helper.assertTrue(
        !overworldData.hasGeneratedChunk(netherChunk),
        "Expected Nether chunk to not appear in Overworld index");
    helper.assertTrue(
        !overworldData.hasGeneratedChunk(endChunk),
        "Expected End chunk to not appear in Overworld index");
    helper.assertTrue(
        netherData.getGeneratedChunkCount() == netherBefore,
        "Expected Nether generated chunk index to remain unchanged");
    helper.assertTrue(
        endData.getGeneratedChunkCount() == endBefore,
        "Expected End generated chunk index to remain unchanged");
    helper.succeed();
  }

  private static ChunkPos nextUnindexedChunk(TakeoverSavedData data, int startX, int startZ) {
    for (int offset = 0; offset < 4096; offset++) {
      ChunkPos candidate = new ChunkPos(startX + offset, startZ + offset);
      if (!data.hasGeneratedChunk(candidate)) {
        return candidate;
      }
    }
    throw new IllegalStateException("Could not find an unindexed chunk candidate for GameTest");
  }

  private static boolean hasTakeoverPath(UnmodifiableConfig config, String key) {
    return config.contains(List.of("takeover", key));
  }
}
