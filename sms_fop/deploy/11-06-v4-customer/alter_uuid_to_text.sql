-- ============================================================================
-- Michelin Inventory Ledger — convert UUID → TEXT across all tables
-- Tag: 11-06-v4-customer · applied 2026-06-11
-- ============================================================================
-- Single in-place ALTER. Works whether the DB is empty (just seeded tenants)
-- or has live data. Default for processed.tenant.tenant_id changes from
-- gen_random_uuid() to gen_random_uuid()::text so auto-generation keeps working.
--
-- Always back up first:
--     pg_dump -Fc -f pre_text.dump <dbname>
-- ============================================================================

BEGIN;

-- ============================================================================
-- 1. DROP foreign keys to processed.tenant.tenant_id
--    (auto-generated constraint names; discovered dynamically)
-- ============================================================================
DO $$
DECLARE r RECORD;
BEGIN
    FOR r IN
        SELECT conrelid::regclass::text AS tbl, conname
          FROM pg_constraint
         WHERE contype = 'f'
           AND confrelid = 'processed.tenant'::regclass
    LOOP
        EXECUTE format('ALTER TABLE %s DROP CONSTRAINT %I', r.tbl, r.conname);
        RAISE NOTICE 'dropped FK %.%', r.tbl, r.conname;
    END LOOP;
END$$;

-- ============================================================================
-- 2. ALTER tenant_id columns from UUID to TEXT (parent first, then children)
-- ============================================================================
ALTER TABLE processed.tenant ALTER COLUMN tenant_id DROP DEFAULT;
ALTER TABLE processed.tenant ALTER COLUMN tenant_id TYPE TEXT USING tenant_id::text;
ALTER TABLE processed.tenant ALTER COLUMN tenant_id SET DEFAULT gen_random_uuid()::text;

ALTER TABLE processed.product            ALTER COLUMN tenant_id TYPE TEXT USING tenant_id::text;
ALTER TABLE processed.warehouse          ALTER COLUMN tenant_id TYPE TEXT USING tenant_id::text;
ALTER TABLE processed.uom                ALTER COLUMN tenant_id TYPE TEXT USING tenant_id::text;
ALTER TABLE processed.lot                ALTER COLUMN tenant_id TYPE TEXT USING tenant_id::text;
ALTER TABLE processed.opening_balance    ALTER COLUMN tenant_id TYPE TEXT USING tenant_id::text;
ALTER TABLE processed.stock_balance      ALTER COLUMN tenant_id TYPE TEXT USING tenant_id::text;
ALTER TABLE processed.inv_transaction    ALTER COLUMN tenant_id TYPE TEXT USING tenant_id::text;
ALTER TABLE processed.sfdc_order         ALTER COLUMN tenant_id TYPE TEXT USING tenant_id::text;
ALTER TABLE processed.sfdc_order_line    ALTER COLUMN tenant_id TYPE TEXT USING tenant_id::text;
ALTER TABLE processed.notification_outbox ALTER COLUMN tenant_id TYPE TEXT USING tenant_id::text;
ALTER TABLE audit.audit_log              ALTER COLUMN tenant_id TYPE TEXT USING tenant_id::text;

-- Non-FK UUID column on inv_transaction (matched-pair id for transfers)
ALTER TABLE processed.inv_transaction
    ALTER COLUMN transfer_pair_id TYPE TEXT USING transfer_pair_id::text;

-- ============================================================================
-- 3. RE-ADD foreign keys with the new TEXT types
-- ============================================================================
ALTER TABLE processed.product
    ADD CONSTRAINT product_tenant_id_fkey
    FOREIGN KEY (tenant_id) REFERENCES processed.tenant(tenant_id);
ALTER TABLE processed.warehouse
    ADD CONSTRAINT warehouse_tenant_id_fkey
    FOREIGN KEY (tenant_id) REFERENCES processed.tenant(tenant_id);
ALTER TABLE processed.uom
    ADD CONSTRAINT uom_tenant_id_fkey
    FOREIGN KEY (tenant_id) REFERENCES processed.tenant(tenant_id);
ALTER TABLE processed.lot
    ADD CONSTRAINT lot_tenant_id_fkey
    FOREIGN KEY (tenant_id) REFERENCES processed.tenant(tenant_id);
ALTER TABLE processed.opening_balance
    ADD CONSTRAINT opening_balance_tenant_id_fkey
    FOREIGN KEY (tenant_id) REFERENCES processed.tenant(tenant_id);
ALTER TABLE processed.stock_balance
    ADD CONSTRAINT stock_balance_tenant_id_fkey
    FOREIGN KEY (tenant_id) REFERENCES processed.tenant(tenant_id);
ALTER TABLE processed.inv_transaction
    ADD CONSTRAINT inv_transaction_tenant_id_fkey
    FOREIGN KEY (tenant_id) REFERENCES processed.tenant(tenant_id);
ALTER TABLE processed.sfdc_order
    ADD CONSTRAINT sfdc_order_tenant_id_fkey
    FOREIGN KEY (tenant_id) REFERENCES processed.tenant(tenant_id);

-- ============================================================================
-- 4. REPLACE notify_outbox — signature change (UUID → TEXT in first param)
--    requires DROP + CREATE (CREATE OR REPLACE can't change argument types).
-- ============================================================================
DROP FUNCTION IF EXISTS processed.notify_outbox(UUID, TEXT, TEXT, TEXT, JSONB, TEXT);

CREATE OR REPLACE FUNCTION processed.notify_outbox(
    p_tenant_id TEXT, p_severity TEXT, p_source TEXT,
    p_message TEXT, p_payload JSONB, p_dedup_key TEXT
)
RETURNS BIGINT
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = processed, public, pg_catalog
AS $f$
DECLARE v_id BIGINT;
BEGIN
    IF p_dedup_key IS NOT NULL THEN
        UPDATE processed.notification_outbox
           SET repeat_count = repeat_count + 1
         WHERE dedup_key = p_dedup_key AND status = 'pending'
        RETURNING outbox_id INTO v_id;
        IF v_id IS NOT NULL THEN RETURN v_id; END IF;
    END IF;
    INSERT INTO processed.notification_outbox
        (tenant_id, severity, source, message, payload, dedup_key)
    VALUES (p_tenant_id, p_severity, p_source, p_message, p_payload, p_dedup_key)
    RETURNING outbox_id INTO v_id;
    RETURN v_id;
END;
$f$;

-- ============================================================================
-- 5. CREATE OR REPLACE the 4 functions that DECLARE UUID-typed variables.
--    Bodies are byte-for-byte identical to customer_install.sql except for
--    swapping `UUID` → `TEXT` in the DECLARE blocks.
-- ============================================================================

-- 5a. load_stocklevel
CREATE OR REPLACE FUNCTION load_stocklevel(p_file_name TEXT)
RETURNS TABLE (
    batch_id      BIGINT,
    rows_accepted INTEGER,
    rows_rejected INTEGER,
    status        TEXT
)
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = processed, staging, public, pg_catalog
AS $f$
DECLARE
    v_batch_id   BIGINT;
    v_existing   BIGINT;
    v_accepted   INTEGER := 0;
    v_rejected   INTEGER := 0;
    v_status     TEXT;
    v_master_row RECORD;
    v_tenant_id  TEXT;          -- was UUID
BEGIN
    IF p_file_name IS NULL OR length(p_file_name) = 0 THEN
        RAISE EXCEPTION 'load_stocklevel: file_name is required';
    END IF;

    SELECT b.batch_id INTO v_existing
      FROM staging.stocklevel_batch b WHERE b.file_name = p_file_name FOR UPDATE;
    IF v_existing IS NOT NULL THEN
        DELETE FROM processed.opening_balance WHERE batch_id = v_existing;
        UPDATE staging.stocklevel_batch
           SET status = 'pending', completed_at = NULL,
               rows_accepted = 0, rows_rejected = 0
         WHERE batch_id = v_existing;
        v_batch_id := v_existing;
    ELSE
        INSERT INTO staging.stocklevel_batch (file_name) VALUES (p_file_name)
        RETURNING staging.stocklevel_batch.batch_id INTO v_batch_id;
    END IF;

    FOR v_master_row IN
        SELECT DISTINCT s.tenant_code,
               s.item_segment1 AS product_code,
               s.warehouse     AS warehouse_code,
               s.primary_uom   AS uom_code
          FROM staging.stocklevel_inbox s
         WHERE s.file_name = p_file_name AND s.promoted_at IS NULL AND s.tenant_code IS NOT NULL
    LOOP
        SELECT t.tenant_id INTO v_tenant_id
          FROM processed.tenant t WHERE t.tenant_code = v_master_row.tenant_code;
        IF v_tenant_id IS NULL THEN CONTINUE; END IF;
        IF v_master_row.product_code IS NOT NULL THEN
            INSERT INTO processed.product (tenant_id, product_code, name)
            VALUES (v_tenant_id, v_master_row.product_code, v_master_row.product_code)
            ON CONFLICT (tenant_id, product_code) DO NOTHING;
        END IF;
        IF v_master_row.warehouse_code IS NOT NULL THEN
            INSERT INTO processed.warehouse (tenant_id, warehouse_code, name)
            VALUES (v_tenant_id, v_master_row.warehouse_code, v_master_row.warehouse_code)
            ON CONFLICT (tenant_id, warehouse_code) DO NOTHING;
        END IF;
        IF v_master_row.uom_code IS NOT NULL THEN
            INSERT INTO processed.uom (tenant_id, uom_code, name)
            VALUES (v_tenant_id, v_master_row.uom_code, v_master_row.uom_code)
            ON CONFLICT (tenant_id, uom_code) DO NOTHING;
        END IF;
    END LOOP;

    WITH src AS (
        SELECT s.inbox_id, s.tenant_code,
               s.item_segment1 AS product_code,
               s.warehouse     AS warehouse_code,
               COALESCE(NULLIF(trim(s.subinventory), ''), '') AS subinventory,
               COALESCE(NULLIF(trim(s.stock_status), ''), 'LIBERATED') AS stock_status,
               s.primary_uom   AS uom_code,
               s.primary_quantity AS qty,
               s.snapshot_date,
               t.tenant_id, p.product_id, w.warehouse_id, u.uom_id,
               CASE
                   WHEN t.tenant_id      IS NULL THEN 'unknown_tenant'
                   WHEN p.product_id     IS NULL THEN 'unknown_product'
                   WHEN w.warehouse_id   IS NULL THEN 'unknown_warehouse'
                   WHEN u.uom_id         IS NULL THEN 'unknown_uom'
                   WHEN s.primary_quantity IS NULL THEN 'missing_qty'
                   WHEN s.snapshot_date  IS NULL THEN 'missing_snapshot_date'
                   ELSE NULL
               END AS reject_reason
          FROM staging.stocklevel_inbox s
          LEFT JOIN processed.tenant    t ON t.tenant_code    = s.tenant_code
          LEFT JOIN processed.product   p ON p.product_code   = s.item_segment1 AND p.tenant_id = t.tenant_id
          LEFT JOIN processed.warehouse w ON w.warehouse_code = s.warehouse     AND w.tenant_id = t.tenant_id
          LEFT JOIN processed.uom       u ON u.uom_code       = s.primary_uom   AND u.tenant_id = t.tenant_id
         WHERE s.file_name = p_file_name AND s.promoted_at IS NULL
    ),
    inserted AS (
        INSERT INTO processed.opening_balance (
            tenant_id, tenant_code, product_id, product_code,
            warehouse_id, warehouse_code,
            subinventory, stock_status,
            qty, uom_id, uom_code,
            as_of_date, batch_id, source_file
        )
        SELECT tenant_id, tenant_code, product_id, product_code,
               warehouse_id, warehouse_code,
               subinventory, stock_status,
               qty, uom_id, uom_code,
               snapshot_date, v_batch_id, p_file_name
          FROM src WHERE reject_reason IS NULL
        RETURNING opening_balance_id
    ),
    counts AS (
        SELECT (SELECT COUNT(*)::int FROM inserted) AS ok_count,
               (SELECT COUNT(*)::int FROM src WHERE reject_reason IS NOT NULL) AS bad_count
    )
    SELECT ok_count, bad_count INTO v_accepted, v_rejected FROM counts;

    UPDATE staging.stocklevel_inbox SET promoted_at = now()
     WHERE file_name = p_file_name AND promoted_at IS NULL;

    v_status := CASE WHEN v_rejected = 0 THEN 'loaded'
                     WHEN v_accepted = 0 THEN 'failed'
                     ELSE 'partial' END;
    UPDATE staging.stocklevel_batch
       SET status = v_status, completed_at = now(),
           rows_accepted = v_accepted, rows_rejected = v_rejected
     WHERE staging.stocklevel_batch.batch_id = v_batch_id;

    RETURN QUERY SELECT v_batch_id, v_accepted, v_rejected, v_status;
END;
$f$;

-- 5b. load_transactions
CREATE OR REPLACE FUNCTION load_transactions(p_limit INTEGER DEFAULT 500)
RETURNS TABLE (rows_processed INTEGER, rows_failed INTEGER, rows_superseded INTEGER)
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = processed, staging, public, pg_catalog
AS $f$
DECLARE
    v_row     RECORD;
    v_tid     TEXT;             -- was UUID
    v_pid     BIGINT;
    v_wid     BIGINT;
    v_uid     BIGINT;
    v_ok      INTEGER := 0;
    v_bad     INTEGER := 0;
    v_sup     INTEGER := 0;
    v_payload JSONB;
BEGIN
    WITH ranked AS (
        SELECT inbox_id,
               row_number() OVER (PARTITION BY tenant_code, external_txn_id
                                  ORDER BY received_at DESC, inbox_id DESC) AS rn
          FROM staging.txn_inbox
         WHERE status = 'pending'
    ),
    superseded AS (
        UPDATE staging.txn_inbox ti
           SET status = 'superseded', processed_at = now()
          FROM ranked r
         WHERE ti.inbox_id = r.inbox_id AND r.rn > 1
         RETURNING ti.inbox_id
    )
    SELECT COUNT(*)::int INTO v_sup FROM superseded;

    FOR v_row IN
        SELECT inbox_id, tenant_code, external_txn_id, payload
          FROM staging.txn_inbox
         WHERE status = 'pending'
         ORDER BY received_at, inbox_id
         LIMIT p_limit
         FOR UPDATE SKIP LOCKED
    LOOP
        v_payload := v_row.payload;
        BEGIN
            SELECT t.tenant_id INTO v_tid
              FROM processed.tenant t WHERE t.tenant_code = v_row.tenant_code;
            IF v_tid IS NULL THEN
                UPDATE staging.txn_inbox SET status='rejected', processed_at=now(),
                       reject_reason='unknown_tenant'
                 WHERE inbox_id = v_row.inbox_id;
                v_bad := v_bad + 1;
                CONTINUE;
            END IF;

            SELECT p.product_id   INTO v_pid FROM processed.product   p WHERE p.tenant_id = v_tid AND p.product_code   = v_payload->>'product_code';
            SELECT w.warehouse_id INTO v_wid FROM processed.warehouse w WHERE w.tenant_id = v_tid AND w.warehouse_code = v_payload->>'warehouse_code';
            SELECT u.uom_id       INTO v_uid FROM processed.uom       u WHERE u.tenant_id = v_tid AND u.uom_code       = v_payload->>'uom_code';

            IF v_pid IS NULL OR v_wid IS NULL OR v_uid IS NULL THEN
                UPDATE staging.txn_inbox SET status='rejected', processed_at=now(),
                       reject_reason=CASE WHEN v_pid IS NULL THEN 'unknown_product'
                                          WHEN v_wid IS NULL THEN 'unknown_warehouse'
                                          ELSE 'unknown_uom' END
                 WHERE inbox_id = v_row.inbox_id;
                v_bad := v_bad + 1;
                CONTINUE;
            END IF;

            INSERT INTO processed.inv_transaction (
                tenant_id, tenant_code, external_txn_id,
                product_id, product_code, warehouse_id, warehouse_code,
                subinventory, stock_status,
                signed_qty, uom_id, uom_code, txn_type,
                posted_at, sfdc_order_id, sfdc_line_id, payload
            )
            SELECT v_tid, v_row.tenant_code, v_row.external_txn_id,
                   v_pid, v_payload->>'product_code', v_wid, v_payload->>'warehouse_code',
                   v_payload->>'subinventory',
                   COALESCE(v_payload->>'stock_status', 'LIBERATED'),
                   (v_payload->>'signed_qty')::numeric, v_uid, v_payload->>'uom_code',
                   COALESCE(v_payload->>'txn_type', 'unknown'),
                   COALESCE((v_payload->>'posted_at')::timestamptz, now()),
                   v_payload->>'sfdc_order_id',
                   v_payload->>'sfdc_line_id',
                   v_payload
             WHERE NOT EXISTS (
                 SELECT 1 FROM processed.inv_transaction it
                  WHERE it.tenant_id = v_tid AND it.external_txn_id = v_row.external_txn_id
             );

            UPDATE staging.txn_inbox SET status='processed', processed_at=now() WHERE inbox_id = v_row.inbox_id;
            v_ok := v_ok + 1;
        EXCEPTION WHEN OTHERS THEN
            UPDATE staging.txn_inbox SET status='rejected', processed_at=now(),
                   reject_reason=SQLERRM
             WHERE inbox_id = v_row.inbox_id;
            v_bad := v_bad + 1;
        END;
    END LOOP;

    RETURN QUERY SELECT v_ok, v_bad, v_sup;
END;
$f$;

-- 5c. load_orders
CREATE OR REPLACE FUNCTION load_orders(p_limit INTEGER DEFAULT 500)
RETURNS TABLE (rows_processed INTEGER, rows_failed INTEGER, rows_superseded INTEGER)
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = processed, staging, public, pg_catalog
AS $f$
DECLARE
    v_row      RECORD;
    v_tid      TEXT;            -- was UUID
    v_line     JSONB;
    v_line_no  INT;
    v_pid      BIGINT;
    v_wid      BIGINT;
    v_uid      BIGINT;
    v_ok       INTEGER := 0;
    v_bad      INTEGER := 0;
    v_sup      INTEGER := 0;
    v_payload  JSONB;
BEGIN
    WITH ranked AS (
        SELECT inbox_id,
               row_number() OVER (PARTITION BY tenant_code, sfdc_order_id
                                  ORDER BY received_at DESC, inbox_id DESC) AS rn
          FROM staging.order_inbox
         WHERE status = 'pending'
    ),
    superseded AS (
        UPDATE staging.order_inbox oi
           SET status = 'superseded', processed_at = now()
          FROM ranked r
         WHERE oi.inbox_id = r.inbox_id AND r.rn > 1
         RETURNING oi.inbox_id
    )
    SELECT COUNT(*)::int INTO v_sup FROM superseded;

    FOR v_row IN
        SELECT inbox_id, tenant_code, sfdc_order_id, payload, received_at
          FROM staging.order_inbox
         WHERE status = 'pending'
         ORDER BY received_at, inbox_id
         LIMIT p_limit
         FOR UPDATE SKIP LOCKED
    LOOP
        v_payload := v_row.payload;
        BEGIN
            SELECT t.tenant_id INTO v_tid
              FROM processed.tenant t WHERE t.tenant_code = v_row.tenant_code;
            IF v_tid IS NULL THEN
                UPDATE staging.order_inbox SET status='rejected', processed_at=now(),
                       reject_reason='unknown_tenant'
                 WHERE inbox_id = v_row.inbox_id;
                v_bad := v_bad + 1;
                CONTINUE;
            END IF;

            DELETE FROM processed.sfdc_order WHERE sfdc_order_id = v_row.sfdc_order_id;

            INSERT INTO processed.sfdc_order (
                sfdc_order_id, tenant_id, tenant_code, customer_id,
                order_state, payload, received_at
            )
            VALUES (
                v_row.sfdc_order_id, v_tid, v_row.tenant_code,
                COALESCE(v_payload->>'customer_id', 'unknown'),
                COALESCE(v_payload->>'order_state', 'open'),
                v_payload, v_row.received_at
            );

            v_line_no := 0;
            FOR v_line IN SELECT * FROM jsonb_array_elements(COALESCE(v_payload->'lines', '[]'::jsonb))
            LOOP
                v_line_no := v_line_no + 1;
                SELECT p.product_id   INTO v_pid FROM processed.product   p WHERE p.tenant_id = v_tid AND p.product_code   = v_line->>'product_code';
                SELECT w.warehouse_id INTO v_wid FROM processed.warehouse w WHERE w.tenant_id = v_tid AND w.warehouse_code = v_line->>'warehouse_code';
                SELECT u.uom_id       INTO v_uid FROM processed.uom       u WHERE u.tenant_id = v_tid AND u.uom_code       = v_line->>'uom_code';

                IF v_pid IS NULL OR v_wid IS NULL OR v_uid IS NULL THEN
                    RAISE EXCEPTION 'unknown master in line %', v_line_no;
                END IF;

                INSERT INTO processed.sfdc_order_line (
                    sfdc_order_id, line_no, sfdc_line_id,
                    tenant_id, tenant_code,
                    product_id, product_code, warehouse_id, warehouse_code,
                    subinventory, stock_status,
                    qty, uom_id, uom_code, line_state, payload
                )
                VALUES (
                    v_row.sfdc_order_id, v_line_no, v_line->>'sfdc_line_id',
                    v_tid, v_row.tenant_code,
                    v_pid, v_line->>'product_code', v_wid, v_line->>'warehouse_code',
                    COALESCE(v_line->>'subinventory', ''),
                    COALESCE(v_line->>'stock_status', 'LIBERATED'),
                    (v_line->>'qty')::numeric, v_uid, v_line->>'uom_code',
                    COALESCE(v_line->>'line_state', 'open'),
                    v_line
                );
            END LOOP;

            UPDATE staging.order_inbox SET status='processed', processed_at=now() WHERE inbox_id = v_row.inbox_id;
            v_ok := v_ok + 1;
        EXCEPTION WHEN OTHERS THEN
            UPDATE staging.order_inbox SET status='rejected', processed_at=now(),
                   reject_reason=SQLERRM
             WHERE inbox_id = v_row.inbox_id;
            v_bad := v_bad + 1;
        END;
    END LOOP;

    RETURN QUERY SELECT v_ok, v_bad, v_sup;
END;
$f$;

-- 5d. recalculate_stock_balance
CREATE OR REPLACE FUNCTION recalculate_stock_balance(p_tenant_code TEXT)
RETURNS TABLE (rows_rebuilt INTEGER)
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = processed, public, pg_catalog
AS $f$
DECLARE
    v_tid TEXT;                 -- was UUID
    v_n   INTEGER := 0;
BEGIN
    SELECT tenant_id INTO v_tid FROM processed.tenant WHERE tenant_code = p_tenant_code;
    IF v_tid IS NULL THEN
        RAISE EXCEPTION 'recalculate_stock_balance: unknown tenant %', p_tenant_code;
    END IF;

    DELETE FROM processed.stock_balance WHERE tenant_id = v_tid;

    WITH latest_ob AS (
        SELECT DISTINCT ON (tenant_id, product_id, warehouse_id, subinventory, stock_status, COALESCE(lot_id, 0))
               tenant_id, tenant_code, product_id, product_code,
               warehouse_id, warehouse_code, subinventory, stock_status,
               lot_id, lot_code, qty, uom_id, uom_code, as_of_date
          FROM processed.opening_balance
         WHERE tenant_id = v_tid
         ORDER BY tenant_id, product_id, warehouse_id, subinventory, stock_status,
                  COALESCE(lot_id, 0), as_of_date DESC
    ),
    txn_after AS (
        SELECT tx.tenant_id, tx.product_id, tx.warehouse_id,
               COALESCE(tx.subinventory, '') AS subinventory,
               COALESCE(tx.stock_status, 'LIBERATED') AS stock_status,
               COALESCE(tx.lot_id, 0) AS lot_key,
               SUM(tx.signed_qty) AS sum_signed
          FROM processed.inv_transaction tx
          JOIN latest_ob lo ON tx.tenant_id    = lo.tenant_id
                           AND tx.product_id   = lo.product_id
                           AND tx.warehouse_id = lo.warehouse_id
                           AND COALESCE(tx.subinventory, '') = lo.subinventory
                           AND COALESCE(tx.stock_status, 'LIBERATED') = lo.stock_status
                           AND COALESCE(tx.lot_id, 0) = COALESCE(lo.lot_id, 0)
                           AND tx.posted_at >= lo.as_of_date
         GROUP BY tx.tenant_id, tx.product_id, tx.warehouse_id,
                  COALESCE(tx.subinventory, ''),
                  COALESCE(tx.stock_status, 'LIBERATED'),
                  COALESCE(tx.lot_id, 0)
    ),
    reservations AS (
        SELECT sol.tenant_id, sol.product_id, sol.warehouse_id,
               sol.subinventory, sol.stock_status,
               SUM(sol.qty) AS sum_reserved
          FROM processed.sfdc_order_line sol
         WHERE sol.tenant_id = v_tid AND sol.line_state IN ('open','synced')
         GROUP BY sol.tenant_id, sol.product_id, sol.warehouse_id,
                  sol.subinventory, sol.stock_status
    ),
    rebuilt AS (
        INSERT INTO processed.stock_balance (
            tenant_id, tenant_code, product_id, product_code,
            warehouse_id, warehouse_code, subinventory, stock_status,
            lot_id, lot_code, on_hand_qty, reserved_qty,
            uom_id, uom_code, last_updated_at
        )
        SELECT lo.tenant_id, lo.tenant_code, lo.product_id, lo.product_code,
               lo.warehouse_id, lo.warehouse_code, lo.subinventory, lo.stock_status,
               COALESCE(lo.lot_id, 0), lo.lot_code,
               lo.qty + COALESCE(ta.sum_signed, 0),
               COALESCE(rv.sum_reserved, 0),
               lo.uom_id, lo.uom_code, now()
          FROM latest_ob lo
          LEFT JOIN txn_after ta
                 ON ta.tenant_id    = lo.tenant_id
                AND ta.product_id   = lo.product_id
                AND ta.warehouse_id = lo.warehouse_id
                AND ta.subinventory = lo.subinventory
                AND ta.stock_status = lo.stock_status
                AND ta.lot_key      = COALESCE(lo.lot_id, 0)
          LEFT JOIN reservations rv
                 ON rv.tenant_id    = lo.tenant_id
                AND rv.product_id   = lo.product_id
                AND rv.warehouse_id = lo.warehouse_id
                AND rv.subinventory = lo.subinventory
                AND rv.stock_status = lo.stock_status
        RETURNING 1
    )
    SELECT COUNT(*) INTO v_n FROM rebuilt;

    RETURN QUERY SELECT v_n;
END;
$f$;

COMMIT;

-- ============================================================================
-- VERIFY post-alter
-- ============================================================================
--   SELECT column_name, data_type FROM information_schema.columns
--    WHERE column_name = 'tenant_id' AND data_type = 'text'
--      AND table_schema IN ('processed','audit') ORDER BY table_schema, table_name;
--   -- expect 13 rows (all tenant_id columns), all data_type = text
--
--   SELECT proname, pg_get_function_arguments(oid)
--     FROM pg_proc WHERE proname='notify_outbox';
--   -- expect: p_tenant_id text, p_severity text, ...
