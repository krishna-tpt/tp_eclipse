---
title: Database overview
description: Three schemas — staging, processed, audit — and the trust boundary between them.
---

**PostgreSQL 16.** Azure Database for PostgreSQL Flexible Server in production; `postgres:16-alpine` in dev/CI. Three schemas, one trust boundary.

```
┌─────────────────────────────────────────────────────────────┐
│ staging.*                                                    │
│  ← writeable by anyone (append-only inboxes)                 │
│  ← per-row AFTER INSERT triggers promote to processed        │
│  stocklevel_inbox, stocklevel_batch, txn_inbox, order_inbox  │
└──────────────────────┬──────────────────────────────────────┘
                       │  promote functions
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ processed.*                                                  │
│  ← writeable ONLY via promote / drain functions              │
│  ← the live ledger                                           │
│  tenant, product, warehouse, uom, lot                        │
│  opening_balance, stock_balance, inv_transaction             │
│  sfdc_order, sfdc_order_line                                 │
│  notification_outbox                                         │
└──────────────────────┬──────────────────────────────────────┘
                       │  Java daemon writes events
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ audit.*                                                      │
│  ← append-only observability + union view                    │
│  event_log (table)                                           │
│  v_file_batch_events / v_order_events / v_txn_events /       │
│    v_stock_change_events / v_notify_events (views)           │
│  v_all_events (master union)                                 │
└─────────────────────────────────────────────────────────────┘
```

## Schema roles at a glance

| Schema | Purpose | Owner | Written by |
|---|---|---|---|
| `staging` | Landing zone for inbound files and messages | DBServices | Java daemon (stocklevel), FOP (txn), SFDC (orders) |
| `processed` | Live ledger + master data | DBServices | Only via SQL functions (`load_stocklevel`, `f_promote_txn`, `f_promote_order`, drain fallbacks) |
| `audit` | Observability event stream + views | DBServices | Java daemon + `fetch_inventory_json_observed` wrapper |

## Key design decisions

- **TEXT identifiers, not UUID** — every `*_id` column is TEXT. Auto-generated tenant_ids use `gen_random_uuid()::text`. Cross-system identifiers (SFDC `sfRecordId__c`, Oracle integers, files.com paths) all flow in as strings, giving a homogeneous schema. Applied uniformly across `processed` and `audit`.
- **Append-only staging** — no UNIQUE on `(tenant_code, external_txn_id)` in `staging.txn_inbox` or on `(tenant_code, sfdc_order_id)` in `staging.order_inbox`. FOP and SFDC re-post on every state change; the drain picks latest by `received_at DESC`.
- **No UPSERTs on inbox drain paths** — `load_orders` DELETEs then INSERTs the lines; `load_transactions` uses `WHERE NOT EXISTS`; `load_stocklevel` DELETEs the file's batch then INSERTs. Trigger functions retain `ON CONFLICT` for `stock_balance` maintenance (materialized aggregate, not the inbox→processed contract).
- **Trigger-first, drain-fallback** — v5 introduced AFTER INSERT triggers on `staging.txn_inbox` and `staging.order_inbox`. Every INSERT auto-promotes to `processed.*`. The `load_transactions` / `load_orders` functions are retained as fallback for catch-up after temporary trigger outages.
- **Line-precise cascade via `erp_external_id`** — v6 added `sfdc_order_line.erp_external_id` (FOP/Oracle SO line ID = `SOURCE_LINE_ID`) and `inv_transaction.erp_line_id`. Shipments cascade to the exact line, closing it when `shipped_qty >= qty`. See [Evolution](/database/evolution#v6).
- **Three-schema layout** — trust boundary between landing zones (`staging`) and live ledger (`processed`). Master data lives in `processed` because it's ledger-authoritative.

## Extensions

Only `pgcrypto` (bundled with PG13+ for `gen_random_uuid`).

Not required (and not requested from customer DBA):

- `pg_partman` — deferred; scale is 5k records/day, partitioning would be premature optimization
- `pg_cron` — deferred; customer runs their own scheduler (Airflow / K8s CronJob) for any DB-side maintenance
- `pgTAP` — dev/CI only; not on Azure allowlist

## Where to look next

- [ER diagram](/database/er-diagram) — the visual model of `processed`
- [Schemas](/database/schemas) — per-schema layout
- [Table reference](/database/tables-reference) — every table's columns, PKs, indexes
- [Functions](/database/functions) — promote, load, fetch, notify
- [Views](/database/views) — audit views for the Grafana endpoint
- [Evolution log](/database/evolution) — how we got here (v1 → v6)
- [`fetch_inventory_json` API](/reference/fetch-inventory-api) — the read contract for the SFDC read tier
