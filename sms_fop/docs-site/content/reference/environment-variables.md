---
title: Environment variables
description: Every var the JAR reads — required vs optional, with defaults.
---

Source of truth: `.env.example` in the repo. Every var here is consumed by `ConfigLoader` at startup.

**Contract:**

- **REQUIRED** vars have no default. JAR refuses to start if any are unset (exit code 1).
- **OPTIONAL** vars have a default in `application.yaml`. Listed for discoverability — only set when overriding.
- Naming: the env var name IS the placeholder in `application.yaml`. No translation.
- Secrets: real values live in `.env` (gitignored) or K8s Secret. Never commit real values.

## Required

### App

| Var | Values | Notes |
|---|---|---|
| `APP_PROFILE` | `dev` / `test` / `prod` | Chooses `application-<profile>.yaml` overlay |

### Database

| Var | Notes |
|---|---|
| `DB_URL` | JDBC URL. Include `?sslmode=require` in prod (e.g., `jdbc:postgresql://host:5432/dbname?sslmode=require`) |
| `DB_USER` | Least-privilege role (`inventoryledger_app`) |
| `DB_PASSWORD` | Secret. Loaded from vault or K8s Secret. |

### Webhook

| Var | Notes |
|---|---|
| `WEBHOOK_URL_PRIMARY` | Failure notification endpoint |

### File source (when `FILE_SOURCE=filescom`, the default)

| Var | Notes |
|---|---|
| `FILES_COM_API_KEY` | From files.com dashboard |
| `FILES_COM_PICKUP_PATH` | e.g., `/EU/PRD/BR/C10/Inbound` |
| `FILES_COM_ARCHIVE_PATH` | e.g., `/EU/PRD/BR/C10/Archive` |
| `FILES_COM_REJECT_PATH` | e.g., `/EU/PRD/BR/C10/Error` |

## Optional (defaults in parentheses)

### Run mode (ADR-0014)

| Var | Default | Notes |
|---|---|---|
| `RUN_MODE` | `oneshot` (unset) | `scheduled` for prod. See [Run modes](/operations/run-modes) |

### Scheduler — only honored in `RUN_MODE=scheduled`

| Var | Default | Notes |
|---|---|---|
| `SCHEDULE_DAILY` | `0 6 * * *` | Producer's nominal delivery time. Empty string to disable. |
| `SCHEDULE_HOURLY` | `5 * * * *` | Hourly catch-up at `HH:05`. Empty string to disable. |

At least one schedule must be non-empty.

### Health endpoint — only in `scheduled` mode

| Var | Default | Notes |
|---|---|---|
| `HEALTH_HOST` | `0.0.0.0` | Bind interface |
| `HEALTH_PORT` | `8080` | K8s liveness + readiness probes hit this |

### External config overlay

| Var | Default | Notes |
|---|---|---|
| `CONFIG_DIR` | (unset) | Directory of `*.yaml` / `*.yml` files loaded in alphabetical order as an overlay. See [Configuration](/operations/configuration#config_dir-centralized-config) |

### Database tuning

| Var | Default | Notes |
|---|---|---|
| `DB_CONNECT_TIMEOUT_S` | `10` | JDBC connect timeout |
| `DB_STATEMENT_TIMEOUT_MS` | `60000` | Per-statement server-side timeout |
| `DB_APP_NAME` | `inventoryledger` | Reported to `pg_stat_activity` |

### Flyway

| Var | Default | Notes |
|---|---|---|
| `FLYWAY_ENABLED` | `true` | **Set `false` in customer prod** — DBServices owns the schema |
| `FLYWAY_BASELINE_ON_MIGRATE` | `false` | |
| `FLYWAY_VALIDATE_ON_MIGRATE` | `true` | |

### File source selection

| Var | Default | Values |
|---|---|---|
| `FILE_SOURCE` | `filescom` | `filescom` / `sftp` / `local` |
| `FILE_NAME_PATTERN` | `^MICH_INV_STOCKLEVEL_413\d{14}\.dat$` | Regex; overrides the catalog's default pattern |

### files.com tuning (when `FILE_SOURCE=filescom`)

| Var | Default | Notes |
|---|---|---|
| `FILES_COM_BASE_URL` | (SDK default `app.files.com`) | Michelin uses `https://filehub.michelin.net` |
| `FILES_COM_CONNECT_TIMEOUT_MS` | `10000` | |
| `FILES_COM_READ_TIMEOUT_MS` | `60000` | |
| `FILES_COM_PAGE_SIZE` | `200` | List page size |

### SFTP (when `FILE_SOURCE=sftp`)

| Var | Default | Notes |
|---|---|---|
| `SFTP_HOST` | (required if sftp) | |
| `SFTP_PORT` | `22` | |
| `SFTP_USER` | (required if sftp) | |
| `SFTP_PASSWORD` | (either password or key) | |
| `SFTP_PRIVATE_KEY_PATH` | | PEM file readable by JAR process. Key wins over password if both set |
| `SFTP_PRIVATE_KEY_PASSPHRASE` | | Blank if key is unencrypted |
| `SFTP_KNOWN_HOSTS_PATH` | (unset — accepts any) | Production posture: pin host key. Blank = insecure, dev only |
| `SFTP_PICKUP_PATH` | `/inbound` | |
| `SFTP_ARCHIVE_PATH` | `/archive` | |
| `SFTP_REJECT_PATH` | `/rejected` | |
| `SFTP_CONNECT_TIMEOUT_MS` | `10000` | |

### Local folder (when `FILE_SOURCE=local`)

| Var | Default | Notes |
|---|---|---|
| `LOCAL_PICKUP_PATH` | `./inbound` | |
| `LOCAL_ARCHIVE_PATH` | `./archive` | |
| `LOCAL_REJECT_PATH` | `./rejected` | |

### Webhook tuning

| Var | Default | Notes |
|---|---|---|
| `WEBHOOK_MAX_RETRIES` | `5` | Then `failed_permanent` |
| `WEBHOOK_TIMEOUT_MS` | `5000` | Per-attempt HTTP timeout |
| `WEBHOOK_RETRY_BACKOFF_MS` | `1000` | Linear backoff × `retry_count` |
| `WEBHOOK_BATCH_SIZE` | `50` | Max outbox rows drained per tick |
| `WEBHOOK_CONNECT_TIMEOUT_MS` | `5000` | |

### Job behavior

| Var | Default | Notes |
|---|---|---|
| `EXIT_ON_FIRST_FAILURE` | `false` | Continue with next file on per-file failure |
| `DRAIN_OUTBOX` | `true` | Drain outbox at end of tick. Set `false` to isolate ingest for debugging |
| `TEMP_FILE_PREFIX` | `ob-` | Java temp file prefix for downloaded files |
| `TIMESTAMP_PATTERN` | `yyyyMMdd_HHmmss` | Archive filename suffix pattern |

### Logging

| Var | Default | Notes |
|---|---|---|
| `LOG_FORMAT` | `json` (prod), `text` (dev) | Encoder selection via `logback.xml` profile |
| `LOG_LEVEL` | `INFO` | `DEBUG` / `INFO` / `WARN` / `ERROR` |

### Synthetic data tools (dev only)

| Var | Default | Notes |
|---|---|---|
| `TOOLS_SEED` | `42` | Deterministic RNG seed for `filemanager-data-tools` |

## Resolution order (last wins)

```
1. application.yaml               (classpath defaults + shape)
2. application-<profile>.yaml      (profile overlay if present)
3. Every *.yaml / *.yml in $CONFIG_DIR, alphabetically  (if $CONFIG_DIR set)
4. Environment variables           (highest)
```

## Fail-fast validation

`ConfigLoader` exits with code 1 (config error) if:

- Any REQUIRED var is unset
- A numeric var can't parse as int (naming the key + the actual value)
- A boolean var can't parse
- A cron expression is invalid (cron-utils error + the offending expression)
- `FILE_SOURCE=filescom` and `FILES_COM_API_KEY` is unset
- `FILE_SOURCE=sftp` and `SFTP_HOST` is unset (and no valid auth)
- Neither `SCHEDULE_DAILY` nor `SCHEDULE_HOURLY` is non-empty in `scheduled` mode

Startup logs the effective config (redacted — secrets masked) so operators can verify what actually got loaded.

## Secret masking

`Config.toString()` masks fields matching any of:

- Name `password` (exact or ends-with)
- Name `api_key`
- Name ending in `_url` (URLs may embed tokens)

Redacted values render as `***`. Safe to include the config dump in logs and support tickets.

## What isn't an env var

- **Interface catalog** — YAML files under `filemanager-core/src/main/resources/interfaces/`. Shipped in the JAR. Rebuild to change.
- **DB schema** — customer-owned. Applied via `customer_install.sql` + `alter_*.sql`.
- **Log format layout** — `logback.xml` in the JAR. Rebuild to change.

Configuration knobs are for behavior. Shape lives in code / SQL / YAML in the JAR.
