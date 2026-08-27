---
title: Audit review
description: SQL patterns for auditing what the system did, when, and for whom.
---

Everything of operational significance leaves a trace. This page catalogs the canonical audit queries — what to run daily, what to run weekly, what compliance or a stakeholder is likely to ask for.

The single source is `audit.v_all_events` (see [Views](/database/views)) — a UNION of the Java-written `audit.event_log` with derived views over inbox, outbox, and transaction tables.

## Daily audit checklist

Run these once a day. Copy the outputs into your ops report.

### 1. Ingest health

```sql
SELECT
    count(*) FILTER (WHERE event_type = 'file.parsed' AND severity != 'error') AS parsed_ok,
    count(*) FILTER (WHERE event_type = 'file.parsed' AND severity  = 'error') AS parsed_failed,
    count(*) FILTER (WHERE event_type = 'file.failed')                          AS pre_stage_failed,
    count(*) FILTER (WHERE event_type = 'file.skipped')                         AS duplicates_skipped
  FROM audit.v_all_events
 WHERE at >= current_date;
```

Healthy target: `parsed_ok >= 1`, `parsed_failed = 0`, `pre_stage_failed = 0`.

### 2. Promotion outcomes (FOP + SFDC)

```sql
SELECT source,
       count(*) FILTER (WHERE event_type = 'promotion.success') AS ok,
       count(*) FILTER (WHERE event_type = 'promotion.failed')  AS failed,
       count(*) FILTER (WHERE event_type IN ('txn.superseded','order.superseded')) AS superseded
  FROM audit.v_all_events
 WHERE at >= current_date
 GROUP BY 1;
```

`failed` should be < 1% of `ok`. Higher → dig into `error_msg`.

### 3. Notification delivery

```sql
SELECT status, count(*)
  FROM processed.notification_outbox
 WHERE created_at >= current_date
 GROUP BY status
 ORDER BY 1;
```

Healthy: mostly `delivered`, small transient `pending`, zero `failed_permanent` new today.

### 4. Heartbeat coverage

```sql
SELECT date_trunc('hour', at) AS hour, count(*) AS beats
  FROM audit.event_log
 WHERE event_type = 'daemon.heartbeat'
   AND at >= current_date
 GROUP BY 1 ORDER BY 1;
```

Every hour should have at least a few beats. Empty hours → daemon was down.

### 5. Ingest latency

```sql
SELECT
    percentile_cont(0.5)  WITHIN GROUP (ORDER BY latency_ms) AS p50,
    percentile_cont(0.95) WITHIN GROUP (ORDER BY latency_ms) AS p95,
    max(latency_ms) AS worst
  FROM audit.event_log
 WHERE event_type = 'file.parsed'
   AND at >= current_date;
```

Target: p95 < 60000 ms (60 s) for a normal-size file. Higher → look at file sizes or DB load.

## Weekly / monthly review

### Total data processed

```sql
SELECT
    count(*) FILTER (WHERE event_type = 'file.parsed')     AS files_processed,
    SUM(rows_affected) FILTER (WHERE event_type = 'file.parsed') AS total_stock_rows_loaded,
    count(*) FILTER (WHERE event_type = 'promotion.success' AND source = 'fop')  AS txns_promoted,
    count(*) FILTER (WHERE event_type = 'promotion.success' AND source = 'sfdc') AS orders_promoted,
    count(*) FILTER (WHERE event_type = 'atp.queried')     AS atp_queries
  FROM audit.v_all_events
 WHERE at >= date_trunc('week', current_date);
```

### Top rejection reasons

```sql
SELECT error_msg, count(*)
  FROM audit.v_all_events
 WHERE event_type IN ('promotion.failed','file.failed')
   AND at >= date_trunc('week', current_date)
 GROUP BY 1 ORDER BY 2 DESC LIMIT 10;
```

Recurring rejections point to master data drift (missing product / warehouse) or an integration bug on FOP / SFDC.

### ATP query volume by caller

```sql
SELECT ref_id AS caller, count(*) AS calls,
       percentile_cont(0.95) WITHIN GROUP (ORDER BY latency_ms) AS p95_ms
  FROM audit.event_log
 WHERE event_type = 'atp.queried'
   AND at >= date_trunc('week', current_date)
 GROUP BY 1 ORDER BY 2 DESC;
```

Unusual caller → security review. Unusual volume → scaling concern.

### Pod restart correlation

Restart events don't land in `audit.event_log` directly, but you can infer restarts from heartbeat gaps:

```sql
WITH beats AS (
  SELECT at, at - lag(at) OVER (ORDER BY at) AS gap
    FROM audit.event_log
   WHERE event_type = 'daemon.heartbeat'
     AND at >= date_trunc('week', current_date)
)
SELECT at AS resumed_at, gap
  FROM beats
 WHERE gap > interval '20 minutes'
 ORDER BY at DESC;
```

Each row = a probable restart / outage. Cross-reference against kubectl pod-restart logs.

## End-to-end tracing

### Trace a specific file

```sql
SELECT at, event_type, severity, source, latency_ms, error_msg, payload
  FROM audit.v_all_events
 WHERE correlation_id = 'MICH_INV_STOCKLEVEL_SMS_413.20260721.LSF.cfo'
 ORDER BY at;
```

Expected happy path:

```
1. file.picked         (files)         source: Java daemon saw it
2. file.parsed         (files, Java)   Java-written after successful load
3. file.parsed         (files, view)   derived from staging.stocklevel_batch
4. stock.changed × N   (files)         one per opening_balance row (cause=opening_balance)
5. notify.emitted      (if any WARN)   informational — e.g., "batch loaded with M rejects"
```

Missing step 2 → parse or promote failed. Missing step 4 → nothing was inserted (probably status='failed'). Look at step 5 for the reason.

### Trace a specific SFDC order

```sql
SELECT at, event_type, severity, error_msg, payload
  FROM audit.v_all_events
 WHERE correlation_id = '0WO9Z000009f6lpWAA'
 ORDER BY at;
```

Expected:

```
1. order.received      (sfdc)   inbox INSERT
2. promotion.success   (sfdc)   trigger fired
3. stock.changed × N   (sfdc)   one per line (cause=order_line)
```

If step 2 is `promotion.failed`, `error_msg` shows why. If step 3 is missing after success, either the order had no lines or the cascade didn't find matches — check `sfdc_order_line` directly.

### Trace a specific transaction

```sql
SELECT at, event_type, severity, error_msg, payload
  FROM audit.v_all_events
 WHERE correlation_id = 'FOP-TX-98765'
 ORDER BY at;
```

Expected:

```
1. txn.received        (fop)    inbox INSERT
2. promotion.success   (fop)    trigger fired
3. stock.changed       (fop)    inv_transaction INSERT applied (cause=transaction)
```

Missing step 3 → trigger fired but the stock apply cascade failed (rare). Look at pod logs or `RAISE WARNING` output in postgres logs.

### Trace stock movements for a product-warehouse

```sql
SELECT at, source, payload->>'cause' AS cause,
       payload->>'signed_qty' AS qty, payload->>'txn_type' AS type,
       correlation_id
  FROM audit.v_stock_change_events
 WHERE payload->>'product_code' = 'PROD-A'
   AND payload->>'warehouse_code' = 'WH-01'
   AND at >= date_trunc('day', current_date)
 ORDER BY at;
```

Every on-hand change: opening balance loads, transactions, order-line reservations. Reconstruct the day's stock story for one product.

## Compliance reports

### All actions for a specific tenant, this week

```sql
SELECT date_trunc('day', at) AS day,
       count(*) AS total,
       count(*) FILTER (WHERE event_type = 'file.parsed')       AS files,
       count(*) FILTER (WHERE event_type LIKE 'promotion.%')     AS promotions,
       count(*) FILTER (WHERE event_type = 'atp.queried')       AS atp_queries,
       count(*) FILTER (WHERE event_type LIKE '%.failed' OR severity IN ('warn','error')) AS problems
  FROM audit.v_all_events
 WHERE tenant_id = 'MNA'
   AND at >= date_trunc('week', current_date)
 GROUP BY 1 ORDER BY 1;
```

### Every access to fetch_inventory_json for a tenant, month-to-date

```sql
SELECT date_trunc('day', at) AS day,
       ref_id AS caller,
       count(*) AS calls,
       SUM(rows_affected) AS total_rows_returned
  FROM audit.event_log
 WHERE event_type = 'atp.queried'
   AND tenant_id = 'MNA'
   AND at >= date_trunc('month', current_date)
 GROUP BY 1, 2
 ORDER BY 1, 2;
```

### Stock movements for a product between dates (exportable)

```sql
COPY (
  SELECT at, payload->>'cause' AS source,
         payload->>'warehouse_code' AS warehouse,
         payload->>'subinventory' AS subinventory,
         payload->>'stock_status' AS stock_status,
         payload->>'signed_qty' AS delta_qty,
         payload->>'qty' AS absolute_qty,
         payload->>'txn_type' AS txn_type,
         correlation_id
    FROM audit.v_stock_change_events
   WHERE payload->>'product_code' = 'PROD-A'
     AND at BETWEEN '2026-07-01' AND '2026-07-31'
   ORDER BY at
) TO STDOUT WITH CSV HEADER;
```

Redirect to a file: `psql ... > movements-PROD-A-2026-07.csv`.

### All promotion rejections in a period, grouped by reason

```sql
SELECT date_trunc('day', at) AS day,
       source, error_msg, count(*)
  FROM audit.v_all_events
 WHERE event_type = 'promotion.failed'
   AND at BETWEEN '2026-07-01' AND '2026-07-31'
 GROUP BY 1, 2, 3
 ORDER BY 1, 4 DESC;
```

## Consistency verification

Run these to sanity-check that `stock_balance` actually reflects `opening_balance + inv_transaction`.

### For one product-warehouse

```sql
WITH lo AS (
  SELECT DISTINCT ON (product_id, warehouse_id, subinventory, stock_status, COALESCE(lot_id, 0))
         product_id, warehouse_id, subinventory, stock_status,
         COALESCE(lot_id, 0) AS lot_key, qty AS opening_qty, as_of_date
    FROM processed.opening_balance
   WHERE tenant_code = 'MNA' AND product_code = 'PROD-A' AND warehouse_code = 'WH-01'
   ORDER BY product_id, warehouse_id, subinventory, stock_status, COALESCE(lot_id, 0), as_of_date DESC
),
tx AS (
  SELECT product_id, warehouse_id,
         COALESCE(subinventory, '')          AS subinventory,
         COALESCE(stock_status, 'LIBERATED') AS stock_status,
         COALESCE(lot_id, 0)                 AS lot_key,
         SUM(signed_qty) AS sum_signed
    FROM processed.inv_transaction it
   WHERE tenant_code = 'MNA' AND product_code = 'PROD-A' AND warehouse_code = 'WH-01'
     AND posted_at >= (SELECT max(as_of_date) FROM lo)
   GROUP BY product_id, warehouse_id, COALESCE(subinventory,''), COALESCE(stock_status,'LIBERATED'), COALESCE(lot_id,0)
)
SELECT lo.subinventory, lo.stock_status, lo.lot_key,
       lo.opening_qty + COALESCE(tx.sum_signed, 0) AS expected_on_hand,
       sb.on_hand_qty                              AS actual_on_hand,
       (lo.opening_qty + COALESCE(tx.sum_signed, 0)) - sb.on_hand_qty AS drift
  FROM lo
  LEFT JOIN tx USING (product_id, warehouse_id, subinventory, stock_status, lot_key)
  JOIN processed.stock_balance sb
    ON sb.product_id = lo.product_id
   AND sb.warehouse_id = lo.warehouse_id
   AND sb.subinventory = lo.subinventory
   AND sb.stock_status = lo.stock_status
   AND sb.lot_id = lo.lot_key;
```

`drift` should be 0 everywhere. Non-zero → run `recalculate_stock_balance('MNA')`.

### Fleet-wide drift check

```sql
WITH lo AS (
  SELECT DISTINCT ON (tenant_id, product_id, warehouse_id, subinventory, stock_status, COALESCE(lot_id, 0))
         tenant_id, product_id, warehouse_id, subinventory, stock_status,
         COALESCE(lot_id, 0) AS lot_key, qty, as_of_date
    FROM processed.opening_balance
   ORDER BY tenant_id, product_id, warehouse_id, subinventory, stock_status, COALESCE(lot_id, 0), as_of_date DESC
),
tx AS (
  SELECT tenant_id, product_id, warehouse_id,
         COALESCE(subinventory, '')          AS subinventory,
         COALESCE(stock_status, 'LIBERATED') AS stock_status,
         COALESCE(lot_id, 0)                 AS lot_key,
         SUM(signed_qty) AS sum_signed
    FROM processed.inv_transaction tx
   GROUP BY tenant_id, product_id, warehouse_id, COALESCE(subinventory,''), COALESCE(stock_status,'LIBERATED'), COALESCE(lot_id,0)
),
expected AS (
  SELECT lo.tenant_id, lo.product_id, lo.warehouse_id, lo.subinventory, lo.stock_status, lo.lot_key,
         lo.qty + COALESCE(tx.sum_signed, 0) AS expected_on_hand
    FROM lo LEFT JOIN tx USING (tenant_id, product_id, warehouse_id, subinventory, stock_status, lot_key)
)
SELECT count(*) FILTER (WHERE expected.expected_on_hand <> sb.on_hand_qty) AS drift_rows,
       count(*) AS total_rows
  FROM expected
  JOIN processed.stock_balance sb USING (tenant_id, product_id, warehouse_id, subinventory, stock_status)
 WHERE sb.lot_id = expected.lot_key;
```

`drift_rows` should be 0. If non-zero, run `recalculate_stock_balance` for the affected tenant(s).

## Retention

`audit.event_log` grows unbounded by default. See [FAQ → audit.event_log is growing large](/support/faq#q-auditevent_log-is-growing-large) for retention SQL.

For long-term compliance archives, export monthly:

```sql
COPY (
  SELECT * FROM audit.event_log
   WHERE at >= date_trunc('month', current_date - interval '1 month')
     AND at <  date_trunc('month', current_date)
) TO '/mnt/backup/audit_event_log_2026_06.csv' WITH CSV HEADER;
```

Then delete the archived rows if storage is a concern. Coordinate with the DBA on the retention window.
