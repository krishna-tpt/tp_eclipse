# MicroService — Inventory Ledger File Manager (Java)

The Java half of the Inventory Ledger. Reads inventory snapshots from a
configurable file source (files.com by default; plain SFTP and a local folder
are siblings), decodes the dialect via the **catalog YAML**, and lands them in
`staging.*`. DBServices does everything from staging onward.

> **Scope:** This project does **only** the SFTP → staging path for opening
> balance files. Transaction and order ingestion are handled by other teams
> writing directly to `staging.txn_inbox` and `staging.order_inbox`. See
> the docs site (`docs-site/content/overview/scope.mdx`) for the full boundary.

---

## What it does

1. **Schedule** — internal cron (`schedule.cron` in `application.yaml`) or a 5-minute fallback if no cron set
2. **Pick up files** from the configured backend — files.com (default), SFTP, or a local folder during dev — matching the configured filename pattern
3. **Variant-detect** each file: filename regex → first envelope row's declared source — both must agree
4. **Parse** the file streaming line-by-line, splitting/dequoting per the variant's parser rules
5. **Map** each data row onto the target staging table per the variant's column rules (field source, envelope capture, literal)
6. **Validate** required fields, max length, allowed values, regex, footer row counts
7. **Insert** into `staging.*` in a single transaction per file
8. **Call** the DBServices promotion function for that file (e.g. `load_opening_balance(batch_id)`)
9. **Archive** the file on success or **reject** on failure (plus a notification row)
10. **Drain** `notification_outbox` over webhook at end of cycle

Steps 2–7 are entirely catalog-driven. Adding a column means editing one YAML
line + a Flyway migration on the DBServices side — no Java change.

---

## Package map

```
src/main/java/org/michelin/filemanager/
├── catalog/        ← interface-catalog: loader, variant detector, sealed result types
├── parser/         ← positional streaming parser (delimiter/quote/trim/empty-is-null)
├── mapper/         ← field mapper: validators + type coercion (LocalDate, BigDecimal …)
├── ingest/         ← orchestrator that ties detect → parse → map → footer-validate → INSERT
├── file/           ← FileSource SPI: Local, FilesCom (default), Sftp (password OR SSH key)
├── config/         ← Config records loaded from application.yaml + env-var override
├── db/             ← raw JDBC infra (Database, FlywayMigrator)
├── notifier/       ← outbox drainer + webhook posting
├── exception/      ← typed exceptions for clean exit codes
├── lifecycle/      ← exit-code mapper
├── pipeline/       ← (legacy CSV-header loader - kept for historical reference)
├── App.java        ← main()
└── Application.java
```

```
src/main/resources/
├── application.yaml + profile yamls
├── interfaces/                                    ← catalog YAMLs (the no-touch knob)
│   └── mich_inv_stocklevel_batch.yaml
└── logback.xml
```

---

## Build and test

```bash
# Compile + unit tests
./gradlew :filemanager-core:test

# Full integration (Testcontainers postgres:16-alpine)
./gradlew :filemanager-core:check

# Fat production JAR
./gradlew :filemanager-core:fatJar
ls filemanager-core/build/libs/psql-inventory-integration-service-1.0.0.jar
```

CI runs `./gradlew build` via the central pipeline template. Coverage gate
is 80% line, 100% on critical paths.

---

## Run

Production-shaped invocation lives in `../README.md`. Local dev:

```bash
APP_PROFILE=dev java -jar build/libs/psql-inventory-integration-service-1.0.0.jar
```

Dev defaults assume Postgres at `localhost:5432`, a local folder file source
under `./inbound`, and an outbox webhook at `localhost:8080`.

### Picking a file backend

| `FILE_SOURCE` | When to use | Env vars |
|---|---|---|
| `filescom` *(default)* | Production / customer environments | `FILES_COM_API_KEY`, `FILES_COM_PICKUP_PATH`, `FILES_COM_ARCHIVE_PATH`, `FILES_COM_REJECT_PATH` (and optional `FILES_COM_BASE_URL` for a subdomain) |
| `local`               | Dev / smoke tests / disaster fallback (drop files into a folder) | `LOCAL_PICKUP_PATH`, `LOCAL_ARCHIVE_PATH`, `LOCAL_REJECT_PATH` |
| `sftp`                | Plain-SFTP customer; supports SSH key OR password auth | `SFTP_HOST`, `SFTP_USER`, plus EITHER `SFTP_PRIVATE_KEY_PATH` OR `SFTP_PASSWORD`. Optional `SFTP_KNOWN_HOSTS_PATH` for host-key pinning. |

ADR-0012 covers the rationale for files.com as the default. ADR-0013 covers
the SFTP sibling and its SSH-key support. See `application.yaml` for the full
set of optional knobs (timeouts, page size).

---

## Adding a new file variant (the no-code-change path)

1. Drop a new `*.yaml` into `src/main/resources/interfaces/`
2. Add a Flyway migration in `db/migration/` for the matching staging table (DBServices side)
3. Add fixture(s) + a new TC-3xx integration test against the catalog
4. Rebuild — JAR picks up the new variant automatically

Catalog YAML reference is in the docs site (`docs-site/content/overview/scope.mdx`) and the magazine-style
overview at [`../docs/file_manager_overview.html`](../docs/file_manager_overview.html).

---

## What I do NOT touch

- Anything inside `public.*` or `audit.*` schemas — that's DBServices
- Anything inside `staging.txn_inbox` or `staging.order_inbox` — those are populated by other teams
- The `mv_atp` materialized view, archive, purge — `pg_cron` runs those
- HTTP transports for transactions, orders, or read API — out of scope

If a stack trace points at any of those, the bug is in the contract, not in this code.
