---
title: Functions
description: Promote, load, fetch, notify — the SQL surface DBServices exposes.
---

Every function is `SECURITY DEFINER` with an explicit `SET search_path = processed, staging, audit, public, pg_catalog`. Explicit `REVOKE ... FROM PUBLIC` + `GRANT EXECUTE TO` the specific role that needs each function. No public execute anywhere.

## Promotion (staging → processed)

These fire automatically as AFTER INSERT triggers on the corresponding inbox. Each catches its own exceptions — the original INSERT never fails because of downstream issues; failures land in `staging.*.status='rejected'` with `reject_reason`.

### `staging.f_promote_txn()` trigger function

Fires on `staging.txn_inbox INSERT`. Parses `NEW.payload`, looks up masters (`product`, `warehouse`, `uom`, `lot`), inserts to `processed.inv_transaction`. On success sets `NEW.status='processed'`; on failure sets `'rejected'` + `reject_reason` and calls `notify_outbox('warn', 'promote_txn', ...)`.

Match precedence for reservation cascade: `erp_line_id > sfdc_line_id > sfdc_order_id`.

### `staging.f_promote_order()` trigger function

Fires on `staging.order_inbox INSERT`. UPSERTs into `processed.sfdc_order` (preserves `created_at`); DELETEs the order's existing lines; INSERTs the new lines. Each new line's INSERT cascades to `f_stock_balance_reservation_apply` via `AFTER INSERT` on `sfdc_order_line`.

### `staging.promote_one_txn(inbox_id)` / `staging.promote_one_order(inbox_id)` (v6)

Callable helpers with the same logic as the triggers. Used by `load_transactions` / `load_orders` (see below) so the trigger and drain paths share code.

## File-batch load

### `staging.load_stocklevel(p_file_name TEXT)`

Called by the Java daemon after INSERTing the whole file's rows into `staging.stocklevel_inbox`.

```
1. DELETE from processed.opening_balance WHERE source_file = p_file_name
   (idempotent for file replay)
2. Resolve masters for each staging row (product, warehouse, uom, lot)
3. Filter out rows with unresolved masters → move to staging.*.status='rejected'
   with reject_reason='unknown_product' | 'unknown_warehouse' | ...
4. INSERT accepted rows into processed.opening_balance
   (trigger f_stock_balance_opening_apply updates stock_balance.on_hand_qty)
5. UPDATE staging.stocklevel_batch: status='loaded', rows_accepted, rows_rejected,
   completed_at=now()
6. If rejected > 0 → notify_outbox('warn', 'load_stocklevel', 'batch N loaded with M rejects')
```

Returns `(accepted INTEGER, rejected INTEGER)`.

## Drain fallbacks

Retained for catch-up after a temporary trigger outage or manual reprocessing.

### `staging.load_transactions()`

Drains `staging.txn_inbox WHERE status='pending'`. Picks latest per `(tenant_code, external_txn_id)` by `received_at DESC`, marks older rows `superseded`, calls `staging.promote_one_txn(inbox_id)` on the winner.

### `staging.load_orders()`

Same shape for `staging.order_inbox`.

## Trigger workers (in `processed` schema)

### `processed.f_stock_balance_opening_apply()`

Trigger on `opening_balance AFTER INSERT`. Sets `stock_balance.on_hand_qty` for the row's `(tenant, product, warehouse, subinv, stock_status, lot)` (INSERTs new row or UPDATEs existing).

### `processed.f_stock_balance_txn_apply()`

Trigger on `inv_transaction AFTER INSERT`. `stock_balance.on_hand_qty += NEW.signed_qty`. Creates the target row if absent.

### `processed.f_stock_balance_reservation_apply()`

Trigger on `sfdc_order_line AFTER INSERT/UPDATE/DELETE` AND on `inv_transaction AFTER INSERT` (when the txn matches an active line).

For the line trigger path: computes `target = (qty - shipped_qty)` when active, `0` when not; delta = `NEW_target − OLD_target`; applies delta to `stock_balance.reserved_qty`.

For the txn trigger path: finds matching line by precedence (`erp_line_id > sfdc_line_id > sfdc_order_id`); updates `shipped_qty += |signed_qty|`; if `shipped_qty >= qty` sets `line_state='closed'` + `fop_synced_at=now()`; re-applies the reservation formula.

The unified formula handles INSERT (new line reserves qty), UPDATE (delta reservation), close (reserved → 0), cancel (release), subinv-move (release OLD, reserve NEW).

## Read API

### `processed.fetch_inventory_json(p_tenant_code, p_warehouse_code DEFAULT NULL, p_subinventory DEFAULT NULL, p_product_code DEFAULT NULL)`

`RETURNS SETOF JSON`. See [`fetch_inventory_json` API](/reference/fetch-inventory-api) for the full contract, JSON shape, and examples.

Aggregates `processed.stock_balance` per `(tenant, product, warehouse, subinventory, stock_status)`. All three tail arguments are optional filters. Result is a JSON object per group with `tenant_code`, `product_code`, `warehouse_code`, `subinventory`, `stock_status`, `on_hand`, `reserved`, `available` (= `on_hand - reserved`).

### `processed.fetch_inventory_json_observed(p_tenant_code, p_warehouse_code, p_subinventory, p_product_code, p_caller)` (alter_04)

Same as `fetch_inventory_json` but wraps every call in an `audit.event_log` insert of type `atp.queried` with the caller's id, `latency_ms`, and `rows_affected`. An audit-write failure NEVER blocks the read response (the write is wrapped in `BEGIN ... EXCEPTION WHEN OTHERS THEN RAISE WARNING`). New callers should use this variant.

## Notifications

### `processed.notify_outbox(p_tenant_id, p_severity, p_source, p_message, p_dedup_key DEFAULT NULL, p_payload DEFAULT '{}')`

Insert into `processed.notification_outbox`, or bump `repeat_count` if a row with the same `(tenant_id, source, dedup_key)` already exists within the last `outbox_dedup_window_minutes` (default 60).

Called by every SQL exception path that wants operator attention. Java-side code writes to the outbox directly (no dedup — Java events are already deduped at source).

## Config helpers

### `processed.pipeline_config_int(p_key TEXT) RETURNS INTEGER`

Read a config value from `processed.pipeline_config` and parse as INT. Returns NULL if key missing. Used by `notify_outbox` for `outbox_dedup_window_minutes` and similar knobs — supports the "no hard-coding" rule at the SQL layer.

## Recovery

### `processed.recalculate_stock_balance()`

Rebuilds `stock_balance` from `opening_balance` (base) + `inv_transaction` (deltas). Used after back-dated `opening_balance` inserts or trigger outages.

Idempotent: TRUNCATE `stock_balance` then INSERT SELECT from a full recomputation. Safe to run any time — but blocks any concurrent write to `stock_balance` for the duration.

## Function-role privileges

Set up in `customer_install.sql`. Typical grants:

| Function | GRANTed to |
|---|---|
| `staging.load_stocklevel` | `inventoryledger_app` (Java daemon role) |
| `staging.load_transactions`, `staging.load_orders` | `inventoryledger_app`, `inventoryledger_ops` |
| `staging.promote_one_txn`, `staging.promote_one_order` | (execute only via triggers / other SECURITY DEFINER fns) |
| `processed.fetch_inventory_json`, `processed.fetch_inventory_json_observed` | `inventoryledger_reader` (SFDC read role) |
| `processed.notify_outbox` | (execute only via other SECURITY DEFINER fns) |
| `processed.recalculate_stock_balance` | `inventoryledger_ops` (manual recovery role) |

All other DB roles have `REVOKE EXECUTE ... FROM PUBLIC` per function.
