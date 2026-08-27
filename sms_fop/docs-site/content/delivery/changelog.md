---
title: Changelog
description: Commit-by-commit history of the sandbox branch.
---

Generated from `git log --oneline` on `sandbox` branch. Reverse chronological. See [Customer releases](/delivery/customer-releases) for the per-tag DB rollups.

## 2026-07 (July)

### `c769341` — docs(site): scaffold Blume docs-site with 5 core system docs

Introduced this documentation site. Blume 1.0.4 as the static-site framework; MDX content under `docs-site/content/`; local preview via `npm run dev`. Initial commit had 5 curated docs (spec, architecture, devops-runbook, customer-deployment, db-evolution). Superseded by the 30+ page structure you're reading now.

### `5683a62` — docs(deploy): add DEPLOY-DEVOPS.md operator runbook

377-line runbook targeting the customer's DevOps team. Sections: what the service does, prerequisites (JDK 21, Maven 3.9+), build, configuration (env vars from `.env.example`), K8s deployment (Secret + ConfigMap + Deployment YAML), verify (pod-level + DB queries), rollback (image-tag swap), change log for the current deploy, escalation path. Content mirrored on this docs site under [Operations → Deployment](/operations/deployment).

### `7764e50` — fix(catalog): align MICH_INV_STOCKLEVEL to actual delimited CFO feed

Central catalog fix after inspecting the real Michelin CFO files.

- `filename_pattern` widened to accept `SMS`, `DMC`, `BATCH` prefixes with 3-digit tenant codes
- `header_confirms.equals` changed to `MNA` (was `IFOPEUR` — variant-specific)
- `MTL_STOCKLEVEL.fields` set to 52 (was 55 — AME_QUANTITY removed per JIRA FOP_AGL-63630)
- Three trailing columns switched to `{ literal: NP }` since the delimited variant is silent on them: `receiving_location_type`, `stock_status`, `business_line`

The alignment lets the JAR ingest both the DMC_408 opening-balance file (June 3) and the SMS_413 stock file (July 15) via the same catalog. Verified end-to-end via SFTP: 284 rows parsed, staged, promoted to `processed.opening_balance`, file archived.

### `ea73a3f` — chore: gitignore .zip/.tar/.gz/.rar/.7z (blocked by gitlab push rule)

Michelin's GitLab has a server-side push rule that blocks binary archives. Added `*.zip`, `*.tar`, `*.gz`, `*.rar`, `*.7z` to `.gitignore`. Handoff bundles now live outside the repo (in the `inventoryledger-devops-*/` sibling folder that ships to DevOps via file drop).

## 2026-06 (June)

### `94aaa13` — fix: align JAR with v6 customer schema (FLYWAY_ENABLED + outbox + severity + notify_outbox)

- Added `FLYWAY_ENABLED` env var. When set `false`, `FlywayMigrator` skips migration entirely — needed because the customer DBA installs the schema via `customer_install.sql`, not via Flyway
- Reworked `OutboxDrainer` to use the v6 outbox schema (uppercased severity: `INFO`/`WARN`/`ERROR`; `dedup_key` + `repeat_count` columns)
- SQL writes now call `notify_outbox(...)` rather than direct INSERT — gains dedup
- Verified against the v6 customer DB backup

### `aa874c9` — fix(audit): correct alter_04 view definitions, caught by end-to-end validation

Ran the full observability stack end-to-end and caught view definition bugs:

- `v_stock_change_events` sfdc_order_line branch used `sfdc_line_id` in `event_id` — nullable, so events would drop. Changed to synthesize event_id from `sfdc_order_id || ':' || line_no::TEXT` (the composite PK).
- `v_txn_events` promotion branch used wrong CASE ordering (would misclassify `superseded` as `unknown`). Rearranged the CASE.
- `v_all_events` UNION signature mismatch (missing `event_id::TEXT` cast on the event_log branch). Fixed cast.

### `9095241` — feat(audit): observability event_log + derived views + Java writes

Shipped `alter_04_audit_event_log.sql` (361 lines):

- `audit.event_log` table with 5 indexes
- `v_file_batch_events`, `v_order_events`, `v_txn_events`, `v_stock_change_events`, `v_notify_events` views
- `v_all_events` master union (the Grafana endpoint)
- `processed.fetch_inventory_json_observed` — ATP read wrapper that logs each call with caller identity, latency, row count

Java side: `Event` record, `EventLogWriter`, `HeartbeatEmitter`. Wired into `CatalogFileLoader` (file lifecycle events) and `SchedulerLoop` (heartbeat). See [Observability](/architecture/observability).

### `c82d28a` — feat(scheduler+health): long-running mode with internal cron + /actuator/health (ADR-0014)

- New packages `scheduler/` (`CronSchedule`, `SchedulerLoop`) and `health/` (`HealthEndpoint`, `HealthStatus`)
- `RUN_MODE` env var dispatches in `App.main`: `oneshot` (dev/CI, default when unset) or `scheduled` (prod)
- `HEALTH_HOST` / `HEALTH_PORT` config
- `SCHEDULE_DAILY` / `SCHEDULE_HOURLY` config (standard 5-field cron, either can be empty)
- SIGTERM handler: interrupt scheduler → wait in-flight tick → close DB → exit 0
- `cron-utils` 9.2.1 added as dependency
- Unit tests: `CronScheduleTest`, `SchedulerLoopTest`, `HealthEndpointTest`

See [Scheduler and lifecycle](/architecture/scheduler-lifecycle) for the full contract.

### `f2d4edc` — feat(config): CONFIG_DIR overlay + rename jar to psql-inventory-integration-service-1.0.0

- `CONFIG_DIR` env var: when set, every `*.yaml` / `*.yml` file in that directory loads in alphabetical order as an overlay on top of the classpath defaults, before env vars
- Pattern used by the customer's centralized-config repository (`common-service-configs.yaml` + service-specific file mounted at `/opt/app/config`)
- JAR renamed from `inventoryledger.jar` to `psql-inventory-integration-service-1.0.0` to match the customer's deployed-service naming

### `a4b88e6` — chore(sandbox): initial snapshot for DevOps sandbox deployment

First cut of the `sandbox` branch — captures the state after v6 baseline was cut for the customer's DevOps sandbox environment.

### `082f086` — Initial import: catalog-driven file manager + customer deploy v2

Repo genesis. Import of the pre-existing `catalog-driven file manager + customer deploy v2` codebase from TenthPlanet's internal starting point.

## Prior history (before repo genesis)

Before `082f086`, work happened in private TenthPlanet workspaces. Highlights (from memory + design docs):

- Interface catalog YAML schema designed (task #35, ADR-0009)
- `InterfaceDefinition` loader + validator (task #36)
- `PositionalRecordParser` for fixed-width and delimited files (task #37)
- `VariantDetector` — filename + `header_confirms` (task #38)
- `RecordRouter` + `FieldMapper` — YAML-driven field mapping and type coercion (task #39)
- `SftpFileSource` via MINA SSHD (task #40)
- Envelope validation — footer counts vs data rows (task #41)
- Staging tables for both Michelin variants (task #43)
- Wire stocklevel staging → core tables via load procedure (task #44)

See [Task board](/delivery/tasks) for the full task history.

## Coming soon

- Alter file for observability enhancement (per-tenant event partitioning if scale warrants)
- Physical-count override path in the simulator (task #79, in progress)
- Formal SAST integration on the Maven build
- Load-test harness (currently informal — see [Performance](/quality/performance#load-testing))
