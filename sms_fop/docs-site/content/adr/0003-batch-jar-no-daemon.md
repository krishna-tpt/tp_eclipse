---
title: ADR-0003 - Single-shot batch JAR (no long-running daemon)
description: Architecture Decision Record 0003 - Single-shot batch JAR (no long-running daemon)
---


**Status:** Superseded by [ADR-0014](0014-long-running-with-internal-scheduler.md) (2026-06-23) · **Date:** 2026-05-18

> **Superseded.** The single-shot model was right for once-daily work with no
> platform expectation of a long-running service. Cadence requirements grew
> (hourly catch-up needed), and the Michelin `nl-integration-services`
> platform standardized on long-running pods with `/actuator/health`. ADR-0014
> reverses this decision; the `oneshot` `RUN_MODE` preserves this ADR's
> semantics for dev / CI / ad-hoc runs.

## Context

The Java service's only responsibilities are: (a) load the daily opening-balance file, and (b) drain the notification outbox. Both happen once per day. The first draft of this design assumed a long-running daemon (LISTEN/NOTIFY consumer, debounced MV refresher, scheduled-job pool, K8s Deployment).

That model adds operational surface — health probes, restart semantics, leak risks on long-lived connections, observability tooling — for a workload that only needs to do something once per day.

## Decision

The JAR is a single-shot batch process: `App.main` opens one connection, runs migrations, processes files, drains the outbox, exits. An external scheduler (chosen by ops) invokes it daily. No HTTP server. No `ScheduledExecutorService`. No `LISTEN/NOTIFY` thread.

All recurring work that previously needed an in-Java scheduler (MV refresh, partition archive, staging purge, drift detection) now lives in `pg_cron` inside Postgres — see [ADR-0004](0004-pg-cron-over-java-scheduler.md).

## Consequences

**Positive**
- The Java process exists for seconds-to-minutes per day. Crash radius is bounded.
- No need to manage long-lived JDBC connections, pool exhaustion, OOM trends, or LISTEN reconnection logic.
- K8s topology trivializes: a single `CronJob`-like resource managed by ops, no `Service`, `Deployment`, or `Ingress` needed.
- Memory footprint at idle is zero (no pod running).

**Negative**
- Real-time triggers (e.g. immediate MV refresh on each write) are not in our service. They live in pg_cron with a few-minute cadence. Sub-minute freshness needs a different mechanism.
- The "drain outbox" path runs only once a day. PG-side failures that write to `notification_outbox` are dispatched the next morning — fine for our SLOs (15-min RPO, daily run cadence) but not for real-time alerting.

## Alternatives considered

- **Long-running daemon with K8s Deployment + LISTEN/NOTIFY** — rejected as over-engineered for once-daily work. Reconsider only if requirements add sub-minute response.
- **Multiple smaller batch jobs** — rejected; one cohesive run is easier to monitor and operate.
