---
title: ADR-0006 - Azure Flexible Postgres (prod), local Postgres (dev/test)
description: Architecture Decision Record 0006 - Azure Flexible Postgres (prod), local Postgres (dev/test)
---


**Status:** Accepted · **Date:** 2026-05-18

## Context

TenthPlanet's target deployment is Azure. Two managed-Postgres flavors are available: Single Server (legacy) and Flexible Server (current). Flexible Server is the only one Microsoft actively develops; Single Server is being retired.

The design uses several extensions: `pg_partman` (partition management), `pg_cron` (scheduled jobs), `pgcrypto` (`gen_random_uuid`), and `pgTAP` (SQL tests). Azure Flexible Postgres has an `azure.extensions` server parameter that allowlists which extensions can be installed.

## Decision

- **Production:** Azure Database for PostgreSQL Flexible Server 16, single zone HA, geo-redundant backup.
- **Dev/test:** local Postgres 16 (Docker or developer-installed) with the same extension set.
- `azure.extensions` must include `PG_CRON`, `PG_PARTMAN`, `PGCRYPTO`. These are all on the standard allowlist as of Postgres 16 on Azure.
- **`pgTAP` is dev/CI only** — it is *not* on the Azure allowlist. The pgTAP test suite (`db/test/*.sql`) runs against Testcontainers and local Postgres, never against production.
- No superuser on Azure — the service connects as a least-privilege role (`inventoryledger_app`) with `EXECUTE` on specific functions only.

## Consequences

**Positive**
- Managed HA, automated backups, PITR — RPO ~15 min, RTO ~4 h out of the box.
- pg_cron and pg_partman are both supported, so scheduled work and partition management don't require external tooling.
- Single-zone HA + Private Endpoint covers the deck's DR target without extra config.

**Negative**
- We are locked to Postgres versions Azure offers — currently 16. Upgrades follow Microsoft's cadence.
- Extension changes require server-parameter edits, not just `CREATE EXTENSION` — adds an ops step for any new extension.
- pgTAP not available in prod means SQL contract is enforced only at test time. Acceptable since pgTAP is a *test* tool, not a runtime concern.
- Connection limits depend on tier; we deliberately use one connection per run to stay well under any limit.

## Alternatives considered

- **Self-managed Postgres on AKS** — rejected; loses managed backups and HA, increases ops burden.
- **Azure Cosmos DB for PostgreSQL** — rejected; distributed semantics not needed at 5k/day, and pg_partman/pg_cron behavior differs.
- **AWS RDS / Aurora** — rejected as outside the chosen cloud.
