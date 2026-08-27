---
title: Configuration
description: How the JAR resolves configuration — YAML defaults, env vars, and CONFIG_DIR overlays.
---

The JAR resolves configuration in three layers, evaluated in order (last wins):

```
1. application.yaml         (classpath) — defaults + shape, no secrets
2. application-<profile>.yaml (classpath) — per-environment overrides (dev/test/prod)
3. env vars                              — highest precedence (K8s Secret / ConfigMap)
```

Optional fourth layer via `CONFIG_DIR`:

```
2b. Every *.yaml / *.yml in ${CONFIG_DIR}, in alphabetical order
    (applied AFTER application-<profile>.yaml, BEFORE env vars)
```

`ConfigLoader` merges these into a validated `Config` record. Missing required fields fail-fast at startup with the field name in the exception message.

## Profile selection

`APP_PROFILE` chooses which `application-<profile>.yaml` to overlay:

- `dev` — local Postgres + LocalFolderFileSource
- `test` — Testcontainers Postgres (used by integration tests)
- `prod` — Azure Postgres + FilesComFileSource

Missing profile file is silently ignored — profile support is opt-in.

## CONFIG_DIR — centralized config

Pattern used by Michelin's DevOps centralized-config repository. Mount all shared config files into `/opt/app/config` and point `CONFIG_DIR` at it:

```
/opt/app/config/
├── 010-common-service-configs.yaml       (shared across all services)
└── 020-psql-inventory-integration-service.yaml  (this service's overrides)
```

Files load in alphabetical order → the service-specific file overrides the shared one. Env vars still win over everything.

Only YAML files (`.yaml` / `.yml`) are loaded. Other files in `CONFIG_DIR` are ignored.

## Reading the effective config

At startup, `ConfigLoader` logs a redacted config summary (secrets masked):

```
{ "profile":"prod", "run_mode":"scheduled",
  "db":{"url":"jdbc:postgresql://...","user":"inventoryledger_app","password":"***"},
  "file_source":"filescom","files_com":{"base_url":"...","api_key":"***"},
  "webhook":{"url_primary":"https://***"},
  "schedule":{"daily":"0 6 * * *","hourly":"5 * * * *"},
  "health":{"host":"0.0.0.0","port":8080} }
```

`toString()` on the `Config` record masks any field named `password`, `api_key`, or ending in `_url` (URLs may contain tokens).

## Required env vars

See [Environment variables](/reference/environment-variables) for the full list.

Absolute minimum to boot:

| Var | Value |
|---|---|
| `APP_PROFILE` | `prod` |
| `DB_URL` | JDBC URL with `?sslmode=require` |
| `DB_USER` | least-privilege DB role (`inventoryledger_app`) |
| `DB_PASSWORD` | secret |
| `FILE_SOURCE` | `filescom` (default) |
| `FILES_COM_API_KEY` | from files.com dashboard |
| `FILES_COM_PICKUP_PATH` | e.g. `/EU/PRD/BR/C10/Inbound` |
| `FILES_COM_ARCHIVE_PATH` | e.g. `/EU/PRD/BR/C10/Archive` |
| `FILES_COM_REJECT_PATH` | e.g. `/EU/PRD/BR/C10/Error` |
| `WEBHOOK_URL_PRIMARY` | outbox drain target |

## Schedule config

Two cron expressions, both env-overridable:

- `SCHEDULE_DAILY` (default `0 6 * * *`) — producer's nominal delivery time
- `SCHEDULE_HOURLY` (default `5 * * * *`) — catch-up

Either can be disabled by setting it to an empty string. At least one must be non-empty in scheduled mode.

**Time zone:** cron fires in the JVM's default TZ, set by the pod's `TZ` env var. Recommended: `Europe/Amsterdam` for Michelin's data source.

## Health endpoint config

- `HEALTH_HOST` — bind interface; default `0.0.0.0` (K8s pod IP)
- `HEALTH_PORT` — default `8080`
- (No auth — health is intentionally unauthenticated for K8s probes)

## File-source config per backend

Each backend has its own env-var namespace:

- `FILES_COM_*` — see [File sources](/architecture/file-sources#files-com-production)
- `SFTP_*` — see [File sources](/architecture/file-sources#sftp-fallback)
- `LOCAL_*` — see [File sources](/architecture/file-sources#local-folder-dev-test)

Only the vars for the active `FILE_SOURCE` are read; the others are ignored at runtime but flagged by `ConfigLoader` at startup if they look mis-set (typo hints).

## Flyway config

Two env vars:

- `FLYWAY_ENABLED` (default `true`) — set `false` in customer prod where DBServices owns the schema
- `FLYWAY_BASELINE_ON_MIGRATE` (default `false`)
- `FLYWAY_VALIDATE_ON_MIGRATE` (default `true`)

When disabled, `FlywayMigrator` logs `flyway-skipped FLYWAY_ENABLED=false` at startup and moves on. Useful in customer envs where the JAR's schema history is out of sync with the customer's `customer_install.sql`.

## Webhook config

- `WEBHOOK_URL_PRIMARY` — required
- `WEBHOOK_MAX_RETRIES` — default `5`
- `WEBHOOK_TIMEOUT_MS` — default `5000`
- `WEBHOOK_RETRY_BACKOFF_MS` — default `1000` (linear backoff × retry_count)
- `WEBHOOK_BATCH_SIZE` — default `50`
- `WEBHOOK_CONNECT_TIMEOUT_MS` — default `5000`

## Logging config

- `LOG_FORMAT` — `json` (prod default via `logback.xml`) or `text` (dev)
- `LOG_LEVEL` — `DEBUG` / `INFO` / `WARN` / `ERROR`

Structured JSON logs are picked up by the K8s log pipeline (Loki / Elastic).

## Config validation

`ConfigLoader` fails fast on:

- Missing required field → exception names the field
- Unparseable int → exception names the key + the actual value
- Unparseable boolean → same
- Invalid cron expression → cron-utils's own error message + the offending expression
- FILE_SOURCE=filescom without FILES_COM_API_KEY (or FILE_SOURCE=sftp without SFTP_HOST)

Startup exits with exit code 1 (config error) if validation fails.

## What NOT to configure at runtime

- The interface catalog YAML — shipped inside the JAR under `src/main/resources/interfaces/`. Column additions require a rebuild.
- The DB schema — owned by DBServices. Applied by the customer DBA via `customer_install.sql` + `alter_*.sql`.
- The trigger set — same as above.

Runtime knobs are for behavior (schedule, retry counts, timeouts, endpoints), not for shape (columns, tables, triggers).
