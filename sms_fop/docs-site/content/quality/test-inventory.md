---
title: Test inventory
description: The actual test classes in the repo (find generated via `find … -name '*Test.java' -o -name '*IT.java'`).
---

Extracted from the repo. Two modules, four categories.

## `filemanager-core/src/test/java/…`

### Catalog + parser (unit)

| Class | Purpose | TC-ID range |
|---|---|---|
| `catalog.CatalogLoaderTest` | Loads valid catalogs; rejects duplicate `(interface, variant)`; rejects duplicate field mapping; rejects missing `target_table`; rejects unknown types | TC-280..TC-285 |
| `catalog.VariantDetectorTest` | Filename pattern miss, hit with no header_confirms, hit with header_confirms, hit but header_confirms mismatches | TC-296..TC-299 |
| `catalog.InterfaceDefinitionTest` | `findRecord(tag)`, `findEnvelope(tag)`, `Optional.empty` for unknown tags | TC-275..TC-277 |
| `parser.PositionalRecordParserTest` | Delimited + fixed-width parses, empty/quoted/bare-empty → null, whitespace trimming, BOM stripping, blank line skipping | TC-290..TC-295 |

### Mapper + envelope (unit)

| Class | Purpose |
|---|---|
| `mapper.FieldMapperTest` | `field:` / `from_envelope:` / `literal:` sources; type coercion (NUMERIC, DATE, TIMESTAMPTZ); `required`, `max_length`, `allowed_values`, `default`; field-count mismatch (TC-205..TC-216) |
| `ingest.EnvelopeValidatorTest` | Footer count validation, mismatch → exception |

### Ingest pipeline (integration)

| Class | Purpose | TC-ID |
|---|---|---|
| `ingest.CatalogIngestPipelineTest` | Full pipeline against Testcontainers Postgres — parse → map → stage → promote for a real Michelin fixture file | TC-310 |

### Config (unit)

| Class | Purpose |
|---|---|
| `config.ConfigLoaderTest` | YAML + env-var merge; missing-key fail-fast; unknown-key warning; profile selection; `CONFIG_DIR` overlay merge |

### DB (unit + integration)

| Class | Purpose |
|---|---|
| `db.DatabaseTest` | Opens connection from Config; SELECT 1 succeeds |
| `db.FlywayMigratorTest` | Applies migrations; idempotent re-run; skipped when `FLYWAY_ENABLED=false` |

### Scheduler + health (unit)

| Class | Purpose |
|---|---|
| `scheduler.CronScheduleTest` | Cron expression parsing, next-fire calculation with injectable Clock |
| `scheduler.SchedulerLoopTest` | Loop fires on tick; mutex prevents overlap; failure in Application.run doesn't kill scheduler |
| `health.HealthEndpointTest` | Both checks UP → 200; either DOWN → 503 |

### Audit (unit)

| Class | Purpose |
|---|---|
| `audit.EventTest` | Event record shape + JSON serialization |
| `audit.EventLogWriterTest` | Successful insert; audit-write failure never propagates |
| `audit.HeartbeatEmitterTest` | Emits `daemon.heartbeat` at configured interval |

### Notifier + outbox (unit)

| Class | Purpose |
|---|---|
| `notifier.SingleUrlWebhookNotifierTest` | 2xx → Delivered; 4xx → FailedPermanent; 5xx → Failed; timeout → Failed |
| `notifier.MultiUrlNotifierTest` | Fans out; combined result semantics |
| `notifier.OutboxDrainerTest` | Selects pending + failed; marks delivered on success; increments retry_count on 5xx; failed_permanent at max_retries |
| `notifier.StatusTransitionPolicyTest` | Retry / no-retry decisions per HTTP code |

### File sources (smoke)

| Class | Purpose |
|---|---|
| `smoke.FilesComConnectivitySmokeTest` | Lists pickup folder, downloads one file, verifies checksum. Requires live creds. |
| `smoke.SftpConnectivitySmokeTest` | Same for SFTP. Requires live creds + SSH key. |

Smoke tests are opt-in via system property (`-Dsmoke.filescom=true`). Never run in CI.

## `filemanager-data-tools/src/test/java/…`

### Unit

| Class | Purpose |
|---|---|
| `unit.NoHardcodedSecretsTest` | Grep tool sources; fail if any production secret reference sneaks in |
| `unit.ToolsConfigLoaderTest` | Loads `tools.yaml`; seed / count / tenant mix parsing |
| `unit.GenerateOpeningBalanceTest` | Generator produces CSV with expected header; same seed = same output |

### Integration

| Class | Purpose |
|---|---|
| `integration.PgTestContainer` | Shared Testcontainers Postgres harness (base class) |
| `integration.LoadOrdersIT` | LoadOrders tool calls `upsert_order` for each generated payload |
| `integration.LoadTransactionsIT` | LoadTransactions tool calls `post_transaction` for each generated payload |

## SQL tests (customer tag)

Inside `deploy/11-06-v6-customer/`:

| File | Purpose |
|---|---|
| `test_smoke_native_payloads.sql` | Smoke against native Oracle / SFDC payload shapes (before normalization at FOP/SFDC) |
| `test_v6_comprehensive.sql` | Earlier iteration of v6 coverage; superseded by full_suite but retained |
| `test_v6_full_suite.sql` | The main v6 pack — 120+ assertions across baseline + guerrilla + adversarial |

Also inside `deploy/11-06-v6-customer/test-data-generator/`:

| Path | Purpose |
|---|---|
| `src/…/InventorySimulator.java` | Java simulator that runs TC01..TC22 scenarios against a live customer DB backup. Backing for the `TEST-REPORT-v6.html` scoreboard. |
| `src/…/sample-scenarios.csv` | Scenario definitions — one CSV row per TC |
| `run-sim.sh` / `run-sim.bat` | Launcher scripts |

## How to run

```bash
# Everything (JUnit + integration + coverage gate)
mvn -pl filemanager-core,filemanager-data-tools verify

# Just filemanager-core unit tests
mvn -pl filemanager-core test

# Just filemanager-core integration tests (requires Docker for Testcontainers)
mvn -pl filemanager-core verify

# Skip integration tests
mvn -pl filemanager-core -DskipITs verify

# Run smoke tests (opt-in)
mvn -pl filemanager-core test -Dsmoke.filescom=true
```

## Coverage gate

JaCoCo enforces:

- Line coverage ≥ 80% on production sources
- Line coverage = 100% on critical paths:
  - `CatalogIngestPipeline`
  - `OutboxDrainer`
  - `ConfigLoader`
  - `Database`
  - `f_promote_txn`, `f_promote_order` (via pgTAP)
  - `f_stock_balance_reservation_apply` (via pgTAP)

Build fails if either gate is missed.

## What's not covered (explicit gaps)

- Multi-replica coordination — single replica by design
- Long-running failure modes (memory leaks over weeks) — needs a soak test, not in the pack
- Network partitions between JAR and Postgres — needs a fault-injection harness
- Files.com API changes — smoke tests catch obvious breakage but nothing that requires a live production API

These aren't tested because the risk / value trade-off doesn't justify the harness build cost at the current scale (5k records/day, one tenant).
