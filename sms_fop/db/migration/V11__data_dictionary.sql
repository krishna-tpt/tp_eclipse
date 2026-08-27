-- V11__data_dictionary.sql
--
-- Documentation-only. Adds COMMENT ON for every schema, table, column, and
-- key function. No DDL behaviour changes. These comments surface in SchemaSpy
-- / DBeaver / psql \d+ output so the team has a single source of truth.

-- ============================================================================
-- Schemas
-- ============================================================================
COMMENT ON SCHEMA public  IS
    'Live business tables and the read/write functions over them. External readers and writers do NOT have direct DML on tables in this schema — they go through staging.* (write) and fetch_inventory (read).';
COMMENT ON SCHEMA staging IS
    'Inbox + landing area. External writers INSERT here (txn_inbox, order_inbox, ob_load*). Drain functions (process_txn_inbox, process_order_inbox, load_opening_balance) move rows from staging to public.';
COMMENT ON SCHEMA audit   IS
    'Trigger-driven audit log. Every INSERT/UPDATE/DELETE on selected live tables produces one audit_log row. Read-only for the application; managed by audit.f_audit_capture trigger.';
COMMENT ON SCHEMA archive IS
    'pg_partman parking lot. Old inv_transaction / audit_log partitions are detached and moved here on the retention schedule, kept until the operator drops them.';

-- ============================================================================
-- Master data (public)
-- ============================================================================
COMMENT ON TABLE tenant IS
    'Top-level tenant boundary. Every business row carries tenant_id; RLS uses the app.tenant_id GUC to enforce isolation.';
COMMENT ON COLUMN tenant.tenant_id   IS 'PK. UUID generated server-side. Referenced by every business table.';
COMMENT ON COLUMN tenant.tenant_code IS 'Stable human-readable code (e.g. "ACME"). UNIQUE across all tenants. Used in inbound payloads instead of UUID to keep external integrations human-friendly.';
COMMENT ON COLUMN tenant.name        IS 'Display name. Free text; not used for matching.';
COMMENT ON COLUMN tenant.is_active   IS 'Soft-delete flag. Inactive tenants reject inbound writes.';
COMMENT ON COLUMN tenant.created_at  IS 'Row insert timestamp.';
COMMENT ON COLUMN tenant.updated_at  IS 'Last update timestamp; maintained by application code.';

COMMENT ON TABLE product IS
    'Per-tenant product master. (tenant_id, product_code) is the natural key external systems use.';
COMMENT ON COLUMN product.product_id   IS 'Surrogate PK. Used by all transactional tables.';
COMMENT ON COLUMN product.tenant_id    IS 'Owning tenant. Products are tenant-scoped; the same product_code in two tenants is two separate rows.';
COMMENT ON COLUMN product.product_code IS 'Tenant-local code (e.g. SKU). Unique within tenant.';
COMMENT ON COLUMN product.name         IS 'Display name.';
COMMENT ON COLUMN product.is_active    IS 'Soft-delete flag. Inactive products reject new transactions.';

COMMENT ON TABLE warehouse IS
    'Per-tenant warehouse / location master. (tenant_id, warehouse_code) is the natural key.';
COMMENT ON COLUMN warehouse.warehouse_id   IS 'Surrogate PK.';
COMMENT ON COLUMN warehouse.tenant_id      IS 'Owning tenant.';
COMMENT ON COLUMN warehouse.warehouse_code IS 'Tenant-local code (e.g. "WH-NL-01"). Unique within tenant.';
COMMENT ON COLUMN warehouse.name           IS 'Display name.';

COMMENT ON TABLE uom IS
    'Per-tenant unit-of-measure master (EA, KG, L, …). Each tenant defines its own set.';
COMMENT ON COLUMN uom.uom_id   IS 'Surrogate PK.';
COMMENT ON COLUMN uom.uom_code IS 'Tenant-local code. Unique within tenant.';

COMMENT ON TABLE lot IS
    'Optional lot / batch identifier for products that track lots. NULL lot_id in transactions means "no lot tracking for this row".';
COMMENT ON COLUMN lot.lot_id     IS 'Surrogate PK.';
COMMENT ON COLUMN lot.product_id IS 'Lots are scoped under a product.';
COMMENT ON COLUMN lot.lot_code   IS 'Tenant-local lot identifier; unique per (tenant, product).';

-- ============================================================================
-- Opening balance (public)
-- ============================================================================
COMMENT ON TABLE opening_balance IS
    'Per-tenant starting stock position as of as_of_date. Loaded by Pipeline 1 (CSV from finance system). ATP = opening_balance + Σ inv_transaction.signed_qty − reserved orders.';
COMMENT ON COLUMN opening_balance.opening_balance_id IS 'Surrogate PK.';
COMMENT ON COLUMN opening_balance.tenant_id          IS 'Tenant scope.';
COMMENT ON COLUMN opening_balance.product_id         IS 'Resolved from CSV product_code at load time.';
COMMENT ON COLUMN opening_balance.warehouse_id       IS 'Resolved from CSV warehouse_code at load time.';
COMMENT ON COLUMN opening_balance.lot_id             IS 'Resolved from CSV lot_code at load time; NULL when not lot-tracked.';
COMMENT ON COLUMN opening_balance.qty                IS 'Quantity at as_of_date. Always positive (opening positions are inbound by convention).';
COMMENT ON COLUMN opening_balance.uom_id             IS 'Unit of measure for qty.';
COMMENT ON COLUMN opening_balance.as_of_date         IS 'Business date the opening position represents. Daily files use today; historic loads can use older dates.';
COMMENT ON COLUMN opening_balance.batch_id           IS 'staging.ob_load_batch reference. Links the row back to the source file for traceability.';

-- ============================================================================
-- Inventory transaction (public, partitioned ANNUALLY by posted_at)
-- ============================================================================
COMMENT ON TABLE inv_transaction IS
    'Immutable inventory movement ledger. Partitioned by posted_at, yearly (pg_partman managed; see pipeline_config.inv_txn_retention). Pipeline 2 (FOP) writes via staging.txn_inbox → process_txn_inbox → post_transaction. Receipts and inbound transfers are positive; issues, scrap, outbound transfers are negative. Transfers post as a paired (out + in) row sharing transfer_pair_id.';
COMMENT ON COLUMN inv_transaction.inv_transaction_id IS 'PK component. Identity-generated.';
COMMENT ON COLUMN inv_transaction.tenant_id          IS 'Tenant scope. FK enforcement is deferred on partitioned tables; post_transaction validates.';
COMMENT ON COLUMN inv_transaction.external_txn_id    IS 'Caller-supplied idempotency key. UNIQUE per (tenant, external_txn_id, posted_at) — a duplicate post returns reason="duplicate" and does NOT insert.';
COMMENT ON COLUMN inv_transaction.product_id         IS 'Resolved from payload product_code.';
COMMENT ON COLUMN inv_transaction.warehouse_id       IS 'Resolved from payload warehouse_code.';
COMMENT ON COLUMN inv_transaction.lot_id             IS 'Resolved from payload lot_code; NULL when not lot-tracked.';
COMMENT ON COLUMN inv_transaction.signed_qty         IS 'Signed quantity. Sign must match txn_type (V5 CHECK): receipt / transfer_in / sales_return > 0; issue / shipment / transfer_out / purchase_return / scrap < 0; adjustment can be either.';
COMMENT ON COLUMN inv_transaction.uom_id             IS 'Unit of measure.';
COMMENT ON COLUMN inv_transaction.txn_type           IS 'One of: receipt, issue, shipment, transfer_out, transfer_in, adjustment, sales_return, purchase_return, scrap. See CHECK constraint.';
COMMENT ON COLUMN inv_transaction.transfer_pair_id   IS 'Set on transfer_in/transfer_out pairs. NULL otherwise. The two rows of a transfer share this UUID so they can be reconciled or rolled back together.';
COMMENT ON COLUMN inv_transaction.source_system      IS 'Origin tag, e.g. "fop", "manual_adjust". Free text; used in observability and audit.';
COMMENT ON COLUMN inv_transaction.posted_at          IS 'Business timestamp. Drives partition routing; the value SHOULD NOT be backdated past the current archive retention window.';
COMMENT ON COLUMN inv_transaction.payload            IS 'Original payload as received. Trimmed to a sha256 reference if it exceeds pipeline_config.payload_max_bytes.';
COMMENT ON COLUMN inv_transaction.created_at         IS 'Insert-time wall clock. Differs from posted_at when a system back-fills historical movements.';

-- ============================================================================
-- Stock balance (public)
-- ============================================================================
COMMENT ON TABLE stock_balance IS
    'Materialised current position per (tenant, product, warehouse, lot). Maintained by triggers on opening_balance + inv_transaction (V2 f_stock_balance_apply; V8 added opening-balance backfill trigger). Used for fast ATP reads; mv_atp is the canonical source.';
COMMENT ON COLUMN stock_balance.lot_id          IS 'Defaults to 0 (meaning "no lot") so the PK is non-NULLable. Real lot rows reference lot.lot_id.';
COMMENT ON COLUMN stock_balance.on_hand_qty     IS 'Running sum = opening_balance.qty + Σ inv_transaction.signed_qty for the key.';
COMMENT ON COLUMN stock_balance.last_updated_at IS 'Timestamp of the most recent inv_transaction or opening_balance that updated this row.';

-- ============================================================================
-- SFDC orders (public)
-- ============================================================================
COMMENT ON TABLE sfdc_order IS
    'Salesforce order header. Pipeline 3 writes via staging.order_inbox → process_order_inbox → upsert_order. order_state machine: open → synced (FOP shipped) → closed | cancelled.';
COMMENT ON COLUMN sfdc_order.sfdc_order_id IS 'Salesforce-supplied identifier. PK.';
COMMENT ON COLUMN sfdc_order.customer_id   IS 'Customer reference, free text.';
COMMENT ON COLUMN sfdc_order.order_state   IS 'open | synced | closed | cancelled. Validated by upsert_order + CHECK.';
COMMENT ON COLUMN sfdc_order.payload       IS 'Original Salesforce payload as received.';

COMMENT ON TABLE sfdc_order_line IS
    'Salesforce order line. Reserved qty for ATP. fop_synced_at is stamped by the inv_transaction trigger (f_reconcile_orders) when a matching issue/shipment posts (V4 narrowed to those txn_types).';
COMMENT ON COLUMN sfdc_order_line.line_no       IS 'Order-local line number.';
COMMENT ON COLUMN sfdc_order_line.qty           IS 'Line quantity. Must be > 0 (V5 CHECK).';
COMMENT ON COLUMN sfdc_order_line.line_state    IS 'open | synced | closed | cancelled. New lines are forced to "open" (V6); cannot be created closed.';
COMMENT ON COLUMN sfdc_order_line.fop_synced_at IS 'Set by f_reconcile_orders when a matching customer-shipment inv_transaction posts. Null = still reserved for ATP, not yet shipped.';

-- ============================================================================
-- Notification outbox (public)
-- ============================================================================
COMMENT ON TABLE notification_outbox IS
    'Pending out-of-band alerts (drift, oversell, reject summaries, etc). Drained by the Java daemon (OutboxDrainer) which POSTs to configured webhooks. status moves pending → delivered | failed → failed_permanent.';
COMMENT ON COLUMN notification_outbox.severity        IS 'info | warn | error | critical (CHECK).';
COMMENT ON COLUMN notification_outbox.source          IS 'Producer tag, e.g. "load_opening_balance", "f_drift_detect", "opening_balance_loader".';
COMMENT ON COLUMN notification_outbox.status          IS 'pending | delivered | failed | failed_permanent (CHECK). Drainer policy in OutboxDrainer.';
COMMENT ON COLUMN notification_outbox.retry_count     IS 'Increments on each failed delivery; row moves to failed_permanent once it hits pipeline_config.max_retries.';
COMMENT ON COLUMN notification_outbox.last_attempt_at IS 'Wall-clock of most recent delivery attempt.';
COMMENT ON COLUMN notification_outbox.delivered_at    IS 'Wall-clock of successful delivery; NULL while unresolved.';

-- ============================================================================
-- Pipeline config (public)
-- ============================================================================
COMMENT ON TABLE pipeline_config IS
    'Business parameters expressed as data, not code. Read via pipeline_config_int / pipeline_config_text helpers. Editing a row takes effect on the next function call — no deploy required.';
COMMENT ON COLUMN pipeline_config.key         IS 'Stable identifier referenced from SQL (PK).';
COMMENT ON COLUMN pipeline_config.value       IS 'Value, always stored as text. Helpers cast at read time.';
COMMENT ON COLUMN pipeline_config.description IS 'What this knob controls and why; required, not for decoration. New keys must include a useful description so on-call can read it.';

-- ============================================================================
-- Materialised view dirty marker (public)
-- ============================================================================
COMMENT ON TABLE mv_atp_dirty IS
    'Per-tenant flag set by triggers when inv_transaction / opening_balance / sfdc_order_line change. refresh_mv_atp clears flagged tenants. Lets us avoid refreshing the whole MV when only one tenant moved.';

-- ============================================================================
-- mv_atp (public) — the ATP truth surface
-- ============================================================================
COMMENT ON MATERIALIZED VIEW mv_atp IS
    'Available-to-Promise per (tenant, product, warehouse). atp = on_hand − reserved (open lines) − allocated (synced-but-not-fop). on_hand = opening_balance.qty + Σ inv_transaction.signed_qty. Refreshed CONCURRENTLY by refresh_mv_atp on a pg_cron schedule (and ad-hoc when mv_atp_dirty has rows).';

-- ============================================================================
-- Staging schema
-- ============================================================================
COMMENT ON TABLE staging.ob_load_batch IS
    'One row per opening-balance CSV ingested. file_hash is sha256(file) and UNIQUE — re-uploading the same content is a no-op.';
COMMENT ON COLUMN staging.ob_load_batch.batch_id       IS 'PK; referenced from staging.ob_load and staging.ob_reject.';
COMMENT ON COLUMN staging.ob_load_batch.file_hash      IS 'sha256 of the source file. UNIQUE; provides idempotency.';
COMMENT ON COLUMN staging.ob_load_batch.status         IS 'in_progress | loaded | rejected. Set by load_opening_balance.';
COMMENT ON COLUMN staging.ob_load_batch.row_count      IS 'Total rows in the file (set after COPY).';
COMMENT ON COLUMN staging.ob_load_batch.accepted_count IS 'Rows successfully promoted to opening_balance.';
COMMENT ON COLUMN staging.ob_load_batch.rejected_count IS 'Rows that failed validation; details in staging.ob_reject.';

COMMENT ON TABLE staging.ob_load IS
    'Raw CSV rows — every column is text so a malformed value never fails the COPY. load_opening_balance parses, validates, and either promotes the row to opening_balance or copies it to staging.ob_reject.';
COMMENT ON COLUMN staging.ob_load.line_no IS 'CSV line number (1-based). Carried through to ob_reject so a partner can find the problem row in their source file.';

COMMENT ON TABLE staging.ob_reject IS
    'Per-row reject log for opening-balance loads. Inspect by batch_id to see why a file rejected rows.';
COMMENT ON COLUMN staging.ob_reject.reason_code   IS 'Short machine-readable reason (unknown_product, invalid_qty, …).';
COMMENT ON COLUMN staging.ob_reject.reason_detail IS 'Human-readable context.';
COMMENT ON COLUMN staging.ob_reject.raw_line      IS 'Original row contents (jsonb) for debugging.';

COMMENT ON TABLE staging.txn_inbox IS
    'External write surface for inventory transactions (Pipeline 2 / FOP). External team INSERTs one row per movement with payload JSON matching post_transaction''s schema. process_txn_inbox drains rows in (tenant, received_at) order, calls post_transaction, and stamps status. UNIQUE (tenant_id, external_txn_id) provides natural idempotency at the inbox level.';
COMMENT ON COLUMN staging.txn_inbox.external_txn_id IS 'Caller-supplied idempotency key. Must match the external_txn_id inside payload; UNIQUE per tenant.';
COMMENT ON COLUMN staging.txn_inbox.payload         IS 'jsonb payload, same schema post_transaction expects (tenant_code, product_code, warehouse_code, signed_qty, txn_type, posted_at, …).';
COMMENT ON COLUMN staging.txn_inbox.status          IS 'pending → processed | rejected. Drain stamps this; readers can poll on status to confirm landing.';
COMMENT ON COLUMN staging.txn_inbox.processed_at    IS 'When the drain settled this row, in either direction.';
COMMENT ON COLUMN staging.txn_inbox.error_detail    IS 'Populated on rejection with the reason returned by post_transaction (unknown_reference, invalid_qty, duplicate, …).';

COMMENT ON TABLE staging.order_inbox IS
    'External write surface for Salesforce orders (Pipeline 3). External team INSERTs one row per order (full upsert payload, not deltas). process_order_inbox drains rows in received_at order and calls upsert_order. No UNIQUE on (tenant, sfdc_order_id) — the same order can be pushed multiple times as it evolves; upsert_order applies state-machine rules.';
COMMENT ON COLUMN staging.order_inbox.payload      IS 'Order payload (header + lines) matching upsert_order''s schema. Resends are expected as the order moves through states.';
COMMENT ON COLUMN staging.order_inbox.status       IS 'pending → processed | rejected.';
COMMENT ON COLUMN staging.order_inbox.error_detail IS 'Reason returned by upsert_order on rejection.';

-- ============================================================================
-- Audit schema
-- ============================================================================
COMMENT ON TABLE audit.audit_log IS
    'Append-only audit log. audit.f_audit_capture trigger writes one row per INSERT/UPDATE/DELETE on watched tables. Partitioned monthly by changed_at; pg_partman archives partitions older than pipeline_config.archive_retention_days.';
COMMENT ON COLUMN audit.audit_log.operation   IS 'I (insert) | U (update) | D (delete). CHECK enforced.';
COMMENT ON COLUMN audit.audit_log.changed_by  IS 'current_user at the time of the change. SECURITY DEFINER functions show the function''s owner, not the caller — pair with notification_outbox.source for caller attribution.';
COMMENT ON COLUMN audit.audit_log.before_data IS 'Row prior state (NULL for INSERT). Trimmed by V7 if payload exceeds size cap.';
COMMENT ON COLUMN audit.audit_log.after_data  IS 'Row new state (NULL for DELETE).';

-- ============================================================================
-- Key functions (the public read/write API)
-- ============================================================================
COMMENT ON FUNCTION fetch_inventory(uuid, bigint, bigint) IS
    'PRIMARY READ API. Returns ATP rows for a tenant, optionally filtered by product_id and/or warehouse_id. Granted to inventoryledger_reader; underlying mv_atp is NOT directly selectable by readers. Refreshed by refresh_mv_atp on cron + dirty-marker.';
COMMENT ON FUNCTION post_transaction(jsonb) IS
    'INTERNAL. Validates payload and applies one inventory movement. SECURITY DEFINER. NOT granted to external writers (V10 revoked) — reachable only via process_txn_inbox.';
COMMENT ON FUNCTION post_transaction_bulk(jsonb[]) IS
    'INTERNAL. Batched form of post_transaction; returns per-row outcomes. Same access rules as post_transaction.';
COMMENT ON FUNCTION upsert_order(jsonb) IS
    'INTERNAL. Applies one Salesforce order upsert (header + lines) with state-machine enforcement. SECURITY DEFINER. NOT granted to external writers (V10 revoked) — reachable only via process_order_inbox.';
COMMENT ON FUNCTION load_opening_balance(bigint) IS
    'INTERNAL. Parses staging.ob_load for a batch_id, promotes valid rows to opening_balance, and copies rejects to staging.ob_reject. Called by the Java daemon (Pipeline 1) inside a transaction.';
COMMENT ON FUNCTION process_txn_inbox(int) IS
    'PIPELINE 2 DRAIN. Pulls up to N pending rows from staging.txn_inbox (default from pipeline_config.txn_inbox_batch_size), calls post_transaction for each, and stamps the inbox row. Runs every minute via pg_cron; safe to invoke ad-hoc.';
COMMENT ON FUNCTION process_order_inbox(int) IS
    'PIPELINE 3 DRAIN. Pulls up to N pending rows from staging.order_inbox (default from pipeline_config.order_inbox_batch_size), calls upsert_order for each, and stamps the inbox row. Runs every minute via pg_cron; safe to invoke ad-hoc.';
COMMENT ON FUNCTION refresh_mv_atp() IS
    'Refreshes mv_atp CONCURRENTLY and clears mv_atp_dirty. Scheduled via pg_cron; also callable ad-hoc for tests.';
COMMENT ON FUNCTION fetch_pending_orders(uuid) IS
    'Diagnostic. Returns sfdc_order_line rows that are still reserved (line_state in open/synced, fop_synced_at IS NULL) for a tenant. Not part of the public API.';
COMMENT ON FUNCTION calculate_inventory(uuid, bigint, bigint) IS
    'Recomputes ATP for one (tenant, product, warehouse, lot) directly from opening_balance + inv_transaction + sfdc_order_line. Slower than fetch_inventory; used by f_drift_detect to compare against mv_atp.';
COMMENT ON FUNCTION pipeline_config_int(text) IS 'Read a pipeline_config value as int. Throws if missing.';
COMMENT ON FUNCTION pipeline_config_text(text) IS 'Read a pipeline_config value as text. Throws if missing.';
COMMENT ON FUNCTION f_purge_staging() IS 'Cron-scheduled cleanup: deletes processed staging.txn_inbox / order_inbox / ob_reject rows older than their respective pipeline_config purge_days. Batched.';
COMMENT ON FUNCTION f_drift_detect() IS 'Cron-scheduled audit: samples drift_sample_pct of (tenant, product, warehouse) keys, compares calculate_inventory vs mv_atp, and emits notification_outbox rows on mismatch.';

-- ============================================================================
-- BI views (aggregations over mv_atp)
-- ============================================================================
COMMENT ON VIEW vw_atp_by_product IS
    'BI roll-up of mv_atp grouped by (tenant, product). Convenience for dashboards that don''t care about warehouse breakdown. Same refresh cadence as mv_atp (reads through, not materialised).';
COMMENT ON VIEW vw_atp_by_warehouse IS
    'BI roll-up of mv_atp grouped by (tenant, warehouse). Same refresh cadence as mv_atp.';

-- ============================================================================
-- Trigger functions (not in the public API; documented so on-call can trace
-- "who wrote this row?" via the trigger name in pg_trigger).
-- ============================================================================
COMMENT ON FUNCTION f_stock_balance_apply() IS
    'TRIGGER on inv_transaction. Maintains stock_balance.on_hand_qty by adding signed_qty on INSERT. inv_transaction is treated as append-only; UPDATE/DELETE are not supported on the ledger.';
COMMENT ON FUNCTION f_stock_balance_opening_apply() IS
    'TRIGGER on opening_balance (V8). Backfills stock_balance when an opening balance is loaded — ensures stock_balance is correct even before the first inv_transaction posts.';
COMMENT ON FUNCTION f_mv_atp_mark_dirty() IS
    'TRIGGER on inv_transaction / sfdc_order_line / opening_balance. Inserts the tenant into mv_atp_dirty so refresh_mv_atp knows which tenants moved since the last refresh.';
COMMENT ON FUNCTION f_reconcile_orders() IS
    'TRIGGER on inv_transaction. When a customer shipment (V4: txn_type in shipment/issue) posts, finds the matching open sfdc_order_line and stamps fop_synced_at — moving the line from "reserved" to "allocated/shipped" for ATP.';
COMMENT ON FUNCTION f_inv_transaction_tenant_check() IS
    'TRIGGER on inv_transaction. Enforces tenant_id consistency between the transaction and the referenced product / warehouse — partitioned tables can''t hold composite FKs, so this guards multi-tenant integrity at write time.';
COMMENT ON FUNCTION audit.f_audit_capture() IS
    'TRIGGER on every audited table. Writes one audit.audit_log row per INSERT/UPDATE/DELETE with before/after JSONB. Tables opt in by installing a trg_audit_<table> trigger pointing at this function (see V7).';
COMMENT ON FUNCTION notify_outbox(uuid, text, text, text, jsonb, text) IS
    'Helper that inserts a notification_outbox row with (tenant_id, severity, source, message, payload, dedup_key). dedup_key (V7) suppresses duplicate alerts within the dedup window. Used by drift, reject summaries, and oversell warnings.';

-- ============================================================================
-- Key indexes (rationale captured so the team knows what they support)
-- ============================================================================
COMMENT ON INDEX idx_inv_transaction_tenant_pw_posted IS
    'Drives the hot path: ATP recompute scans (tenant, product, warehouse, posted_at) ranges. Composite order matches calculate_inventory''s WHERE clause.';
COMMENT ON INDEX idx_inv_transaction_external_id IS
    'Idempotency lookup for post_transaction. UNIQUE on (tenant_id, external_txn_id, posted_at) — a re-post returns reason="duplicate" without inserting.';
COMMENT ON INDEX idx_inv_transaction_transfer_pair IS
    'Partial index on transfer_pair_id (WHERE NOT NULL). Used to reconcile or roll back a transfer''s out+in rows together.';
COMMENT ON INDEX idx_sfdc_order_line_pending IS
    'Partial index on open lines (line_state in (open, synced) AND fop_synced_at IS NULL). The "reserved" set for ATP — kept small so mv_atp refresh stays fast even as historical orders accumulate.';
COMMENT ON INDEX idx_notification_outbox_pending IS
    'Partial index on status=''pending''. OutboxDrainer scans this many times per second; the partial keeps the drained set tiny vs. the full table.';
COMMENT ON INDEX staging.idx_txn_inbox_status IS
    'Drives process_txn_inbox''s claim query (status=''pending'' ORDER BY received_at). FOR UPDATE SKIP LOCKED rides this index.';
COMMENT ON INDEX staging.idx_order_inbox_status IS
    'Drives process_order_inbox''s claim query. Same shape as idx_txn_inbox_status.';
COMMENT ON INDEX idx_mv_atp_key IS
    'UNIQUE (tenant, product, warehouse). Required by REFRESH MATERIALIZED VIEW CONCURRENTLY (Postgres needs a unique index to know which rows changed).';
