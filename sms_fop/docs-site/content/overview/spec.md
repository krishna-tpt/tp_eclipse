---
title: Specification
description: Functional + non-functional requirements for the Inventory Ledger.
---

**Version:** 0.5 · **Source:** `SPEC.md` in the repo · **Companion:** [Architecture](/architecture/overview), [Test plan](/quality/test-plan)

> **0.5 change-log:** Execution model flipped from single-shot batch to long-running service with internal cron scheduler (ADR-0014). Original single-shot semantics survive as `RUN_MODE=oneshot` for dev/CI/ad-hoc.

## 1. Purpose

Deliver (a) the PostgreSQL ledger schema, stored functions, triggers, and (optional) `pg_cron` schedules for inventory accounting, and (b) a long-running Java microservice with an internal cron scheduler that loads the opening balance file into the ledger on the configured cadence. Other teams write transactions and orders directly to the database. Calculations, promotion, and housekeeping run inside Postgres.

The JAR runs as a long-lived pod in the customer's K8s platform. Containerization and deployment YAML are owned by the customer's DevOps team; the JAR exposes `/actuator/health` for K8s probes.

## 2. Scope

### In scope

**Database:** three-schema layout (`processed`, `staging`, `audit`), master + live tables, promotion functions, cascade triggers, audit event log, notification outbox, `fetch_inventory_json` read API. See [Database overview](/database/overview).

**Java application:** long-running microservice with internal cron scheduler (see [Scheduler](/architecture/scheduler-lifecycle)); files.com / SFTP / local file source; SHA-256 hash dedup; catalog-driven parse; atomic COPY-to-staging then `CALL load_stocklevel`; archive/reject move; webhook outbox drain; `/actuator/health` liveness endpoint.

**Tests:** JUnit 5 + Testcontainers + pgTAP. See [Test plan](/quality/test-plan) and [Test results](/quality/test-results).

### Out of scope

- `recalculate_inventory` rules engine (deferred)
- Inbound business REST in our service
- Pipeline 2 (FOP) and Pipeline 3 (Salesforce) ingest code — those teams own their write paths
- Salesforce-facing HTTP read tier
- Compiere migration
- Container images, K8s manifests, secret sourcing — owned by customer DevOps

## 3. Functional requirements

| ID | Requirement | Owner |
|---|---|---|
| **F01** | File pickup on configured cron schedule; idempotent on SHA-256 hash; a tick that finds zero files logs `none-processed` and returns success | Java |
| **F01a** | Two schedules — `SCHEDULE_DAILY` (default `0 6 * * *`) and `SCHEDULE_HOURLY` (default `5 * * * *`); either disable-able; mutex prevents overlap | Java |
| **F01b** | `RUN_MODE=oneshot` env var bypasses the scheduler for dev/CI/ad-hoc runs | Java |
| **F02** | File shape validated against interface catalog (field count, header confirms, footer counts) | Java |
| **F03** | Bad shape → move to `reject/`, write notification, continue with next file | Java |
| **F04** | Good rows COPY-loaded to `staging.stocklevel_inbox`; then `load_stocklevel(file_name)` validates and promotes | Java + SQL |
| **F05** | On success → move file to `archive/<yyyy-mm>/<filename>_<ts>`; record `stocklevel_batch` | Java + SQL |
| **F06** | Drain `notification_outbox` via webhook; mark each row `delivered`/`failed` with retry tracking | Java |
| **F07** | `staging.f_promote_txn` — validates against masters, dedupes on `(tenant_id, external_txn_id)`, inserts to `inv_transaction`; trigger updates `stock_balance` | SQL |
| **F09** | Transfers post atomically as paired in/out rows in `inv_transaction` | SQL |
| **F10** | `staging.f_promote_order` — diff against existing SFDC order; state machine `open → synced → closed → cancelled` | SQL |
| **F11** | Reservation cascade — outbound txn matching an open order line stamps `fop_synced_at` and closes the line when `shipped_qty >= qty` | SQL |
| **F12** | Inventory calculation returns `(on_hand, reserved, available)` per `(tenant, product, warehouse, subinventory, stock_status)` | SQL |
| **F13** | `fetch_inventory_json(tenant, warehouse?, subinventory?, product?)` returns MV-backed read of the current position | SQL |
| **F17** | All exception paths (Java + SQL) write to `notification_outbox` with severity tag and message | Both |

## 4. Non-functional requirements (acceptance criteria)

| NFR | Priority | Acceptance criterion |
|---|---|---|
| **Failure notification** | MUST | All exception paths write to `notification_outbox`; drained by Java run; test asserts webhook fires |
| **Performance** | SHOULD | `fetch_inventory_json` p95 < 300 ms at 5k records/day |
| **Idempotency** | MUST | UNIQUE `(tenant_id, external_txn_id)` on `inv_transaction`; at-least-once retry returns original outcome. Java daily run idempotent on file hash. |
| **Audit & traceability** | MUST | `audit.event_log` records every file event; `audit.v_all_events` aggregates for Grafana |
| **Monitoring** | MUST | Java service logs structured JSON to stdout (K8s log pipeline); each scheduled run logs start, file count, exit code, latency |
| **Liveness / readiness** | MUST | `/actuator/health` returns 200 `{"status":"UP"}` when DB ping succeeds and scheduler thread is alive; 503 otherwise |
| **Scheduler reliability** | MUST | A failed `Application.run()` logs WARN but does not kill the scheduler — next tick still fires. Mutex serializes overlapping triggers. |
| **Graceful shutdown** | MUST | SIGTERM interrupts scheduler sleep, allows in-flight run to finish, closes DB. `terminationGracePeriodSeconds` ≥ 120 s recommended. |
| **Disaster recovery** | SHOULD | Azure Flexible Postgres PITR; RPO ≤ 15 min, RTO ≤ 4 h; quarterly restore drill |

## 5. Constraints

- **JDK:** Eclipse Temurin 21
- **Framework:** none — plain Java + single-purpose libraries
- **DB engine:** Azure Database for PostgreSQL Flexible Server 16 (prod); local Postgres (dev)
- **DB extensions:** `pgcrypto` built-in (PG13+); no `pg_partman` / `pg_cron` in the v6 customer package (customer scheduler is Airflow / K8s CronJob)
- **Naming:** all DB objects lowercase snake_case, no spaces
- **Scale:** max ~5,000 records/day
- **Identifier columns:** TEXT (not UUID); auto-gen uses `gen_random_uuid()::text`
- **No superuser on Azure** — JAR connects as a least-privilege role

## 6. Engineering principles (non-negotiable)

- **No hard-coding** — every threshold, path, URL, timeout, batch size, schedule, file pattern, retry count, retention window comes from `application.yaml` + env vars. Validated at startup; fail fast on missing values. See [Coding guidelines](/quality/coding-guidelines).
- **Test-driven development** — tests written before implementation. Red → green → refactor. Every public SQL function and every Java class has tests before code is merged.
- **Synthetic data loaders are a separate Maven module** (`filemanager-data-tools`). Never bundled into the production JAR.
- **Test cases documented + executed** — `TEST-PLAN.md` lists every TC-NNN mapped to F-IDs / NFRs; `TEST-RESULTS.md` regenerated each CI run.
- **Coverage gate** — ≥ 80% line coverage on production sources; 100% on critical paths.

## 7. Definition of done (base release)

- All in-scope schema and functions deployed via `deploy/11-06-v6-customer/customer_install.sql` + alter_04
- `mvn verify` at parent POM builds both modules and passes all tests
- `filemanager-core` is the only artifact shipped to production
- `filemanager-data-tools` builds independently; generates synthetic CSVs and loads synthetic transactions/orders
- Triggers verified: INSERT into `staging.txn_inbox` promotes to `inv_transaction` and updates `stock_balance` in the same transaction
- Failure notifier delivers to a test webhook on simulated failure (Java side and SQL side)
- [Test plan](/quality/test-plan) exists with every test case mapped to F-IDs / NFRs
- [Test results](/quality/test-results) exists from most recent CI run; all required test cases pass
- README documents how to run the JAR, required env vars, exit codes, observed performance

## 8. Open questions

Most Q1–Q3 answered by 2026-06-11. Q2 (Oracle txn ↔ SFDC line link) closed in **v6** via `erp_external_id` on `sfdc_order_line` and `erp_line_id` on `inv_transaction`. See [Evolution log](/database/evolution).
