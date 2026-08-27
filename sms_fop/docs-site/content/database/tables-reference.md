---
title: Table reference
description: Every table, every column — schema by schema.
---

Extracted from `deploy/11-06-v6-customer/customer_install.sql` + `alter_04_audit_event_log.sql`. Columns marked "v6" were added in the v6 tag.

## `processed` schema

### `processed.tenant`

Tenant registry. `tenant_id` defaults to `gen_random_uuid()::text`.

| Column | Type | Constraints |
|---|---|---|
| `tenant_id` | TEXT | PK, default `gen_random_uuid()::text` |
| `tenant_code` | TEXT | NOT NULL, UNIQUE |
| `name` | TEXT | NOT NULL |
| `is_active` | BOOLEAN | NOT NULL, default `true` |
| `created_at` | TIMESTAMPTZ | NOT NULL, default `now()` |
| `updated_at` | TIMESTAMPTZ | NOT NULL, default `now()` |

### `processed.product`

Per-tenant product catalog.

| Column | Type | Constraints |
|---|---|---|
| `product_id` | BIGINT | PK, IDENTITY |
| `tenant_id` | TEXT | NOT NULL, FK → `tenant(tenant_id)` |
| `product_code` | TEXT | NOT NULL |
| `name` | TEXT | NOT NULL |
| `is_active` | BOOLEAN | NOT NULL, default `true` |
| — | — | UNIQUE `(tenant_id, product_code)` |

### `processed.warehouse`

Same shape as `product` — `warehouse_id`, `warehouse_code`, `name`, `is_active`, UNIQUE `(tenant_id, warehouse_code)`.

### `processed.uom`

Same shape — `uom_id`, `uom_code`, `name`, `is_active`, UNIQUE `(tenant_id, uom_code)`.

### `processed.lot`

| Column | Type | Constraints |
|---|---|---|
| `lot_id` | BIGINT | PK, IDENTITY |
| `tenant_id` | TEXT | NOT NULL, FK → `tenant` |
| `product_id` | BIGINT | NOT NULL, FK → `product` |
| `lot_code` | TEXT | NOT NULL |
| — | — | UNIQUE `(tenant_id, product_id, lot_code)` |

### `processed.opening_balance`

File-driven stock snapshot per `(tenant, product, warehouse, subinventory, stock_status, lot, as_of_date)`.

| Column | Type | Notes |
|---|---|---|
| `opening_balance_id` | BIGINT | PK, IDENTITY |
| `tenant_id`, `product_id`, `warehouse_id`, `uom_id`, `lot_id` | BIGINT / TEXT | FKs to masters |
| `tenant_code`, `product_code`, `warehouse_code`, `uom_code`, `lot_code` | TEXT | Denormalized for reporting |
| `subinventory` | TEXT | NOT NULL, default `''` |
| `stock_status` | TEXT | NOT NULL, default `'LIBERATED'` |
| `qty` | NUMERIC(18,4) | NOT NULL |
| `as_of_date` | DATE | NOT NULL |
| `batch_id` | BIGINT | NOT NULL — links back to `staging.stocklevel_batch` |
| `source_file` | TEXT | Replay key — DELETE-by-source_file works for clean file replay |
| `created_at` | TIMESTAMPTZ | NOT NULL, default `now()` |

**Indexes:**

- `idx_opening_balance_key` UNIQUE `(tenant_id, product_id, warehouse_id, subinventory, stock_status, COALESCE(lot_id, 0), as_of_date)`
- `idx_opening_balance_tenant_product` `(tenant_id, product_id, warehouse_id)`
- `idx_opening_balance_source_file` `(source_file) WHERE source_file IS NOT NULL`

### `processed.stock_balance`

Running aggregate. Maintained by two triggers on `inv_transaction` + one on `sfdc_order_line`.

| Column | Type | Notes |
|---|---|---|
| `tenant_id` | TEXT | PK part, FK |
| `product_id` | BIGINT | PK part, FK |
| `warehouse_id` | BIGINT | PK part, FK |
| `subinventory` | TEXT | PK part, NOT NULL, default `''` |
| `stock_status` | TEXT | PK part, NOT NULL, default `'LIBERATED'` |
| `lot_id` | BIGINT | PK part, NOT NULL, default `0` |
| `on_hand_qty` | NUMERIC(18,4) | NOT NULL, default `0` |
| `reserved_qty` | NUMERIC(18,4) | NOT NULL, default `0` |
| `uom_id` | BIGINT | NOT NULL, FK |
| `last_updated_at` | TIMESTAMPTZ | NOT NULL, default `now()` |

**PK:** `(tenant_id, product_id, warehouse_id, subinventory, stock_status, lot_id)` — note `lot_id` is 0 (not NULL) so the PK is total.

### `processed.inv_transaction`

Immutable stock movement log.

| Column | Type | Notes |
|---|---|---|
| `inv_transaction_id` | BIGINT | PK, IDENTITY |
| `tenant_id` | TEXT | NOT NULL, FK |
| `external_txn_id` | TEXT | NOT NULL |
| `product_id`, `warehouse_id`, `uom_id` | BIGINT | NOT NULL, FKs |
| `subinventory`, `stock_status` | TEXT | |
| `lot_id` | BIGINT | FK (nullable) |
| `signed_qty` | NUMERIC(18,4) | NOT NULL — sign encodes direction |
| `txn_type` | TEXT | NOT NULL — receipt / issue / shipment / transfer_in / transfer_out / adjustment / sales_return / purchase_return / scrap |
| `posted_at` | TIMESTAMPTZ | NOT NULL, default `now()` |
| `transfer_pair_id` | TEXT | Links the two rows of a transfer |
| `sfdc_order_id`, `sfdc_line_id` | TEXT | SFDC-side audit links |
| `erp_line_id` **(v6)** | TEXT | Oracle `SOURCE_LINE_ID` — matches `sfdc_order_line.erp_external_id` |
| `erp_header_id` **(v6)** | TEXT | Oracle `TRANSACTION_SOURCE_ID` — matches `sfdc_order.erp_external_id` |
| `payload` | JSONB | Full inbound payload for audit |
| — | — | UNIQUE `(tenant_id, external_txn_id)` — idempotency guarantee |

**Indexes:**

- `idx_inv_transaction_tenant_pw_posted` `(tenant_id, product_id, warehouse_id, posted_at)`
- `idx_inv_transaction_order_link` `(sfdc_order_id) WHERE sfdc_order_id IS NOT NULL`
- `idx_inv_transaction_line_link` `(sfdc_line_id) WHERE sfdc_line_id IS NOT NULL`
- `idx_inv_transaction_erp_line_link` `(erp_line_id) WHERE erp_line_id IS NOT NULL` **(v6)**
- `idx_inv_transaction_erp_header_link` `(erp_header_id) WHERE erp_header_id IS NOT NULL` **(v6)**

### `processed.sfdc_order`

| Column | Type | Notes |
|---|---|---|
| `sfdc_order_id` | TEXT | PK — the SFDC `sfRecordId__c` (e.g. `0WO9Z000009f6lpWAA`) |
| `tenant_id` | TEXT | NOT NULL, FK |
| `customer_id` | TEXT | NOT NULL |
| `erp_external_id` **(v6)** | TEXT | FOP/Oracle SO header ID |
| `order_state` | TEXT | NOT NULL, default `'open'`, CHECK IN `(open, synced, closed, cancelled)` |
| `payload` | JSONB | NOT NULL |
| `received_at` | TIMESTAMPTZ | NOT NULL |
| `created_at` | TIMESTAMPTZ | NOT NULL, default `now()` — preserved across UPSERTs |
| `updated_at` | TIMESTAMPTZ | NOT NULL, default `now()` — refreshed each UPSERT |

**Index:** `idx_sfdc_order_erp_external_id (erp_external_id) WHERE erp_external_id IS NOT NULL` **(v6)**

### `processed.sfdc_order_line`

Composite PK. Cascade reservation lives here.

| Column | Type | Notes |
|---|---|---|
| `sfdc_order_id` | TEXT | PK part, FK → `sfdc_order`, ON DELETE CASCADE |
| `line_no` | INTEGER | PK part |
| `sfdc_line_id` | TEXT | SFDC's own line Id (audit) |
| `erp_external_id` **(v6)** | TEXT | FOP/Oracle SO line ID (`SOURCE_LINE_ID`) — the cascade match key |
| `tenant_id`, `product_id`, `warehouse_id`, `uom_id` | | Same as `opening_balance` |
| `subinventory` | TEXT | NOT NULL, default `''` |
| `stock_status` | TEXT | NOT NULL, default `'LIBERATED'` |
| `qty` | NUMERIC(18,4) | NOT NULL, CHECK `> 0` |
| `shipped_qty` **(v6)** | NUMERIC(18,4) | NOT NULL, default `0`. When `>= qty`, line auto-closes. |
| `line_state` | TEXT | NOT NULL, default `'open'`, CHECK IN `(open, synced, closed, cancelled)` |
| `fop_synced_at` | TIMESTAMPTZ | Stamped when the line closes |
| `payload` | JSONB | NOT NULL |

**Indexes:**

- `idx_sfdc_order_line_sfdc_line_id (sfdc_line_id) WHERE sfdc_line_id IS NOT NULL`
- `idx_sfdc_order_line_erp_external_id (erp_external_id) WHERE erp_external_id IS NOT NULL` **(v6)**
- `idx_sfdc_order_line_subinv_active (tenant_id, product_id, warehouse_id, subinventory, stock_status) WHERE line_state IN ('open', 'synced')`

### `processed.notification_outbox`

See [Observability](/architecture/observability#2-notification-outbox).

| Column | Type | Notes |
|---|---|---|
| `outbox_id` | BIGINT | PK, IDENTITY |
| `tenant_id` | TEXT | Nullable |
| `severity` | TEXT | NOT NULL, CHECK IN `(INFO, WARN, ERROR)` |
| `source` | TEXT | NOT NULL |
| `message` | TEXT | NOT NULL |
| `payload` | JSONB | NOT NULL, default `'{}'` |
| `dedup_key` | TEXT | Collapses repeats via `notify_outbox(...)` |
| `repeat_count` | INTEGER | NOT NULL, default `1` |
| `status` | TEXT | NOT NULL, default `'pending'`, CHECK IN `(pending, delivered, failed, failed_permanent)` |
| `retry_count` | INTEGER | NOT NULL, default `0` |
| `created_at`, `delivered_at`, `last_attempt_at` | TIMESTAMPTZ | |

**Index:** `idx_notification_outbox_pending (status, created_at) WHERE status IN ('pending', 'failed')`

## `staging` schema

### `staging.stocklevel_inbox`

Populated by the Java daemon. Wide table — one column per catalog field. See [Interface catalog](/reference/interface-catalog) for the field list. Not repeated here; the columns match the catalog YAML 1:1.

**PK:** `inbox_id` BIGSERIAL. **Indexes:** `ix_stocklevel_inbox_file (file_name)`, `ix_stocklevel_inbox_pending (file_name) WHERE promoted_at IS NULL`.

### `staging.stocklevel_batch`

One row per file processed by the daemon.

| Column | Type | Notes |
|---|---|---|
| `batch_id` | BIGSERIAL | PK |
| `file_name` | TEXT | NOT NULL, UNIQUE |
| `started_at`, `completed_at` | TIMESTAMPTZ | |
| `status` | TEXT | NOT NULL, default `'pending'`, CHECK IN `(pending, loaded, partial, failed)` |
| `rows_accepted` | INTEGER | NOT NULL, default `0` |
| `rows_rejected` | INTEGER | NOT NULL, default `0` |

### `staging.txn_inbox`

FOP writes here on every state change.

| Column | Type | Notes |
|---|---|---|
| `inbox_id` | BIGSERIAL | PK |
| `tenant_code` | TEXT | NOT NULL |
| `external_txn_id` | TEXT | NOT NULL |
| `payload` | JSONB | NOT NULL |
| `status` | TEXT | NOT NULL, default `'pending'`, CHECK IN `(pending, processed, rejected, superseded)` |
| `received_at` | TIMESTAMPTZ | NOT NULL, default `now()` |
| `processed_at` | TIMESTAMPTZ | Set by promote trigger |
| `reject_reason` | TEXT | Set by promote trigger on rejection |

**No UNIQUE on `(tenant_code, external_txn_id)`** — append-only.

**Indexes:** `idx_txn_inbox_pending_key (tenant_code, external_txn_id, received_at DESC) WHERE status = 'pending'`; `idx_txn_inbox_status_received (status, received_at)`.

### `staging.order_inbox`

SFDC writes here on every state change. Same shape as `txn_inbox` but the business key is `sfdc_order_id` instead of `external_txn_id`. Same append-only + auto-promote-trigger contract.

## `audit` schema

### `audit.event_log`

The one thing anyone writes to.

| Column | Type | Notes |
|---|---|---|
| `event_id` | BIGSERIAL | PK |
| `at` | TIMESTAMPTZ | NOT NULL, default `now()` |
| `event_type` | TEXT | NOT NULL — see [Event log schema](/reference/event-log-schema) |
| `severity` | TEXT | NOT NULL, default `'info'`, CHECK IN `(debug, info, warn, error)` |
| `source` | TEXT | NOT NULL — `files`, `fop`, `sfdc`, `atp`, `daemon` |
| `tenant_id` | TEXT | |
| `correlation_id` | TEXT | e.g. filename, order id, external_txn_id |
| `status` | TEXT | outcome |
| `ref_id` | TEXT | secondary identifier |
| `error_code`, `error_msg` | TEXT | |
| `latency_ms` | INTEGER | |
| `rows_affected` | INTEGER | |
| `bytes` | BIGINT | |
| `payload` | JSONB | NOT NULL, default `'{}'` |

**Indexes:**

- `ix_event_log_at (at DESC)`
- `ix_event_log_type_at (event_type, at DESC)`
- `ix_event_log_tenant_at (tenant_id, at DESC) WHERE tenant_id IS NOT NULL`
- `ix_event_log_corr (correlation_id) WHERE correlation_id IS NOT NULL`
- `ix_event_log_alerts (severity, at DESC) WHERE severity IN ('warn', 'error')`

Views over the existing tables are documented in [Views](/database/views).
