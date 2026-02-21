package net.jaftsun.fromtheskies.takeover;

import java.util.List;

import com.electronwill.nightconfig.core.UnmodifiableConfig;

import net.jaftsun.fromtheskies.Config;
import net.jaftsun.fromtheskies.FromTheSkies;
import net.jaftsun.fromtheskies.registry.ModBlocks;
import net.jaftsun.fromtheskies.takeover.data.TakeoverSavedData;
import net.jaftsun.fromtheskies.takeover.world.GeneratedChunkIndexService;
import net.jaftsun.fromtheskies.takeover.world.MeteorSchedulerService;
import net.jaftsun.fromtheskies.takeover.world.BiomeConversionService;
import net.jaftsun.fromtheskies.takeover.world.SurfaceSpreadService;
import net.jaftsun.fromtheskies.takeover.world.TakeoverCoreService;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.util.RandomSource;
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

  @GameTest(template = "empty")
  public static void scheduler_waits_for_threshold(GameTestHelper helper) {
    ServerLevel level = helper.getLevel();
    TakeoverSavedData data = TakeoverSavedData.get(level);
    data.resetForTesting();

    ChunkPos indexedA = new ChunkPos(1000, 1000);
    ChunkPos indexedB = new ChunkPos(1001, 1001);
    data.addGeneratedChunk(indexedA);
    MeteorSchedulerService.SchedulerSettings settings = new MeteorSchedulerService.SchedulerSettings(2, 3, 3, 10);
    RandomSource random = RandomSource.create(42L);

    MeteorSchedulerService.TickResult beforeThreshold = MeteorSchedulerService.tick(
        level,
        data,
        settings,
        random,
        100L);
    helper.assertTrue(!beforeThreshold.armedThisTick(), "Expected scheduler to stay dormant before threshold");
    helper.assertTrue(!beforeThreshold.triggeredThisTick(), "Expected no meteor trigger before threshold");
    helper.assertTrue(data.getState() == TakeoverLifecycleState.DORMANT, "Expected lifecycle state to remain DORMANT");

    data.addGeneratedChunk(indexedB);
    MeteorSchedulerService.TickResult armedTick = MeteorSchedulerService.tick(
        level,
        data,
        settings,
        random,
        101L);
    helper.assertTrue(armedTick.armedThisTick(), "Expected scheduler to arm once threshold is reached");
    helper.assertTrue(!armedTick.triggeredThisTick(), "Expected meteor not to trigger immediately after arming");
    helper.assertTrue(data.getState() == TakeoverLifecycleState.ARMED, "Expected lifecycle state to transition to ARMED");
    helper.assertTrue(data.getScheduledMeteorGameTime() == 104L, "Expected meteor to schedule within configured window");

    MeteorSchedulerService.TickResult waitingTick = MeteorSchedulerService.tick(
        level,
        data,
        settings,
        random,
        103L);
    helper.assertTrue(!waitingTick.triggeredThisTick(), "Expected scheduler to wait until scheduled game time");
    helper.assertTrue(data.getState() == TakeoverLifecycleState.ARMED, "Expected lifecycle state to remain ARMED while waiting");

    MeteorSchedulerService.TickResult triggerTick = MeteorSchedulerService.tick(
        level,
        data,
        settings,
        random,
        104L);
    helper.assertTrue(triggerTick.triggeredThisTick(), "Expected meteor to trigger at scheduled game time");
    helper.assertTrue(data.isTakeoverLocked(), "Expected takeover to lock after first trigger");
    helper.assertTrue(data.getState() == TakeoverLifecycleState.ACTIVE, "Expected lifecycle state to transition to ACTIVE");
    helper.assertTrue(data.getCorePos() != null, "Expected scheduler trigger to choose and store a core landing position");
    helper.assertTrue(
        data.hasGeneratedChunk(new ChunkPos(data.getCorePos())),
        "Expected selected landing chunk to come from generated chunk index");
    helper.succeed();
  }

  @GameTest(template = "empty")
  public static void single_event_only(GameTestHelper helper) {
    ServerLevel level = helper.getLevel();
    TakeoverSavedData data = TakeoverSavedData.get(level);
    data.resetForTesting();

    ChunkPos indexedChunk = new ChunkPos(1200, 1200);
    data.addGeneratedChunk(indexedChunk);
    MeteorSchedulerService.SchedulerSettings settings = new MeteorSchedulerService.SchedulerSettings(1, 0, 0, 10);
    RandomSource random = RandomSource.create(99L);

    MeteorSchedulerService.TickResult firstTick = MeteorSchedulerService.tick(
        level,
        data,
        settings,
        random,
        200L);
    helper.assertTrue(firstTick.triggeredThisTick(), "Expected first eligible scheduler tick to trigger meteor");
    helper.assertTrue(data.isTakeoverLocked(), "Expected takeover to be locked after first trigger");
    BlockPos originalCorePos = data.getCorePos();
    helper.assertTrue(originalCorePos != null, "Expected first trigger to persist selected core position");

    data.setState(TakeoverLifecycleState.DORMANT);
    data.setScheduledMeteorGameTime(-1L);
    MeteorSchedulerService.TickResult secondTick = MeteorSchedulerService.tick(
        level,
        data,
        settings,
        random,
        500L);
    helper.assertTrue(!secondTick.armedThisTick(), "Expected locked takeover not to arm a second time");
    helper.assertTrue(!secondTick.triggeredThisTick(), "Expected locked takeover not to trigger a second meteor");
    helper.assertTrue(data.getState() == TakeoverLifecycleState.DORMANT, "Expected lifecycle to stay DORMANT after lock");
    helper.assertTrue(
        originalCorePos.equals(data.getCorePos()),
        "Expected first meteor landing position to remain unchanged after attempted re-trigger");
    helper.succeed();
  }

  @GameTest(template = "empty")
  public static void core_destroy_stops_spread(GameTestHelper helper) {
    ServerLevel level = helper.getLevel();
    TakeoverSavedData data = TakeoverSavedData.get(level);
    data.resetForTesting();
    data.setState(TakeoverLifecycleState.ACTIVE);
    data.setCorePos(new BlockPos(5, 2, 5));

    TakeoverCoreService.placeCoreIfNeeded(level, data);
    helper.assertTrue(
        level.getBlockState(data.getCorePos()).is(ModBlocks.ALIEN_CORE.get()),
        "Expected alien_core block to be placed at scheduled landing position");

    SurfaceSpreadService.tick(
        level,
        data,
        1,
        1,
        RandomSource.create(4L),
        300L);
    helper.assertTrue(data.getLastSpreadTickGameTime() == 300L, "Expected spread loop to advance while ACTIVE");

    TakeoverCoreService.onCoreBroken(level, data, data.getCorePos());
    helper.assertTrue(data.getState() == TakeoverLifecycleState.STOPPED, "Expected core break to set lifecycle state to STOPPED");

    SurfaceSpreadService.tick(
        level,
        data,
        1,
        1,
        RandomSource.create(5L),
        301L);
    helper.assertTrue(
        data.getLastSpreadTickGameTime() == 300L,
        "Expected spread loop to halt immediately after core is destroyed");
    helper.succeed();
  }

  @GameTest(template = "empty")
  public static void surface_infection_counts_correct(GameTestHelper helper) {
    ServerLevel level = helper.getLevel();
    TakeoverSavedData data = TakeoverSavedData.get(level);
    data.resetForTesting();

    ChunkPos chunkPos = new ChunkPos(40, 40);
    BlockPos surfaceA = new BlockPos(chunkPos.getMinBlockX() + 1, 80, chunkPos.getMinBlockZ() + 1);
    BlockPos surfaceB = new BlockPos(chunkPos.getMinBlockX() + 2, 80, chunkPos.getMinBlockZ() + 1);
    BlockPos surfaceC = new BlockPos(chunkPos.getMinBlockX() + 1, 80, chunkPos.getMinBlockZ() + 2);
    BlockPos surfaceD = new BlockPos(chunkPos.getMinBlockX() + 2, 80, chunkPos.getMinBlockZ() + 2);
    List<BlockPos> eligiblePositions = List.of(surfaceA, surfaceB, surfaceC, surfaceD);
    SurfaceSpreadService.updateChunkCountsFromEligiblePositions(data, chunkPos, eligiblePositions);
    int eligibleCount = data.getEligibleSurfaceCount(chunkPos);
    helper.assertTrue(eligibleCount == 4, "Expected explicit eligible-position model to track four eligible surfaces");
    helper.assertTrue(data.getInfectedSurfaceCount(chunkPos) == 0, "Expected no infected blocks before seeding");

    data.addInfectedSurfaceBlock(surfaceA);
    data.addInfectedSurfaceBlock(surfaceD);
    SurfaceSpreadService.updateChunkCountsFromEligiblePositions(data, chunkPos, eligiblePositions);
    helper.assertTrue(
        data.getEligibleSurfaceCount(chunkPos) == eligibleCount,
        "Expected eligible count to remain stable after infection updates");
    helper.assertTrue(
        data.getInfectedSurfaceCount(chunkPos) == 2,
        "Expected infected count to match the number of seeded infected surface blocks");
    helper.assertTrue(
        data.hasInfectedSurfaceBlock(surfaceA) && data.hasInfectedSurfaceBlock(surfaceD),
        "Expected infected surface position tracking set to contain the seeded positions");
    helper.succeed();
  }

  @GameTest(template = "empty")
  public static void active_core_seeds_initial_infection(GameTestHelper helper) {
    ServerLevel level = helper.getLevel();
    TakeoverSavedData data = TakeoverSavedData.get(level);
    data.resetForTesting();
    data.setState(TakeoverLifecycleState.ACTIVE);

    BlockPos corePos = helper.absolutePos(new BlockPos(5, 2, 5));
    BlockPos seedCandidate = corePos.east();
    level.setBlock(seedCandidate, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
    level.setBlock(corePos.north(), Blocks.STONE.defaultBlockState(), 3);
    level.setBlock(corePos.south(), Blocks.STONE.defaultBlockState(), 3);
    level.setBlock(corePos.west(), Blocks.STONE.defaultBlockState(), 3);

    data.setCorePos(corePos);
    data.addGeneratedChunk(new ChunkPos(corePos));
    TakeoverCoreService.placeCoreIfNeeded(level, data);

    int spreads = SurfaceSpreadService.tick(
        level,
        data,
        1,
        1,
        RandomSource.create(71L),
        700L);

    helper.assertTrue(spreads >= 0, "Expected spread tick to execute while ACTIVE");
    helper.assertTrue(
        data.getInfectedSurfaceBlockCount() >= 1,
        "Expected active takeover tick to seed initial infection near the core when none exists");
    helper.assertTrue(
        data.hasInfectedSurfaceBlock(seedCandidate),
        "Expected deterministic nearest eligible surface around core to be seeded as infected");
    helper.assertTrue(
        level.getBlockState(seedCandidate).is(Blocks.SCULK),
        "Expected seeded infection to mutate the world block for visible takeover progression");
    helper.succeed();
  }

  @GameTest(template = "empty")
  public static void top_surface_spread_converts_target_to_sculk(GameTestHelper helper) {
    ServerLevel level = helper.getLevel();
    TakeoverSavedData data = TakeoverSavedData.get(level);
    data.resetForTesting();
    data.setState(TakeoverLifecycleState.ACTIVE);

    BlockPos source = helper.absolutePos(new BlockPos(1, 2, 1));
    BlockPos target = helper.absolutePos(new BlockPos(2, 2, 1));
    level.setBlock(source, Blocks.SAND.defaultBlockState(), 3);
    level.setBlock(target, Blocks.SAND.defaultBlockState(), 3);
    data.addGeneratedChunk(new ChunkPos(source));
    data.addGeneratedChunk(new ChunkPos(target));

    data.addInfectedSurfaceBlock(source);
    boolean spread = SurfaceSpreadService.spreadFromSourceForTesting(level, data, source, 1, 0, true);
    helper.assertTrue(spread, "Expected grass-like local spread to operate on natural surface blocks");
    helper.assertTrue(data.hasInfectedSurfaceBlock(target), "Expected target natural surface block to become infected");
    helper.assertTrue(
        level.getBlockState(target).is(Blocks.SCULK),
        "Expected infected natural surface block to visually convert for client validation");
    helper.succeed();
  }

  @GameTest(template = "empty")
  public static void grass_like_local_spread(GameTestHelper helper) {
    ServerLevel level = helper.getLevel();
    TakeoverSavedData data = TakeoverSavedData.get(level);
    data.resetForTesting();
    data.setState(TakeoverLifecycleState.ACTIVE);

    BlockPos source = helper.absolutePos(new BlockPos(1, 1, 1));
    BlockPos adjacent = helper.absolutePos(new BlockPos(2, 1, 1));
    BlockPos farther = helper.absolutePos(new BlockPos(3, 1, 1));
    level.setBlock(source, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
    level.setBlock(adjacent, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
    level.setBlock(farther, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
    data.addGeneratedChunk(new ChunkPos(source));
    data.addGeneratedChunk(new ChunkPos(adjacent));
    data.addGeneratedChunk(new ChunkPos(farther));

    data.addInfectedSurfaceBlock(source);
    boolean firstSpread = SurfaceSpreadService.spreadFromSourceForTesting(level, data, source, 1, 0, true);
    helper.assertTrue(firstSpread, "Expected spread to infect adjacent eligible block");
    helper.assertTrue(data.hasInfectedSurfaceBlock(adjacent), "Expected first spread step to infect adjacent position");
    helper.assertTrue(!data.hasInfectedSurfaceBlock(farther), "Expected spread not to skip directly to non-adjacent position");

    boolean secondSpread = SurfaceSpreadService.spreadFromSourceForTesting(level, data, adjacent, 1, 0, true);
    helper.assertTrue(secondSpread, "Expected contiguous spread step from newly infected adjacent position");
    helper.assertTrue(data.hasInfectedSurfaceBlock(farther), "Expected second contiguous spread step to infect next local position");
    helper.succeed();
  }

  @GameTest(template = "empty")
  public static void spread_ignores_light(GameTestHelper helper) {
    ServerLevel level = helper.getLevel();
    TakeoverSavedData data = TakeoverSavedData.get(level);
    data.resetForTesting();
    data.setState(TakeoverLifecycleState.ACTIVE);

    BlockPos source = helper.absolutePos(new BlockPos(1, 1, 1));
    BlockPos target = helper.absolutePos(new BlockPos(2, 1, 1));
    level.setBlock(source, Blocks.DIRT.defaultBlockState(), 3);
    level.setBlock(target, Blocks.DIRT.defaultBlockState(), 3);
    data.addGeneratedChunk(new ChunkPos(source));
    data.addGeneratedChunk(new ChunkPos(target));

    data.addInfectedSurfaceBlock(source);
    boolean spread = SurfaceSpreadService.spreadFromSourceForTesting(level, data, source, 1, 0, true);
    helper.assertTrue(spread, "Expected spread to occur without any light-level gating");
    helper.assertTrue(data.hasInfectedSurfaceBlock(target), "Expected low block-light target to become infected");
    helper.succeed();
  }

  @GameTest(template = "empty")
  public static void generated_boundary_freeze(GameTestHelper helper) {
    ServerLevel level = helper.getLevel();
    TakeoverSavedData data = TakeoverSavedData.get(level);
    data.resetForTesting();
    data.setState(TakeoverLifecycleState.ACTIVE);

    BlockPos source = new BlockPos(15, 80, 0);
    BlockPos target = new BlockPos(16, 80, 0);
    level.setBlock(source, Blocks.STONE.defaultBlockState(), 3);
    level.setBlock(target, Blocks.STONE.defaultBlockState(), 3);

    ChunkPos sourceChunk = new ChunkPos(source);
    ChunkPos targetChunk = new ChunkPos(target);
    data.addGeneratedChunk(sourceChunk);
    data.addInfectedSurfaceBlock(source);

    boolean blocked = SurfaceSpreadService.spreadFromSourceForTesting(level, data, source, 1, 0, true);
    helper.assertTrue(!blocked, "Expected spread to be rejected when adjacent chunk is not generated-indexed");
    helper.assertTrue(!data.hasInfectedSurfaceBlock(target), "Expected blocked frontier target to remain uninfected");
    helper.assertTrue(
        data.hasBlockedFrontierEdge(sourceChunk, targetChunk),
        "Expected blocked frontier edge to be tracked for debugging visibility");

    data.addGeneratedChunk(targetChunk);
    boolean resumed = SurfaceSpreadService.spreadFromSourceForTesting(level, data, source, 1, 0, true);
    helper.assertTrue(resumed, "Expected spread to continue after adjacent chunk is indexed");
    helper.assertTrue(data.hasInfectedSurfaceBlock(target), "Expected indexed frontier target to become infected");
    helper.succeed();
  }

  @GameTest(template = "empty")
  public static void stop_halts_future_spread(GameTestHelper helper) {
    ServerLevel level = helper.getLevel();
    TakeoverSavedData data = TakeoverSavedData.get(level);
    data.resetForTesting();
    data.setState(TakeoverLifecycleState.ACTIVE);

    BlockPos source = helper.absolutePos(new BlockPos(1, 1, 1));
    BlockPos target = helper.absolutePos(new BlockPos(2, 1, 1));
    BlockPos next = helper.absolutePos(new BlockPos(3, 1, 1));
    level.setBlock(source, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
    level.setBlock(target, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
    level.setBlock(next, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
    data.addGeneratedChunk(new ChunkPos(source));
    data.addGeneratedChunk(new ChunkPos(target));
    data.addGeneratedChunk(new ChunkPos(next));
    data.addInfectedSurfaceBlock(source);

    boolean spreadWhileActive = SurfaceSpreadService.spreadFromSourceForTesting(level, data, source, 1, 0, true);
    helper.assertTrue(spreadWhileActive, "Expected spread to work while lifecycle is ACTIVE");
    helper.assertTrue(data.hasInfectedSurfaceBlock(target), "Expected first active spread step to infect target");

    int infectedBeforeStop = data.getInfectedSurfaceBlockCount();
    data.setState(TakeoverLifecycleState.STOPPED);
    boolean spreadAfterStop = SurfaceSpreadService.spreadFromSourceForTesting(level, data, target, 1, 0, true);
    helper.assertTrue(!spreadAfterStop, "Expected spread attempts to be blocked once lifecycle is STOPPED");
    helper.assertTrue(
        data.getInfectedSurfaceBlockCount() == infectedBeforeStop,
        "Expected infected counters to stop increasing after STOPPED transition");
    helper.assertTrue(!data.hasInfectedSurfaceBlock(next), "Expected no new infections after STOPPED transition");
    helper.succeed();
  }

  @GameTest(template = "empty")
  public static void no_rollback_after_stop(GameTestHelper helper) {
    ServerLevel level = helper.getLevel();
    TakeoverSavedData data = TakeoverSavedData.get(level);
    data.resetForTesting();

    ChunkPos convertedChunk = new ChunkPos(96, 96);
    data.setEligibleSurfaceCount(convertedChunk, 10);
    data.setInfectedSurfaceCount(convertedChunk, 6);
    BiomeConversionService.applyChunkThresholdCheck(level, data, convertedChunk, 0.5D);
    data.setState(TakeoverLifecycleState.STOPPED);

    int infectedCountBefore = data.getInfectedSurfaceCount(convertedChunk);
    int spreadAttemptsAfterStop = SurfaceSpreadService.tick(
        level,
        data,
        1,
        8,
        RandomSource.create(11L),
        500L);
    helper.assertTrue(spreadAttemptsAfterStop == 0, "Expected STOPPED state to halt future spread ticks");
    helper.assertTrue(
        data.getInfectedSurfaceCount(convertedChunk) == infectedCountBefore,
        "Expected infected count to remain unchanged after STOPPED tick advance");

    CompoundTag saved = data.save(new CompoundTag(), level.registryAccess());
    TakeoverSavedData loaded = TakeoverSavedData.load(saved, level.registryAccess());
    helper.assertTrue(loaded.hasConvertedChunk(convertedChunk), "Expected converted chunk state to persist after save/load");
    helper.assertTrue(
        loaded.getInfectedSurfaceCount(convertedChunk) == infectedCountBefore,
        "Expected infected counters to persist without rollback after save/load");
    helper.succeed();
  }

  @GameTest(template = "empty")
  public static void debug_commands_transition_state(GameTestHelper helper) {
    ServerLevel level = helper.getLevel();
    TakeoverSavedData data = TakeoverSavedData.get(level);
    data.resetForTesting();

    BlockPos source = helper.absolutePos(new BlockPos(1, 1, 1));
    BlockPos target = helper.absolutePos(new BlockPos(2, 1, 1));
    level.setBlock(source, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
    level.setBlock(target, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
    data.addGeneratedChunk(new ChunkPos(source));
    data.addGeneratedChunk(new ChunkPos(target));
    data.addInfectedSurfaceBlock(source);

    int armed = TakeoverCommands.forceArm(level, data);
    helper.assertTrue(armed == 1, "Expected force_arm command handler to arm takeover state");
    helper.assertTrue(data.getState() == TakeoverLifecycleState.ARMED, "Expected lifecycle state to be ARMED after force_arm");

    int meteor = TakeoverCommands.forceMeteor(level, data);
    helper.assertTrue(meteor == 1, "Expected force_meteor command handler to activate takeover");
    helper.assertTrue(data.getState() == TakeoverLifecycleState.ACTIVE, "Expected lifecycle state to be ACTIVE after force_meteor");
    helper.assertTrue(data.getCorePos() != null, "Expected force_meteor to select a core position");

    int stepped = TakeoverCommands.stepSpread(level, data, 1);
    helper.assertTrue(stepped >= 0, "Expected step command handler to execute spread ticks without error");

    int stopped = TakeoverCommands.forceStop(level, data);
    helper.assertTrue(stopped == 1, "Expected stop command handler to return success");
    helper.assertTrue(data.getState() == TakeoverLifecycleState.STOPPED, "Expected lifecycle state to become STOPPED");
    helper.succeed();
  }

  @GameTest(template = "empty")
  public static void chunk_threshold_biome_flip(GameTestHelper helper) {
    ServerLevel level = helper.getLevel();
    TakeoverSavedData data = TakeoverSavedData.get(level);
    data.resetForTesting();

    ChunkPos chunkPos = new ChunkPos(64, 64);
    data.setEligibleSurfaceCount(chunkPos, 10);
    data.setInfectedSurfaceCount(chunkPos, 3);
    boolean belowThreshold = BiomeConversionService.applyChunkThresholdCheck(level, data, chunkPos, 0.4D);
    helper.assertTrue(!belowThreshold, "Expected chunk below threshold to not convert");
    helper.assertTrue(!data.hasConvertedChunk(chunkPos), "Expected chunk to remain unconverted below threshold");

    data.setInfectedSurfaceCount(chunkPos, 4);
    boolean atThreshold = BiomeConversionService.applyChunkThresholdCheck(level, data, chunkPos, 0.4D);
    helper.assertTrue(atThreshold, "Expected chunk at threshold to convert");
    helper.assertTrue(data.hasConvertedChunk(chunkPos), "Expected converted chunk set to include threshold-hit chunk");

    boolean idempotentRecheck = BiomeConversionService.applyChunkThresholdCheck(level, data, chunkPos, 0.4D);
    helper.assertTrue(!idempotentRecheck, "Expected repeated threshold checks to remain idempotent after conversion");
    helper.assertTrue(data.getConvertedChunkCount() == 1, "Expected converted chunk count to remain stable after recheck");
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
