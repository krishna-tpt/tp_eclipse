---
title: Schemas
description: Layout, ownership, and search_path for staging, processed, and audit.
---

Three schemas, applied in one script. All owned by the DBServices project. See [Table reference](/database/tables-reference) for individual columns.

```sql
CREATE SCHEMA IF NOT EXISTS processed;
CREATE SCHEMA IF NOT EXISTS staging;
CREATE SCHEMA IF NOT EXISTS audit;
```

The default `search_path` for the customer install is `processed, staging, audit, public, pg_catalog` — operators can write `SELECT * FROM stock_balance` without qualifying. Functions have their own `SET search_path` per `SECURITY DEFINER` best practice.

## `staging` — landing zones

Where inbound data first appears. Writeable by the corresponding integrating team's DB role. Rows survive with a `status` marker for observability but are considered ephemeral (superseded rows can be purged).

| Table | Populated by | Trigger on INSERT |
|---|---|---|
| `staging.stocklevel_inbox` | Java daemon (per file) | (none — file-batch semantics) |
| `staging.stocklevel_batch` | Java daemon (per file header) | (none) |
| `staging.txn_inbox` | FOP integration | `trg_txn_inbox_promote → f_promote_txn` |
| `staging.order_inbox` | SFDC integration | `trg_order_inbox_promote → f_promote_order` |

### Append-only contract

The two "message" inboxes (`txn_inbox`, `order_inbox`) have **no UNIQUE** on business keys. FOP and SFDC re-post the same key on every state change:

- Drain functions pick the latest by `received_at DESC` (via `DISTINCT ON`).
- Older rows of the same key transition to `status='superseded'`, keeping history without ambiguity about which was promoted.

### Batch semantics

`stocklevel_inbox` is different — it's populated one file at a time. The Java daemon INSERTs the whole file in one transaction, then calls `staging.load_stocklevel(file_name)` which DELETEs any prior rows for the file's batch and INSERTs fresh into `processed.opening_balance`. On any failure, the whole thing rolls back.

## `processed` — live ledger

The authoritative ledger. Read by everyone; written **only** through promote / drain functions or their internal triggers. Direct INSERTs are refused via role privileges (see [Coding guidelines](/quality/coding-guidelines#security-postures)).

### Master data (owner: application team)

| Table | Purpose |
|---|---|
| `processed.tenant` | Tenant registry. `tenant_code` is the natural key used in file headers (e.g. `MNA`, `IFOPEUR`). |
| `processed.product` | Product catalog per tenant. `product_code` matches file field `item_segment1`. |
| `processed.warehouse` | Warehouse catalog per tenant. |
| `processed.uom` | Unit of measure per tenant. |
| `processed.lot` | Lot codes per product (nullable on transactions if the product is not lot-tracked). |

### Live ledger

| Table | Purpose | Written by |
|---|---|---|
| `processed.opening_balance` | File-driven stock snapshot. One row per `(tenant, product, warehouse, subinv, stock_status, lot, as_of_date)`. | `staging.load_stocklevel` |
| `processed.stock_balance` | Running aggregate. PK: `(tenant, product, warehouse, subinv, stock_status, lot)`. Two triggers maintain `on_hand_qty` and `reserved_qty`. | `f_stock_balance_*` triggers |
| `processed.inv_transaction` | Immutable transaction log. Every stock movement (receipt, issue, shipment, transfer, adjustment, scrap, return). | `staging.f_promote_txn` trigger |
| `processed.sfdc_order` | SFDC order header. UPSERT on `sfdc_order_id` preserves `created_at`. | `staging.f_promote_order` trigger |
| `processed.sfdc_order_line` | SFDC order lines. DELETE-then-INSERT per order (cascades to `stock_balance.reserved_qty` via `f_stock_balance_reservation_apply`). | `staging.f_promote_order` trigger |

### Operational

| Table | Purpose |
|---|---|
| `processed.notification_outbox` | Failure/notification channel. SQL side calls `notify_outbox(...)` to insert; Java side drains via webhook. |

## `audit` — observability

Append-only. See [Observability](/architecture/observability) and [Event log schema](/reference/event-log-schema).

| Object | Type | Purpose |
|---|---|---|
| `audit.event_log` | table | The one thing anyone writes to. `event_id BIGSERIAL PRIMARY KEY, at TIMESTAMPTZ, event_type TEXT, severity TEXT CHECK (info/warn/error/debug), source, tenant_id, correlation_id, status, ref_id, error_code, error_msg, latency_ms, rows_affected, bytes, payload JSONB` |
| `audit.v_file_batch_events` | view | `staging.stocklevel_batch → file.parsed` |
| `audit.v_order_events` | view | `staging.order_inbox → order.received + promotion.*` |
| `audit.v_txn_events` | view | `staging.txn_inbox → txn.received + promotion.*` |
| `audit.v_stock_change_events` | view | `inv_transaction ∪ opening_balance ∪ sfdc_order_line → stock.changed` |
| `audit.v_notify_events` | view | `notification_outbox → notify.emitted` |
| `audit.v_all_events` | view | UNION ALL of all of the above — the Grafana endpoint |

## Ownership summary

| Concern | Java | DBServices | Ops |
|---|---|---|---|
| File pickup | ✓ | | |
| Parse + INSERT into `staging.stocklevel_inbox` | ✓ | | |
| Call `staging.load_stocklevel` | ✓ | | |
| `f_promote_txn` / `f_promote_order` triggers | | ✓ | |
| `stock_balance` maintenance triggers | | ✓ | |
| Cascade reservation | | ✓ | |
| Emit `notification_outbox` rows | ✓ (from Java) + ✓ (from SQL via `notify_outbox`) | | |
| Drain `notification_outbox` → webhook | ✓ | | |
| Write to `audit.event_log` | ✓ (file events, heartbeat, atp.queried) | | |
| Schedule the Java daemon | | | ✓ (customer K8s / Airflow) |
| Apply DDL from `customer_install.sql` + `alter_*.sql` | | | ✓ (customer DBA) |
