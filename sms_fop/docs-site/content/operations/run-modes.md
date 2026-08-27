---
title: Run modes
description: oneshot vs scheduled — when to use which.
---

The JAR runs in one of two modes, chosen at startup by `RUN_MODE`:

| Mode | Description | Default for |
|---|---|---|
| `scheduled` | Long-running JVM with internal cron scheduler + `/actuator/health` | prod (customer K8s Deployment) |
| `oneshot` | Original ADR-0003 batch: `Application.run()` once, exit | dev, CI, ad-hoc backfill |

Unset defaults to `oneshot` for backward compatibility with existing dev/CI harnesses.

## Deep-dive lives elsewhere

- [Scheduler and lifecycle](/architecture/scheduler-lifecycle) — thread model, signal handling, health endpoint
- [Architecture overview](/architecture/overview) — process topology diagram

This page is the operator's quick reference.

## When to use each

**Use `scheduled` in production.** The K8s Deployment expects a long-running pod. Startup + tear-down cost is amortized across weeks. Health probes need something to answer.

**Use `oneshot` for:**

- **Dev** — `scripts/run-dev.sh` runs the pipeline once against local Postgres + local files
- **CI** — every integration test runs the pipeline in `oneshot` mode
- **Ad-hoc backfill** — one-off `kubectl exec` or a shell run to reprocess a specific file
- **Debugging** — attach a debugger, hit run, exit. No scheduler thread to interfere.

Same JAR. Same code paths for parse / promote / archive. Only difference is `App.main` picks one branch or the other.

## Startup script differences

### `scheduled` (Kubernetes-style)

```bash
export RUN_MODE=scheduled
export APP_PROFILE=prod
# … all the DB / files.com / webhook vars …
java -jar psql-inventory-integration-service-1.0.0.jar
# Process stays up. Ctrl-C or SIGTERM to shut down gracefully.
```

Expected output:

```
[INFO] startup complete
[INFO] health-endpoint listening on 0.0.0.0:8080
[INFO] scheduler-loop-started schedules=[daily(0 6 * * *), hourly(5 * * * *)]
[INFO] next-fire at=2026-07-21T06:00+02:00 in=<N> ms
   … waits …
[INFO] tick-start
[INFO] file-picked file=MICH_INV_STOCKLEVEL_SMS_413.20260721.LSF.cfo
[INFO] file-parsed file=…  rows=284
[INFO] scheduled-run-complete latency=1240 ms files_processed=1
```

### `oneshot` (dev)

```bash
export RUN_MODE=oneshot
export APP_PROFILE=dev
# … dev-local DB + local folder …
java -jar psql-inventory-integration-service-1.0.0.jar
# Runs once. Exits with code 0 on success.
```

Expected output:

```
[INFO] startup complete (mode=oneshot)
[INFO] run-start
[INFO] file-picked file=…
[INFO] file-parsed file=… rows=…
[INFO] run-complete
[INFO] outbox-drained delivered=N failed=0
[INFO] shutdown complete
```

## What each mode enables

| Feature | `oneshot` | `scheduled` |
|---|---|---|
| Config load + validate | ✓ | ✓ |
| DB connection | ✓ (opened, closed after run) | ✓ (opened, reused across runs) |
| Flyway migrate | ✓ (unless `FLYWAY_ENABLED=false`) | ✓ (once at startup) |
| Pipeline `Application.run()` | once | on every cron tick |
| Cron schedules | not read | `SCHEDULE_DAILY` + `SCHEDULE_HOURLY` |
| `/actuator/health` | not started | `HealthEndpoint` on `HEALTH_HOST:HEALTH_PORT` |
| `daemon.heartbeat` events | not emitted | every N minutes via `HeartbeatEmitter` |
| SIGTERM handling | JDK default | graceful drain (stop scheduler → wait in-flight tick → close DB → exit 0) |
| Exit code | 0 / 1 / 2 / 3 / 4 per outcome | 0 on SIGTERM, non-zero only on fatal startup |

## Shared behavior

Both modes:

- Load the same `Config` (same YAML overlay, same env vars, same `CONFIG_DIR`)
- Use the same `FileSource` implementation (`filescom` / `sftp` / `local`)
- Use the same `CatalogIngestPipeline` (parse / map / validate / stage)
- Write to the same `staging.*` and `processed.*` tables
- Drain `notification_outbox` at end of run
- Write to `audit.event_log` for every file event

Only the outer loop and lifecycle differ.

## Anti-patterns

- **Do not run `scheduled` mode in CI.** CI needs deterministic exit codes and a hard time boundary. Scheduled mode never exits on its own.
- **Do not run `oneshot` mode in production K8s.** The pod will exit immediately after the first pipeline pass, and K8s will restart it in a crash-loop. Use `scheduled` in prod.
- **Do not depend on env vars being set differently between modes.** Same env vars work in both — mode-specific vars (`SCHEDULE_*`, `HEALTH_*`) are just ignored in `oneshot`.

## Switching modes

Requires a pod restart — mode is picked at `main()` entry. Not a hot-reload.

Rolling deploy:

```bash
kubectl set env deploy/psql-inventory-integration-service RUN_MODE=scheduled
kubectl rollout restart deploy/psql-inventory-integration-service
```

Or edit the ConfigMap and let the pod restart naturally.
