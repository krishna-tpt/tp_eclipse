---
title: Scheduler and lifecycle
description: RUN_MODE dispatch, cron ticks, health probes, and graceful shutdown.
---

Introduced in **ADR-0014**. Flips the execution model from single-shot batch to long-running JVM with an internal cron scheduler. The single-shot behavior survives as `RUN_MODE=oneshot` for dev/CI/ad-hoc runs.

## Why long-running

The customer's DevOps platform (`nl-integration-services`) expects a K8s `Deployment` with `/actuator/health` probes, not a `CronJob`. Two consequences:

1. The JVM must stay up and answer probes 24/7.
2. Scheduling of the ingest cadence moves inside the JVM (via `cron-utils`) since there is no external `CronJob` to trigger us.

The dev/CI story doesn't want a long-running process — tests need to run the pipeline once and exit. So we keep both modes.

## `RUN_MODE` dispatch

`App.main` reads `RUN_MODE` from the environment and picks a code path:

| Value | Behavior | Default for |
|---|---|---|
| `scheduled` | Long-running with SchedulerLoop + HealthEndpoint | production (customer's K8s Deployment) |
| `oneshot` | Original ADR-0003 batch: Application.run() once, exit | dev, CI, ad-hoc backfill |

An unset value defaults to `oneshot` for backward compatibility with existing dev/CI harnesses. Production sets `RUN_MODE=scheduled` explicitly.

## Scheduled mode: threads and lifecycle

Three threads matter:

1. **Main thread** — completes startup, parks on a barrier, wakes only on shutdown to close resources and exit.
2. **Scheduler thread** — owned by `SchedulerLoop`. Sleeps until the next cron fire-time, wakes, runs `Application.run()` under a mutex, sleeps again.
3. **Health thread** — owned by `HealthEndpoint` (JDK `HttpServer`). Handles GET `/actuator/health` on `HEALTH_HOST:HEALTH_PORT`. Response is a small JSON blob with `status` (`UP` / `DOWN`) and reasons.

### SchedulerLoop details

```java
while (!Thread.currentThread().isInterrupted()) {
    Instant nextFire = schedules.stream()
        .map(cron -> cron.nextFireAfter(clock.instant()))
        .min(Comparator.naturalOrder())
        .orElseThrow();
    long sleepMs = Math.max(0, Duration.between(clock.instant(), nextFire).toMillis());
    Thread.sleep(sleepMs);

    synchronized (runLock) {
        try {
            long start = System.currentTimeMillis();
            application.run();
            log.info("scheduled-run-complete latency={} ms", System.currentTimeMillis() - start);
        } catch (Throwable t) {
            log.warn("scheduled run failed — next tick still fires", t);
        }
    }
}
```

Failures are logged at WARN and swallowed by the loop — the next tick still fires. A file-level or DB-level failure inside `Application.run()` does not kill the scheduler.

The `synchronized (runLock)` mutex is the scheduler-side guarantee that two overlapping schedules (e.g., daily and hourly firing on the same minute) do not run the pipeline concurrently.

### Configuring the schedules

Two env-var-configurable cron expressions:

- `SCHEDULE_DAILY` (default `0 6 * * *`) — the producer's nominal delivery time
- `SCHEDULE_HOURLY` (default `5 * * * *`) — hourly catch-up at `HH:05`

Either can be disabled by setting it to an empty string. At least one must be non-empty. Both use standard 5-field Unix cron via `com.cronutils:cron-utils`.

Cron fires in the JVM's default time zone, which the pod sets via the `TZ` env var. Documented default is Europe/Amsterdam for Michelin's data source.

### HealthEndpoint details

Tiny `com.sun.net.httpserver.HttpServer`. One handler at `/actuator/health`. Response:

```json
{
  "status": "UP",
  "checks": {
    "db":        { "status": "UP" },
    "scheduler": { "status": "UP" }
  }
}
```

Bound to `HEALTH_HOST:HEALTH_PORT` (defaults `0.0.0.0:8080`). K8s liveness AND readiness probes point at the same URL.

Status resolution:

- `db` — `SELECT 1` with a 2 s timeout. Fails → `DOWN`, reason `db-ping-failed`.
- `scheduler` — the SchedulerLoop's thread reports `isAlive() && !isInterrupted()`. Fails → `DOWN`, reason `scheduler-dead`.
- Any check `DOWN` → HTTP 503, otherwise HTTP 200.

## Graceful shutdown

K8s sends SIGTERM to the pod. Our JVM shutdown hook does, in order:

1. **Interrupt** the scheduler thread — if it's mid-sleep, the interrupt wakes it and it exits the loop.
2. **Wait** for the in-flight `Application.run()` (if any) to release the mutex. No new tick starts.
3. **Stop** the health server (`HttpServer.stop(0)`).
4. **Close** the JDBC connection. If close throws, log at WARN — we're exiting anyway.

The pod exits cleanly. `terminationGracePeriodSeconds` in the K8s deployment YAML should be ≥ 120 s to give a long-running tick time to finish. A tick killed mid-transaction rolls back cleanly (see [Failure modes](/architecture/overview#failure-modes)).

## Oneshot mode

No scheduler, no health server, no signal handling beyond the JVM default. `Application.run()` executes once; on success exit 0, on unrecoverable failure exit non-zero per `ExitCodeMapper`. Every unit and integration test uses this mode. `scripts/run-dev.sh` uses this mode.

## Failure isolation summary

| Failure | Behavior in scheduled mode | Behavior in oneshot mode |
|---|---|---|
| One file fails to parse | Rejected, event logged, next file processed | Same |
| DB unreachable mid-tick | Tick logs WARN, health probe reports `db-ping-failed` next check → K8s restart | Exit code 2 |
| SchedulerLoop thread crashes | Health probe reports `scheduler-dead` → K8s restart | n/a |
| SIGTERM received | Graceful drain + exit 0 | Same |
| Application.run throws | WARN, next tick still fires | Exit non-zero |
