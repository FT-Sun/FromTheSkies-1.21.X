# Takeover Code FAQ (Confusing Parts)

This file explains the most common questions raised while reading takeover code.

## Why so many `long` values?

- Minecraft time values (like `gameTime`) are `long`.
- `BlockPos` and `ChunkPos` are packed to a single `long` (`asLong()`, `toLong()`) for compact storage.
- `Set`/`Map` generics require object types, so packed keys are `Long` inside collections.

## What does `-1L` mean?

`-1L` is an unset sentinel for `long` time fields (`armedAtGameTime`, `scheduledMeteorGameTime`, `lastSpreadTickGameTime`).

## What is NBT / `CompoundTag`?

- NBT = Named Binary Tag (Minecraft's typed binary save format).
- `CompoundTag` is like a typed map/object in NBT.
- `TakeoverSavedData.save(...)` writes to NBT.
- `TakeoverSavedData.load(...)` reconstructs Java state from NBT.

## What does `Tag.TAG_LONG` check do?

`tag.contains(key, Tag.TAG_LONG)` verifies the NBT value exists and is a `long` before reading.

Example: loading `corePos` only if stored as packed long.

## What is `parseState(...)` doing?

`parseState` safely parses lifecycle enum text from save data. Missing/invalid values fall back to `DORMANT` so bad data does not crash load.

## What is `countTag` in `loadCountMap`?

`countTag` is just a local `CompoundTag` variable representing one map entry:

- `chunk` (packed long)
- `count` (int)

`Math.max(0, ...)` clamps negatives; only positive counts are kept.

## What does `setDirty()` do?

`TakeoverSavedData` extends `SavedData`. `setDirty()` marks state as changed so Minecraft persists it on save.

## What does `computeIfAbsent(FACTORY, DATA_NAME)` do?

In `TakeoverSavedData.get(level)`: load existing save entry if present, otherwise create/register a new one. It gives one canonical takeover state object per level.

## What does `source.hasPermission(2)` mean?

Permission level gate for commands. Level `2` is operator/admin-level command access.

## What does `TakeoverCommands::status` mean?

Method reference shorthand for lambda form:

`ctx -> TakeoverCommands.status(ctx)`

## Why use `@FunctionalInterface` for `LevelCommandAction`?

It defines a single-method callback interface so lambdas/method references can be passed to shared command helper `executeWithLevel(...)`.

## What are Java records here?

`TickResult` and `SchedulerSettings` are `record`s: compact immutable data carriers with generated constructor/accessors/equality/toString.

## Why `level.setBlock(..., 3)`?

Flag `3` means:

- `1`: neighbor/block update notifications
- `2`: send update to clients

So placement/infection is both simulated and visible immediately.

## Why does spread loop return `successfulSpreads`?

In `SurfaceSpreadService.tick(...)`, each attempt may fail due to eligibility/adjacency/frontier checks. The return value is the number of attempts that actually infected a new block this tick.

## What does `spreadFromSourceForTesting(...)` really do?

Despite the name, it enforces real production spread rules:

- state must be `ACTIVE`
- optional source-infected requirement
- cardinal adjacency only
- target must be top eligible surface and not already infected
- target chunk must be in generated chunk index

Returns `true` only when a new infection was committed.

## What does `onLevelTickPost` mean?

`LevelTickEvent.Post` runs after the level tick. Takeover systems run there so they operate on the world after normal tick updates for that frame.
