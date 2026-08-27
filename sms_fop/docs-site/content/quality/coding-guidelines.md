---
title: Coding guidelines
description: The non-negotiable rules that shape this codebase.
---

Extracted from ADRs 0001–0014 and reinforced by production-readiness review. Every rule here has surfaced a bug (or would have) if broken.

## 1. No hard-coding (ADR-0009)

Every threshold, path, URL, timeout, batch size, schedule, file pattern, retry count, retention window comes from `application.yaml` + env vars + `CONFIG_DIR` overlay. Validated at startup; fail-fast on missing values.

Permitted `static final` constants:

- Regex patterns compiled once
- Fixed health-check SQL (`SELECT 1`)
- JDBC parameter names
- Structural constants (`PostgreSQL DEFAULT SCHEMA = "public"`)

Never as static:

- Timeouts
- Retry counts
- URLs
- File paths
- Schedule expressions
- File name patterns
- Retention windows

**Enforcement:** `NoHardcodedSecretsTest` in `filemanager-data-tools`. Code review checks for numeric literals in production sources.

## 2. Test-driven development (ADR-0010)

Red → green → refactor. Every public SQL function and every Java class has tests **before** code is merged.

Rules:

- Test names describe behavior, not implementation ("rejects invalid_qty" not "testProcessBadQty")
- Every fixture is a file, never an inline literal
- Every TC-NNN maps to an F-ID or NFR in the [spec](/overview/spec)
- Coverage gate: ≥ 80% line on production sources, 100% on critical paths

## 3. Synthetic data separation (ADR-0011)

`filemanager-data-tools` is a separate Maven module. It:

- Generates opening-balance CSVs, transaction payloads, order payloads for dev / test / perf
- Never bundled into the production `filemanager-core` JAR
- Depends on `filemanager-core` only for shared DTOs / contracts
- Never published to production registries
- Has its own `tools.yaml` config; never references production secrets

## 4. Plain Java, no framework (ADR-0001)

No Quarkus, Spring, Micronaut, JPA, Hibernate, jOOQ, JDBI. Standard Java 21 + a curated set of single-purpose libraries.

Consequences:

- Constructor injection (no `@Inject`, no DI container)
- Config → `Config` record via `ConfigLoader`; no `@ConfigurationProperties`
- HTTP → `java.net.http.HttpClient` for outbound; JDK `com.sun.net.httpserver.HttpServer` for `/actuator/health`
- Logging → SLF4J → Logback with JSON encoder in prod
- Records for DTOs; classes only when you need methods

Why: matches existing TenthPlanet style (`smslite-filemanager`); minimal dependency surface; predictable at scale-up.

## 5. Raw JDBC, single connection (ADR-0002)

- No connection pool. One `Connection` per Application.run, reused across per-file transactions.
- Every DDL / DML uses parameterized `PreparedStatement`. No string concatenation into SQL.
- `INSERT ... COPY` for staging batches (via `CopyManager`).
- Transactions explicit: `conn.setAutoCommit(false)` at the top of each per-file operation; `commit()` or `rollback()` explicit.

## 6. No inbound REST (ADR-0007)

Our service exposes:

- `GET /actuator/health` on `HEALTH_HOST:HEALTH_PORT` — for K8s probes

That's it. No business REST. No admin endpoints. Every business flow inbound to the DB is either:

- Java file loader (this service)
- FOP / SFDC direct DB writes to `staging.*_inbox`

Reads out of the DB go via `fetch_inventory_json` called by SFDC through their own HTTP tier (PostgREST / Heroku Connect / whatever they use).

## 7. `pg_cron` when we need scheduling in the DB (ADR-0004)

But not in the v6 customer package — the customer runs their own scheduler (Airflow / K8s CronJob), and they don't want `pg_cron` enabled on their Azure Flexible Server.

**When we do use pg_cron:** it lives in the DB, observable via `cron.job_run_details`, and survives Java pod absence.

## 8. Outbox pattern for notifications (ADR-0005)

Failure notifications go to `processed.notification_outbox` (DB table), drained by the Java daemon via webhook.

Why: reliable async — survives pod restarts, PG-side failures, network hiccups. No in-Java retry loop needed.

## 9. Ops team owns containerization (ADR-0008)

We deliver:

- A runnable JAR
- Documented env vars
- Documented exit codes
- Health endpoint spec

Ops owns:

- Dockerfile
- K8s manifests (Deployment, ConfigMap, Secret)
- Helm / Kustomize
- Secret sourcing (Vault, Azure Key Vault, K8s Secret)

Our JAR must not care whether it's running in a container, VM, or on bare metal.

## 10. Long-running with internal scheduler (ADR-0014)

Two modes: `oneshot` (dev/CI) and `scheduled` (prod). See [Run modes](/operations/run-modes) and [Scheduler and lifecycle](/architecture/scheduler-lifecycle).

## 11. Files.com is the production file source (ADR-0012)

`FilesComFileSource` is the default. SFTP demoted to a sibling implementation for fallback. Local folder retained for dev/test.

## 12. SFTP with SSH keys (ADR-0013)

When SFTP is used, prefer key-based auth over password. Set `SFTP_PRIVATE_KEY_PATH` (PEM). Optionally pin the server key via `SFTP_KNOWN_HOSTS_PATH`.

## Naming conventions

### Database

- All object names lowercase snake_case: `stock_balance`, `f_promote_txn`, `idx_opening_balance_key`
- No spaces, no quotes required
- Function prefix `f_` for trigger functions; unprefixed for direct-callable functions
- Trigger prefix `trg_`
- Index prefix `idx_` (query indexes) or `ix_` (audit / operational)
- Constraint prefix `chk_` (checks), `fk_` (foreign keys), `uq_` (uniques)

### Java

- Package: `org.michelin.filemanager.<domain>` (config, db, file, ingest, mapper, catalog, etc.)
- Classes: PascalCase, noun-first (`CatalogIngestPipeline`, `HealthEndpoint`)
- Methods: verb-first (`ingest`, `list`, `moveToArchive`)
- Exceptions: end with `Exception`, name the domain (`FieldMappingException`, `EnvelopeValidationException`)

### YAML

- Interface: `<INTERFACE>_<VARIANT>.yaml` — one per file family
- Camel-case not used; underscore snake_case follows DB convention
- Column names in `columns:` block match the target table's column names 1:1

## Security postures

### Roles + grants

Three least-privilege DB roles:

- `inventoryledger_app` — file loader (Java daemon). Grants: INSERT on staging tables, EXECUTE on `staging.load_stocklevel`, INSERT/UPDATE on `notification_outbox`, INSERT on `audit.event_log`.
- `inventoryledger_writer` — FOP + SFDC integrations. Grants: INSERT on `staging.txn_inbox` / `staging.order_inbox` (their triggers do the promote as SECURITY DEFINER).
- `inventoryledger_reader` — SFDC read tier. Grants: EXECUTE on `processed.fetch_inventory_json_observed`.

None of these roles has direct SELECT / INSERT / UPDATE / DELETE on `processed.*` tables.

### Tenant isolation is upstream

The DB does NOT bind identity to tenant. `app.tenant_id` is a session variable the calling service sets. See TC-C02 / TC-I01 in [Test results](/quality/test-results#documented-residual-risk).

The calling service MUST:

1. Authenticate the user at the edge (mTLS, OAuth, signed JWT).
2. Resolve the user's permitted tenant set from the identity store.
3. Validate the inbound tenant claim is in the permitted set.
4. `SET LOCAL app.tenant_id = '<validated>'` inside a transaction before calling any function.

Skipping step 3 lets a compromised service role reach any tenant.

### `SECURITY DEFINER` + explicit search_path

Every function is `SECURITY DEFINER` with `SET search_path = processed, staging, audit, public, pg_catalog`. Protects against `search_path` attacks.

Every function has `REVOKE ALL FROM PUBLIC; GRANT EXECUTE TO <specific role>` — no public execute anywhere.

### No secrets in code

- No hard-coded passwords, API keys, or URLs
- `Config.toString()` masks any field named `password`, `api_key`, or ending in `_url`
- `NoHardcodedSecretsTest` fails the build if a secret literal appears

## Style rules for markdown docs

Applies to `SPEC.md`, `ARCHITECTURE.md`, this docs site, ADRs:

- One `#` per file (title)
- Use `##` and `###` for section hierarchy
- Tables preferred for structured data (columns, TC-IDs, rate limits)
- Code blocks fenced with language tag (```bash, ```sql, ```java)
- No trailing whitespace
- No emojis in prose or code

## Alter file conventions

Post-v6 additive features ship as `alter_<name>.sql` in `deploy/11-06-v6-customer/`, not as new tag folders. Rules:

- Every DDL uses `CREATE IF NOT EXISTS` / `CREATE OR REPLACE`
- Every DML uses `ON CONFLICT DO NOTHING` / `INSERT ... WHERE NOT EXISTS`
- Every function is `CREATE OR REPLACE`
- Files number-prefixed for apply order (`alter_01_*.sql`, `alter_02_*.sql`, …)
- Safe to re-run against an already-installed DB (idempotent)
- Each alter file has a header comment explaining what it adds and why

## Git conventions

- Conventional commit format: `type(scope): description`
- Types: `feat`, `fix`, `docs`, `refactor`, `test`, `chore`
- Scope: module or feature name (`catalog`, `deploy`, `scheduler`, `audit`)
- Body explains why, not what
- Never commit secrets — `.env` is gitignored
- `.zip` / `.tar` / `.gz` / `.rar` / `.7z` are gitignored (customer GitLab blocks binary archives)
- Never `--amend` a pushed commit
- Never `--force` to main/master
