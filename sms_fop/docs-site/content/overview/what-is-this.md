---
title: What is this?
description: Plain-English intro to psql-inventory-integration-service.
---

**Michelin runs a global stock-level extract every day.** Their Oracle EBS system generates a semicolon-delimited file — `MICH_INV_STOCKLEVEL_*.cfo` — for every business unit and drops it onto files.com. This service picks it up, parses it, and loads it into a PostgreSQL "ledger" that other Michelin systems (FOP transactions, Salesforce order management) can query for accurate on-hand and available-to-promise (ATP) numbers.

## What the service does end to end

1. **Wakes on schedule** (daily 06:00 + hourly catch-up) or runs once (`oneshot` mode for dev/CI).
2. **Lists** the pickup folder on files.com (or SFTP as a fallback).
3. **Filters** by filename pattern — accepts `MICH_INV_STOCKLEVEL_SMS_413…` and its siblings.
4. **Downloads** each file and computes SHA-256.
5. **Skips** files already ingested (hash-dedup against `staging.stocklevel_batch`).
6. **Decodes** the file against the [interface catalog](/reference/interface-catalog) — variant detection, positional parse, envelope capture, field mapping with type coercion.
7. **Loads** all data rows into `staging.stocklevel_inbox` in one atomic transaction.
8. **Promotes** into `processed.opening_balance` via `staging.load_stocklevel(file_name)`.
9. **Moves** the file to `archive/YYYY-MM/` on success, or `reject/` on failure.
10. **Emits** a `file.parsed` event to `audit.event_log` and drains the notification outbox via webhook.

## What other teams do

The Java service is only one of three pipelines. The database schema is the shared contract:

- **FOP (inventory transactions)** — the Oracle-side team INSERTs into `staging.txn_inbox`; an auto-promote trigger writes to `processed.inv_transaction` and updates `stock_balance`.
- **SFDC (Salesforce orders)** — the Salesforce integration team INSERTs into `staging.order_inbox`; a trigger promotes to `processed.sfdc_order` + `sfdc_order_line` and cascades reservations.
- **SFDC read tier** — calls `processed.fetch_inventory_json(...)` to display on-hand and ATP on quote and order screens.

We own the Java loader **and** the SQL schema + functions. We do NOT own the FOP or SFDC ingest paths, and we do NOT expose an HTTP API — reads happen at the Postgres level.

## What you get in this documentation

- **[Spec](/overview/spec)** — every requirement (F01..F17) and NFR, with acceptance criteria
- **[Architecture](/architecture/overview)** — process topology, Java package map, DB flow
- **[Database](/database/overview)** — schemas, tables, functions, ER diagram, evolution
- **[Operations](/operations/deployment)** — build, deploy, configure, run
- **[Quality](/quality/test-plan)** — the test plan and its latest execution results
- **[Delivery](/delivery/changelog)** — commit-by-commit history and current status
- **[Reference](/reference/interface-catalog)** — file formats, API contracts, env vars
