---
title: ADR-0014 - Long-running service with internal cron scheduler
description: Architecture Decision Record 0014 - Long-running service with internal cron scheduler
---


**Status:** Accepted · **Date:** 2026-06-23
**Supersedes:** [ADR-0003](0003-batch-jar-no-daemon.md) (single-shot batch JAR)

## Context

ADR-0003 (May 2026) chose a single-shot batch JAR invoked by an external
scheduler — a `CronJob`-like primitive owned by ops. That choice was right
for the assumed shape of work: one daily opening-balance load, idle the rest
of the day.

Two things changed:

1. **Cadence requirements grew.** The customer reload pattern is "expected at
   06:00, but occasionally retried by humans at unpredictable times during
   the day after a SFDC-side correction." A once-daily run is no longer
   sufficient; we need a recovery cadence (hourly catch-up) on top of the
   primary daily trigger.

2. **DevOps platform pattern is long-running-service-shaped.** The Michelin
   `nl-integration-services` platform expects each service as a long-running
   pod with `/actuator/health`, centralized config mounted at
   `/opt/app/config/`, K8s `Deployment` (not `CronJob`), and uniform
   observability. Two CronJobs would force a fork in their platform model
   for this one service.

Three options were considered before reversing ADR-0003.

## Options considered

### A. Two K8s `CronJob`s — preserve ADR-0003

One CronJob fires at the configured time (e.g., `0 6 * * *`); the other
fires hourly (`0 * * * *`). Each spawns a pod that runs `App.main` once
and exits. The Java code does not change.

**Pros**: idiomatic for batch; zero resource cost between runs; one-shot
process — no leak risk on long-lived connections; failed run = K8s retry.

**Cons**: duplicates K8s manifests (image-pull, config mount, secret mount)
twice; no `/actuator/health` endpoint (CronJob pods don't take liveness
probes); deviates from the platform's standard service shape that the
DevOps team has tooling, dashboards, and runbooks built around.

### B. Long-running service with internal cron scheduler — chosen

Single pod stays up 24/7. A `SchedulerLoop` thread inside the JVM computes
the earliest next-fire across all configured cron expressions, sleeps until
it, invokes `Application.run()` under a mutex, and repeats. A tiny
JDK `HttpServer` exposes `/actuator/health` continuously.

**Pros**: matches the platform's standard service shape; `/actuator/health`
falls out naturally; schedules are env-var driven so DevOps configures them
in the central config repo without touching K8s YAML; single Deployment
manifest.

**Cons**: ~300 LOC of new scheduler + health code to maintain and test; JVM
held idle ~23 hours/day; needs explicit graceful-shutdown handling.

### C. Spring Boot wrapper

Add Spring Boot starter-actuator + `@Scheduled` annotations. Trivial to
write but pulls ~40 MB of dependencies into a shaded jar that today is 12 MB
and has no Spring in it. Rejected as disproportionate.

## Decision

Adopt option B. The platform-uniformity benefit and the natural fit for
`/actuator/health` outweigh the ~300 LOC and the idle JVM cost. The idle
cost is small in absolute terms (~256 MB heap), and the LOC investment is
one-time. Recurring operational overhead of two CronJob manifests with
duplicated config plumbing would be larger than the maintenance cost of a
single scheduler module.

## Implementation contract

- **`RUN_MODE` env var.** `oneshot` (default in dev profile) preserves the
  original ADR-0003 single-run behavior — used for tests, local dev,
  ad-hoc reruns, CI. `scheduled` (default in prod profile) starts the
  scheduler + health endpoint and blocks on SIGTERM. Switching modes is
  config only, no rebuild.

- **Two schedules by default.**
  - `schedule.daily` (`SCHEDULE_DAILY`, default `0 6 * * *`) — the
    expected run, lines up with the producer's nominal delivery time.
  - `schedule.hourly` (`SCHEDULE_HOURLY`, default `5 * * * *`) — the
    catch-up. The `5` offset reduces collision with on-the-hour daily-style
    schedules.

  Both are cron expressions; either can be set to empty string to disable.
  More schedules can be added with no code change — pass a comma-separated
  list of cron expressions (future extension).

- **Mutex.** A `synchronized` block on the run method serializes overlapping
  triggers. If the daily and hourly fire within milliseconds of each other,
  the second waits for the first to finish. Combined with
  `staging.stocklevel_batch.file_name UNIQUE`, double-pickup is impossible.

- **Failure isolation.** If `Application.run()` returns a non-zero exit code
  or throws, the scheduler logs WARN and continues to the next tick. One
  bad file does not silence the daily run.

- **No-files behavior.** Unchanged from ADR-0003. `Application.run()` already
  returns success (exit 0) when the file source yields zero files. The
  scheduler logs the result and goes back to wait.

- **Graceful shutdown.** SIGTERM from K8s → shutdown hook sets a flag and
  interrupts the scheduler thread → any in-flight `Application.run()`
  completes → health server stops → DB connection closes → exit. K8s
  `terminationGracePeriodSeconds` should be at least one ingest cycle (e.g.
  120 s) to allow in-flight runs to finish.

- **Health endpoint.** `GET /actuator/health` returns:
  - `200 {"status":"UP","db":"UP","scheduler":"UP"}` when both checks pass
  - `503 {"status":"DOWN","db":"DOWN","reason":"<error>"}` when DB ping fails
  - `503 {"status":"DOWN","scheduler":"DOWN"}` when the scheduler thread died

  Liveness probe = process responds. Readiness probe = the same endpoint
  with status UP. Configurable host (`HEALTH_HOST`, default `0.0.0.0`) and
  port (`HEALTH_PORT`, default `8080`).

## Consequences

**Positive**

- One K8s manifest (`Deployment`) per environment, matches DevOps's pattern.
- `/actuator/health` ships built-in — no follow-on work needed.
- Schedule changes are config-only (env var or centralized YAML), no jar
  rebuild.
- Multiple schedules trivially extensible (just add cron expressions).
- Sub-hour cadence available — no longer constrained by external cron
  granularity decisions.
- Single continuous log stream per pod simplifies debugging.

**Negative**

- JVM held warm ~23 hours/day. Modest resource cost (~256 MB heap, ~5
  Postgres connections idle).
- Memory-leak watch required. ADR-0003's "the process exists for seconds per
  day" hedge no longer applies — long-running JVMs need ongoing attention to
  steady-state allocation.
- More moving parts to test (scheduler thread lifecycle, health server,
  mutex behavior, shutdown ordering). Mitigated by the test plan that
  ships with this change.
- Single pod = single point of failure if it crashes between scheduled
  ticks. Mitigated by K8s pod-restart policy and the hourly catch-up
  recovering whatever the daily missed.

## What this does NOT change

- Postgres-side recurring work (MV refresh, partition archive) still runs
  under `pg_cron` — see [ADR-0004](0004-pg-cron-over-java-scheduler.md).
  Application-level scheduling is for the file ingest cycle only.
- The outbox-drain pattern (ADR-0005) is unchanged — every run still drains
  the outbox at the end.
- The existing `App.run()` contract is unchanged — same pipeline, same exit
  codes, same idempotency guarantees. The scheduler just calls it on a
  cadence.

## Relationship to ADR-0003

ADR-0003 is superseded by this ADR, not invalidated. Its reasoning was
correct for the constraints it described — once-daily work, no platform
expectation for long-running services, no `/actuator/health` requirement.
Those constraints changed; the decision changes with them.

The `oneshot` `RUN_MODE` preserves ADR-0003's batch-JAR semantics for any
context that still wants them (dev, CI, manual reruns, ad-hoc data
backfill). The two paths are not mutually exclusive — they coexist in one
binary.
