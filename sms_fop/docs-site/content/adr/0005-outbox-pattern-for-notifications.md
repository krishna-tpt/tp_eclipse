---
title: ADR-0005 - Outbox pattern for failure notifications
description: Architecture Decision Record 0005 - Outbox pattern for failure notifications
---


**Status:** Accepted · **Date:** 2026-05-18

## Context

Failures originate from two places: the Java daily run (file processing, COPY, function calls), and Postgres-side scheduled jobs (`f_purge_staging`, `f_drift_detect`, `partman.run_maintenance`). Both must be surfaced to ops via webhooks (Slack/Teams). Doing webhook POSTs directly from Postgres requires `pg_net` or similar, and inside a trigger or pg_cron job a webhook failure is hard to recover from cleanly.

A daemon with LISTEN/NOTIFY would let Postgres notify Java in real time, but [ADR-0003](0003-batch-jar-no-daemon.md) precludes a daemon.

## Decision

Use the **transactional outbox pattern**:
1. Any failing code path (Java or SQL) inserts a row into `notification_outbox` with status=`pending`, severity, source, message, payload.
2. Once per daily Java run, `OutboxDrainer` selects pending + failed rows below `max_retries`, POSTs each to the configured webhook URLs, and updates status to `delivered` / `failed` / `failed_permanent`.
3. Failed rows below the retry cap are picked up by the next daily run.

The outbox table participates in the same transaction as the failure event — so the notification record survives even if the worker that observed the failure crashes mid-transaction.

## Consequences

**Positive**
- No webhook coupling inside triggers or pg_cron jobs — they just INSERT a row.
- Survives Java pod absence — pending notifications wait until the next run.
- Survives webhook outages — failed rows retry until `max_retries` is reached, then permanent-fail.
- Single observability surface: `SELECT count(*), status FROM notification_outbox` shows backlog.

**Negative**
- Notifications are not real-time. Latency = time until next daily run (worst case ~24 h). Acceptable for our SLO; would not be acceptable for an alerting platform.
- The outbox table grows over time. Mitigated by purging delivered rows older than the retention window in `f_purge_staging` (TODO: configurable).

## Alternatives considered

- **Direct webhook POST from `RAISE NOTICE` / pg_net inside triggers** — rejected. Webhook latency would block the calling transaction. Failure handling inside triggers is awkward.
- **LISTEN/NOTIFY consumer in Java** — rejected; requires a long-running daemon (see ADR-0003).
- **Kafka / queue-based event bus** — rejected as far over-engineered at 5k/day.
