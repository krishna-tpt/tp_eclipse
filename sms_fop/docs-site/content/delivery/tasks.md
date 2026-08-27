---
title: Task board
description: Every completed task, plus what's still open.
---

Ordered by task ID. 66+ tasks in the tracker; only three still open. See [Status](/delivery/status) for the summary.

## Open

| # | Status | Task |
|---|---|---|
| 45 | pending | Tests + fixtures for both Michelin variants (SMS_413 and DMC_408) — the sample fixture for BATCH_408 exists but a second variant hasn't been wired |
| 46 | pending | Document the catalog contract in ARCHITECTURE.md + ADR — largely covered by this docs site (see [Catalog-driven decoding](/architecture/catalog-driven)); ADR still to write |
| 47 | pending | Build long-lived scheduler + lifecycle layer — **shipped** as c82d28a, but the task remains open until it's smoke-tested in prod for a week |
| 79 | in progress | Add physical-count override path (TC22) to `filemanager-data-tools:InventorySimulator` |

## Completed by area

### Catalog + parser (35–41, 48)

| # | Task |
|---|---|
| 35 | Design interface-catalog YAML schema |
| 36 | Build InterfaceDefinition loader + validator |
| 37 | Build PositionalRecordParser |
| 38 | Build VariantDetector |
| 39 | Build RecordRouter + FieldMapper |
| 40 | Implement SftpFileSource (MINA SSHD) |
| 41 | Add envelope validation (footer counts) |
| 48 | Align catalog + staging to actual Michelin Extract Phase spec |

### Staging + core tables (43–44, 60–61)

| # | Task |
|---|---|
| 43 | Add staging tables for MICH_INV_STOCKLEVEL variants |
| 44 | Wire stocklevel staging → core tables (load procedure) |
| 60 | Duplicate-row natural-key check within file |
| 61 | Verify staging→processed promotion post-commit |

### Scope + retirements (49–50)

| # | Task |
|---|---|
| 49 | Document two-project boundary (MicroService vs DBServices) |
| 50 | Retire legacy CSV-header OpeningBalanceLoader chain |

### Customer install packages (51–55, 62–64)

| # | Task | Tag |
|---|---|---|
| 51 | Customer fresh install package (processed schema, V14+V15 baked in) | (pre-v1) |
| 52 | Move customer scripts into versioned deploy/ tag folder | (all) |
| 53 | Cut deploy/05-06-v2-customer with simplified function/trigger model | v2 |
| 54 | Generate schema documentation HTML (tables + functions + triggers) | v2 |
| 55 | Cut deploy/08-06-v3-customer with subinventory and stock_status dimensions | v3 |
| 62 | Cut deploy/11-06-v4-customer with per-subinv reservations + insert-only inboxes | v4 |
| 63 | Cut deploy/11-06-v5-customer with auto-promote triggers | v5 |
| 64 | Cut deploy/11-06-v6-customer with line-level cascade + partial shipments | v6 |

### Files.com integration + naming (56, 58–59)

| # | Task |
|---|---|
| 56 | Add files.com FileSource backend (configurable, TDD) |
| 58 | Rename package + module: `org.tenthplanet` → `org.michelin.filemanager` |
| 59 | Apply audit fixes + verify (compile + all tests) |

### Code audit (57)

| # | Task |
|---|---|
| 57 | Audit codebase: comments, hardcoding, naming, OOP, unused |

### Config overlay + JAR rename (80–84)

| # | Task |
|---|---|
| 80 | Add CONFIG_DIR overlay support to ConfigLoader |
| 81 | Add unit test for CONFIG_DIR overlay |
| 82 | Rename JAR to `psql-inventory-integration-service-1.0.0` |
| 83 | Update `.env.example` + README with CONFIG_DIR docs |
| 84 | Build + run tests to verify nothing regressed |

### v6 test pack (65–68)

| # | Task |
|---|---|
| 65 | Design full-coverage v6 test pack (calc + edge + race + exception + negative) |
| 66 | Author `test_v6_full_suite.sql` |
| 67 | Run pack against fresh customer DB backup container |
| 68 | Write `TEST-REPORT-v6.md` with results |

### Inventory simulator (69–78)

Scenario-based simulator that runs realistic order → shipment → close flows against a live customer DB backup. Drives `TEST-REPORT-v6.html` scoreboard.

| # | Task |
|---|---|
| 69 | Design scenario CSV schema with TC-keyed products |
| 70 | Author `InventorySimulator.java` (new main class in `filemanager-data-tools`) |
| 71 | Add `sample-scenarios.csv` + `run-sim.sh` + `run-sim.bat` |
| 72 | Build & smoke-test simulator against the customer DB backup |
| 73 | Extend simulator: returns / receipts / adjustments / post-ship cancellation |
| 74 | Add TC11–TC15 to `sample-scenarios.csv` (returns, receipt, adjustments) |
| 75 | Rebuild and re-run full 15-case suite against customer backup |
| 76 | Run simulator live + capture per-TC results for HTML |
| 77 | Extend simulator for moves + concurrency + multi-line |
| 78 | Add TC16–TC21 + run live + update HTML |

### Scheduler + health (85–93, ADR-0014)

| # | Task |
|---|---|
| 85 | Write ADR-0014 for long-running scheduler decision |
| 86 | Update SPEC.md with scheduler + health NFRs |
| 87 | Update ARCHITECTURE.md with scheduler + health components |
| 88 | Add cron-utils dependency + Config records |
| 89 | Implement CronSchedule + SchedulerLoop |
| 90 | Implement HealthEndpoint (JDK HttpServer) |
| 91 | Wire RUN_MODE dispatch in App.main |
| 92 | Write unit tests for scheduler + health |
| 93 | Build, run all tests, commit, bundle for VDI |

### Observability event log (94–99, alter_04)

| # | Task |
|---|---|
| 94 | Write `alter_04` audit event_log + views |
| 95 | Implement `EventLogWriter` helper |
| 96 | Wire file events into `CatalogFileLoader` |
| 97 | Add `HeartbeatEmitter` + wire into scheduled mode |
| 98 | Unit tests for `EventLogWriter` + `HeartbeatEmitter` |
| 99 | Build, test, commit, bundle |

### Customer schema alignment (100)

| # | Task |
|---|---|
| 100 | Add `FLYWAY_ENABLED` env var to skip migrations in prod |

## Task life-cycle rules

- Every task cited an F-ID or NFR from [Spec](/overview/spec) at creation
- "Done" means: code merged, tests pass, feature verified against a live DB or a fixture, and either shipped in a bundle to DevOps or waiting to
- Task IDs are not recycled — retired tasks keep their number for traceability
- Tasks 1–34 predate the current repo — captured in the private TenthPlanet workspace history

## What's not tracked here

- Bug fixes surfaced by testing — tracked in [Test results](/quality/test-results#bugs-surfaced-by-execution-16-total-all-fixed)
- Documentation updates — captured in the [Changelog](/delivery/changelog) via commit messages
- Ops-side changes (K8s YAML, secret rotation, CA bundle updates) — customer DevOps's tracker
