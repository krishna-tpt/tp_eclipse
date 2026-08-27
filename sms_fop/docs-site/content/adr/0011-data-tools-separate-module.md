---
title: ADR-0011 - Synthetic data tools in a separate Maven module
description: Architecture Decision Record 0011 - Synthetic data tools in a separate Maven module
---


**Status:** Accepted · **Date:** 2026-05-18

## Context

The project needs synthetic data generators for three purposes:
1. Local development — load realistic data so a fresh dev environment is immediately useful.
2. Integration test setup — seed deterministic data before TC-602 / TC-603 run.
3. Performance baselining — generate enough rows to exercise the indexes, partitions, and MV refresh path.

Putting these generators inside `filemanager-core` would mean their classes (and their dependencies) ship with the production JAR. Worse, accidentally invoking a generator in production would write garbage data into the live ledger.

## Decision

Synthetic data tools live in a separate Maven module: **`filemanager-data-tools`**.

```
inventoryledger/                  (parent POM)
├── filemanager-core/         (production JAR — shipped)
└── filemanager-data-tools/   (dev-only — never shipped)
```

- `filemanager-data-tools` depends on `filemanager-core` (for `Config.DbConfig`, `Database`, `FlywayMigrator`).
- `filemanager-core` does NOT depend on `filemanager-data-tools`.
- The production uber-JAR built by `maven-shade-plugin` includes only `filemanager-core` and its transitive deps — no data-tools code can be invoked from the production JAR.
- The data-tools module exposes three main classes: `GenerateOpeningBalance`, `LoadTransactions`, `LoadOrders`, each runnable via `mvn -pl filemanager-data-tools exec:java -Dexec.mainClass=...`.
- All generators are deterministic given `TOOLS_SEED`. Same seed produces byte-identical output across machines (see TC-601).

## Consequences

**Positive**
- The production JAR cannot accidentally invoke a data generator. Architectural separation enforces operational separation.
- Generators can evolve independently of production code (different release cadence, no impact on prod artifact).
- The "no hard-coded secrets" rule has a dedicated test (TC-604) scanning only the data-tools sources for credential patterns.
- Local dev gets a one-command path to realistic data — major onboarding win.

**Negative**
- Shared test helpers (e.g. `InventoryLedgerPgContainer`) are duplicated rather than imported. Acceptable at this scale; revisit if duplication grows beyond 2-3 classes (extract a test-support module or use Maven `test-jar`).
- Two modules to build, two POMs to maintain. Build time difference is negligible (~5 s).

## Alternatives considered

- **Generators as `src/test/java/...` in core** — rejected because they would only be invokable via `mvn test`, awkward for ad-hoc generation. Also conflates fixture-level test data with operational seeding.
- **Generators as `src/main/java/...` in core, gated by a profile** — rejected; nothing prevents accidental invocation in production.
- **Stand-alone repository** — rejected; needs to track core schema changes lock-step. Single repo with two modules is the right cohesion/coupling balance.
