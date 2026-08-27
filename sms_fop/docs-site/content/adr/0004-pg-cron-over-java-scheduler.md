---
title: ADR-0004 - `pg_cron` over a Java scheduler
description: Architecture Decision Record 0004 - `pg_cron` over a Java scheduler
---


**Status:** Accepted · **Date:** 2026-05-18

## Context

Several recurring tasks need to run on a schedule:
- `refresh_mv_atp` (every few minutes)
- `partman.run_maintenance` (every 30 min, creates new partitions and detaches old ones)
- `f_purge_staging` (daily)
- `f_drift_detect` (daily)

We could implement these as a `ScheduledExecutorService` in the Java process, but that contradicts [ADR-0003](0003-batch-jar-no-daemon.md) (the JAR exits after the daily run). Alternatively, ops could schedule them externally (multiple invocations of separate JARs), which is brittle and creates many independent jobs to monitor.

Azure Database for PostgreSQL Flexible Server supports `pg_cron` (via the `azure.extensions` allowlist), so a database-resident scheduler is available without adding new infrastructure.

## Decision

All recurring SQL work lives in `pg_cron` jobs, scheduled by V3 migration:

| jobname | cron | command |
|---|---|---|
| `inventoryledger_partman_maintenance` | `*/30 * * * *` | `SELECT partman.run_maintenance(p_analyze := false)` |
| `inventoryledger_refresh_mv_atp` | `*/5 * * * *` | `SELECT refresh_mv_atp()` |
| `inventoryledger_purge_staging` | `15 3 * * *` | `SELECT f_purge_staging()` |
| `inventoryledger_drift_detect` | `0 4 * * *` | `SELECT f_drift_detect()` |

Schedules are initial defaults — ops may live-edit them via `cron.alter_job()` without redeploying anything. Business thresholds the jobs read (retention days, sample size, etc.) live in `pipeline_config` and are tunable via UPDATE.

## Consequences

**Positive**
- One scheduler in the database, fully observable via `cron.job_run_details`.
- Survives Java pod absence — recurring work continues regardless of when the daily JAR runs.
- pg_cron jobs run inside a real transaction with the DB's connection, no JDBC round-trip needed.
- Schedule changes are SQL, not code — no redeploy for tuning.

**Negative**
- Operational coupling to a Postgres extension. If we migrate off Azure Flexible (which allowlists pg_cron) to a host that doesn't support it, all jobs must be re-homed.
- Local dev needs `pg_cron` installed in the Postgres image. Documented; included in the test image.
- pg_cron jobs run as the postgres role on Azure (or `azure_pg_admin`). Care needed when granting access to functions they call.

## Alternatives considered

- **Java `ScheduledExecutorService` in a separate daemon JAR** — rejected; reintroduces the long-running-daemon problem (see ADR-0003).
- **System cron + `psql -c "SELECT ..."`** — rejected; couples job execution to a single host, breaks under HA.
- **External job runner (Kubernetes CronJob × 4)** — rejected; four extra deployment manifests for what is essentially "run this SQL on a schedule."
