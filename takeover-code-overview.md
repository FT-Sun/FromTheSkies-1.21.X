# Takeover Code Overview

## Purpose

The takeover system models a one-time Overworld event:

1. Track generated chunks in Overworld.
2. Arm takeover once a generated-chunk threshold is reached.
3. Schedule and trigger a meteor/core landing.
4. Spread surface infection via local adjacency.
5. Convert chunk biome state when infected/eligible ratios cross a threshold.
6. Halt future spread if the core is destroyed (no rollback of prior changes).

## Main Runtime Flow

- Event wiring: `takeover/event/TakeoverServerEvents.java`
- Per-level scheduler tick: `takeover/world/MeteorSchedulerService.java`
- Core placement/break handling: `takeover/world/TakeoverCoreService.java`
- Surface spread + counters: `takeover/world/SurfaceSpreadService.java`
- Chunk threshold conversion gate: `takeover/world/BiomeConversionService.java`

Tick order in `onLevelTickPost`:

1. `MeteorSchedulerService.tick(level)`
2. `TakeoverCoreService.placeCoreIfNeeded(level, data)`
3. `SurfaceSpreadService.tick(level)`

## Persistent State

`takeover/data/TakeoverSavedData.java` stores:

- lifecycle state (`DORMANT`, `ARMED`, `ACTIVE`, `STOPPED`, `COMPLETED`)
- timing fields (armed/scheduled/last spread tick)
- core position
- generated chunks index
- infected surface block set
- converted chunks set
- per-chunk eligible/infected counts
- blocked frontier edge diagnostics

All mutators call `setDirty()` so state is written back by `SavedData`.

## Config and Registries

- Config keys/defaults: `Config.java`
- Alien core block + item registration: `registry/ModBlocks.java`
- Target biome key: `takeover/world/TakeoverBiomes.java`

## Commands and Tests

- Admin/debug commands: `takeover/command/TakeoverCommands.java`
  - `fts takeover status`
  - `fts takeover force_arm`
  - `fts takeover force_meteor`
  - `fts takeover step <ticks>`
  - `fts takeover stop`
- Coverage tests: `takeover/TakeoverRegistryGameTests.java`

## Notes

- Spread is local cardinal adjacency (grass-like), not chunk-frontier flood-fill.
- Stopping takeover blocks future spread but keeps already converted/infected history.
- For low-level syntax/Java API clarifications, see `takeover-confusing-parts.md`.
