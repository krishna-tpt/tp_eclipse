-- ============================================================================
-- Michelin Inventory Ledger — In-place upgrade v3 → v4
-- Tag: 11-06-v4-customer (2026-06-11)
-- ============================================================================
-- Use this when the customer DB has live data you want to preserve. For
-- fresh / pre-production environments, prefer the clean reinstall:
--
--     DROP SCHEMA processed, staging, audit CASCADE;
--     psql -f customer_install.sql
--
-- This script is idempotent against v3 — running it twice is safe.
-- Always back up first:
--     pg_dump -Fc -f pre_v4.dump <dbname>
-- ============================================================================

BEGIN;

-- ============================================================================
-- 1. DROP existing triggers (they'd see partial schema during the migration)
-- ============================================================================
DROP TRIGGER IF EXISTS trg_opening_balance_stock_apply ON processed.opening_balance;
DROP TRIGGER IF EXISTS trg_inv_transaction_stock_apply ON processed.inv_transaction;
DROP TRIGGER IF EXISTS trg_sfdc_order_line_reservation ON processed.sfdc_order_line;

-- ============================================================================
-- 2. DROP staging UNIQUE constraints (auto-generated names — discover via DO)
-- ============================================================================
DO $$
DECLARE c TEXT;
BEGIN
    SELECT conname INTO c
      FROM pg_constraint
     WHERE conrelid = 'staging.txn_inbox'::regclass
       AND contype  = 'u'
       AND pg_get_constraintdef(oid) ILIKE '%tenant_code%external_txn_id%';
    IF c IS NOT NULL THEN
        EXECUTE format('ALTER TABLE staging.txn_inbox DROP CONSTRAINT %I', c);
        RAISE NOTICE 'dropped txn_inbox UNIQUE: %', c;
    END IF;

    SELECT conname INTO c
      FROM pg_constraint
     WHERE conrelid = 'staging.order_inbox'::regclass
       AND contype  = 'u'
       AND pg_get_constraintdef(oid) ILIKE '%tenant_code%sfdc_order_id%';
    IF c IS NOT NULL THEN
        EXECUTE format('ALTER TABLE staging.order_inbox DROP CONSTRAINT %I', c);
        RAISE NOTICE 'dropped order_inbox UNIQUE: %', c;
    END IF;
END$$;

-- ============================================================================
-- 3. EXTEND inbox status CHECK to include 'superseded'
-- ============================================================================
DO $$
DECLARE c TEXT;
BEGIN
    SELECT conname INTO c FROM pg_constraint
     WHERE conrelid = 'staging.txn_inbox'::regclass AND contype = 'c'
       AND pg_get_constraintdef(oid) ILIKE '%status%';
    IF c IS NOT NULL THEN
        EXECUTE format('ALTER TABLE staging.txn_inbox DROP CONSTRAINT %I', c);
    END IF;
    ALTER TABLE staging.txn_inbox
        ADD CONSTRAINT txn_inbox_status_check
        CHECK (status IN ('pending','processed','rejected','superseded'));

    SELECT conname INTO c FROM pg_constraint
     WHERE conrelid = 'staging.order_inbox'::regclass AND contype = 'c'
       AND pg_get_constraintdef(oid) ILIKE '%status%';
    IF c IS NOT NULL THEN
        EXECUTE format('ALTER TABLE staging.order_inbox DROP CONSTRAINT %I', c);
    END IF;
    ALTER TABLE staging.order_inbox
        ADD CONSTRAINT order_inbox_status_check
        CHECK (status IN ('pending','processed','rejected','superseded'));
END$$;

-- ============================================================================
-- 4. ADD COLUMNs (IF NOT EXISTS for idempotency)
-- ============================================================================
ALTER TABLE processed.sfdc_order_line
    ADD COLUMN IF NOT EXISTS subinventory TEXT NOT NULL DEFAULT '';
ALTER TABLE processed.sfdc_order_line
    ADD COLUMN IF NOT EXISTS stock_status TEXT NOT NULL DEFAULT 'LIBERATED';
ALTER TABLE processed.sfdc_order_line
    ADD COLUMN IF NOT EXISTS sfdc_line_id TEXT;

ALTER TABLE processed.inv_transaction
    ADD COLUMN IF NOT EXISTS sfdc_line_id TEXT;

ALTER TABLE processed.sfdc_order
    ADD COLUMN IF NOT EXISTS received_at TIMESTAMPTZ NOT NULL DEFAULT now();

ALTER TABLE processed.opening_balance
    ADD COLUMN IF NOT EXISTS source_file TEXT;

-- ============================================================================
-- 5. New indexes
-- ============================================================================
CREATE INDEX IF NOT EXISTS idx_inv_transaction_line_link
    ON processed.inv_transaction (sfdc_line_id) WHERE sfdc_line_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_sfdc_order_line_sfdc_line_id
    ON processed.sfdc_order_line (sfdc_line_id) WHERE sfdc_line_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_sfdc_order_line_subinv_active
    ON processed.sfdc_order_line (tenant_id, product_id, warehouse_id, subinventory, stock_status)
    WHERE line_state IN ('open','synced');
CREATE INDEX IF NOT EXISTS idx_opening_balance_source_file
    ON processed.opening_balance (source_file) WHERE source_file IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_txn_inbox_pending_key
    ON staging.txn_inbox (tenant_code, external_txn_id, received_at DESC)
    WHERE status = 'pending';
CREATE INDEX IF NOT EXISTS idx_order_inbox_pending_key
    ON staging.order_inbox (tenant_code, sfdc_order_id, received_at DESC)
    WHERE status = 'pending';

-- ============================================================================
-- 6. REPLACE all functions (bodies identical to customer_install.sql)
-- ============================================================================

CREATE OR REPLACE FUNCTION processed.f_stock_balance_opening_apply()
RETURNS trigger LANGUAGE plpgsql AS $f$
DECLARE
    v_latest_date DATE;
BEGIN
    SELECT MAX(ob.as_of_date) INTO v_latest_date
      FROM processed.opening_balance ob
     WHERE ob.tenant_id    = NEW.tenant_id
       AND ob.product_id   = NEW.product_id
       AND ob.warehouse_id = NEW.warehouse_id
       AND ob.subinventory = NEW.subinventory
       AND ob.stock_status = NEW.stock_status
       AND COALESCE(ob.lot_id, 0) = COALESCE(NEW.lot_id, 0)
       AND ob.opening_balance_id <> NEW.opening_balance_id;

    IF v_latest_date IS NULL OR NEW.as_of_date >= v_latest_date THEN
        INSERT INTO processed.stock_balance (
            tenant_id, tenant_code, product_id, product_code,
            warehouse_id, warehouse_code, subinventory, stock_status,
            lot_id, lot_code, on_hand_qty, reserved_qty,
            uom_id, uom_code, last_updated_at
        )
        VALUES (
            NEW.tenant_id, NEW.tenant_code, NEW.product_id, NEW.product_code,
            NEW.warehouse_id, NEW.warehouse_code, NEW.subinventory, NEW.stock_status,
            COALESCE(NEW.lot_id, 0), NEW.lot_code, NEW.qty, 0,
            NEW.uom_id, NEW.uom_code, now()
        )
        ON CONFLICT (tenant_id, product_id, warehouse_id, subinventory, stock_status, lot_id)
        DO UPDATE SET
            on_hand_qty     = EXCLUDED.on_hand_qty,
            last_updated_at = now();
    ELSE
        PERFORM processed.notify_outbox(
            NEW.tenant_id, 'WARN', 'opening_balance_apply',
            format('back-dated opening_balance (as_of=%s, latest=%s) — run recalculate_stock_balance to apply',
                   NEW.as_of_date, v_latest_date),
            jsonb_build_object('product_code', NEW.product_code,
                               'warehouse_code', NEW.warehouse_code,
                               'subinventory', NEW.subinventory),
            'ob_backdated:' || NEW.tenant_code || ':' || NEW.product_code
            || ':' || NEW.warehouse_code || ':' || NEW.as_of_date
            || ':' || NEW.subinventory
        );
    END IF;
    RETURN NEW;
END;
$f$;

CREATE OR REPLACE FUNCTION processed.f_stock_balance_txn_apply()
RETURNS trigger LANGUAGE plpgsql AS $f$
DECLARE
    v_lot_key      BIGINT := COALESCE(NEW.lot_id, 0);
    v_subinv       TEXT   := COALESCE(NEW.subinventory, '');
    v_stock_status TEXT   := COALESCE(NEW.stock_status, 'LIBERATED');
BEGIN
    INSERT INTO processed.stock_balance (
        tenant_id, tenant_code, product_id, product_code,
        warehouse_id, warehouse_code, subinventory, stock_status,
        lot_id, lot_code, on_hand_qty, reserved_qty,
        uom_id, uom_code, last_updated_at
    )
    VALUES (
        NEW.tenant_id, NEW.tenant_code, NEW.product_id, NEW.product_code,
        NEW.warehouse_id, NEW.warehouse_code, v_subinv, v_stock_status,
        v_lot_key, NEW.lot_code, NEW.signed_qty, 0,
        NEW.uom_id, NEW.uom_code, now()
    )
    ON CONFLICT (tenant_id, product_id, warehouse_id, subinventory, stock_status, lot_id)
    DO UPDATE SET
        on_hand_qty     = processed.stock_balance.on_hand_qty + EXCLUDED.on_hand_qty,
        last_updated_at = now();

    IF NEW.sfdc_order_id IS NOT NULL THEN
        UPDATE processed.sfdc_order_line
           SET line_state = 'closed', updated_at = now()
         WHERE sfdc_order_id = NEW.sfdc_order_id
           AND line_state IN ('open','synced');
    END IF;
    RETURN NEW;
END;
$f$;

CREATE OR REPLACE FUNCTION processed.f_stock_balance_reservation_apply()
RETURNS trigger LANGUAGE plpgsql AS $f$
DECLARE
    v_active_states TEXT[] := ARRAY['open','synced'];
    v_was_active    BOOLEAN := false;
    v_is_active     BOOLEAN := false;
    v_release_qty   NUMERIC := 0;
    v_reserve_qty   NUMERIC := 0;
    v_old_subinv    TEXT;
    v_old_status    TEXT;
BEGIN
    IF TG_OP IN ('DELETE','UPDATE') THEN
        v_was_active := OLD.line_state = ANY(v_active_states);
        IF v_was_active THEN
            v_release_qty := OLD.qty;
            v_old_subinv  := OLD.subinventory;
            v_old_status  := OLD.stock_status;
        END IF;
    END IF;

    IF TG_OP IN ('INSERT','UPDATE') THEN
        v_is_active := NEW.line_state = ANY(v_active_states);
        IF v_is_active THEN
            v_reserve_qty := NEW.qty;
        END IF;
    END IF;

    IF v_release_qty > 0 THEN
        UPDATE processed.stock_balance
           SET reserved_qty    = reserved_qty - v_release_qty,
               last_updated_at = now()
         WHERE tenant_id    = OLD.tenant_id
           AND product_id   = OLD.product_id
           AND warehouse_id = OLD.warehouse_id
           AND subinventory = v_old_subinv
           AND stock_status = v_old_status
           AND lot_id       = 0;
    END IF;

    IF v_reserve_qty > 0 THEN
        UPDATE processed.stock_balance
           SET reserved_qty    = reserved_qty + v_reserve_qty,
               last_updated_at = now()
         WHERE tenant_id    = NEW.tenant_id
           AND product_id   = NEW.product_id
           AND warehouse_id = NEW.warehouse_id
           AND subinventory = NEW.subinventory
           AND stock_status = NEW.stock_status
           AND lot_id       = 0;

        IF NOT FOUND THEN
            INSERT INTO processed.stock_balance (
                tenant_id, tenant_code, product_id, product_code,
                warehouse_id, warehouse_code, subinventory, stock_status,
                lot_id, lot_code, on_hand_qty, reserved_qty,
                uom_id, uom_code, last_updated_at
            )
            VALUES (
                NEW.tenant_id, NEW.tenant_code, NEW.product_id, NEW.product_code,
                NEW.warehouse_id, NEW.warehouse_code, NEW.subinventory, NEW.stock_status,
                0, NULL, 0, v_reserve_qty,
                NEW.uom_id, NEW.uom_code, now()
            )
            ON CONFLICT (tenant_id, product_id, warehouse_id, subinventory, stock_status, lot_id)
            DO UPDATE SET
                reserved_qty    = processed.stock_balance.reserved_qty + EXCLUDED.reserved_qty,
                last_updated_at = now();
        END IF;
    END IF;
    RETURN COALESCE(NEW, OLD);
END;
$f$;

CREATE OR REPLACE FUNCTION processed.notify_outbox(
    p_tenant_id UUID, p_severity TEXT, p_source TEXT,
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
-- DRAIN: load_stocklevel — file-based opening balance promotion
-- v4: DELETE existing opening_balance rows for this file's batch, then INSERT
--     fresh. No ON CONFLICT. Re-running the same file replays cleanly.
-- ============================================================================
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
    v_tenant_id  UUID;
BEGIN
    IF p_file_name IS NULL OR length(p_file_name) = 0 THEN
        RAISE EXCEPTION 'load_stocklevel: file_name is required';
    END IF;

    -- Replay-friendly: if a batch exists for this file, blow away its opening_balance
    -- rows before re-inserting. The trigger will rebuild stock_balance from the new
    -- rows; recalculate_stock_balance is the manual recovery path if needed.
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

    -- Auto-create masters as needed.
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

    -- Insert the promoted rows.
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

-- ============================================================================
-- DRAIN: load_transactions — append-only inbox, dedupe by external_txn_id
-- v4: pick latest pending row per (tenant_code, external_txn_id). Older pending
--     rows for the same key get marked 'superseded'. processed.inv_transaction
--     enforces UNIQUE so duplicate txn_id from FOP retry is silently absorbed.
-- ============================================================================
CREATE OR REPLACE FUNCTION load_transactions(p_limit INTEGER DEFAULT 500)
RETURNS TABLE (rows_processed INTEGER, rows_failed INTEGER, rows_superseded INTEGER)
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = processed, staging, public, pg_catalog
AS $f$
DECLARE
    v_row     RECORD;
    v_tid     UUID;
    v_pid     BIGINT;
    v_wid     BIGINT;
    v_uid     BIGINT;
    v_ok      INTEGER := 0;
    v_bad     INTEGER := 0;
    v_sup     INTEGER := 0;
    v_payload JSONB;
BEGIN
    -- Mark older pending rows as superseded (when same key has multiple rows).
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

    -- Drain the latest pending row per key.
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

            -- Idempotent INSERT — if (tenant_id, external_txn_id) already exists in
            -- processed.inv_transaction (FOP retried after we processed it), the
            -- WHERE NOT EXISTS guard silently absorbs the duplicate. No UPSERT.
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

-- ============================================================================
-- DRAIN: load_orders — append-only inbox, latest-wins, DELETE-then-INSERT
-- v4: pick DISTINCT ON (tenant_code, sfdc_order_id) ORDER BY received_at DESC.
--     Then DELETE existing sfdc_order (cascades to lines + reservation release
--     via trigger 3) and INSERT fresh. Older pending rows for the same key
--     marked 'superseded'. No ON CONFLICT anywhere.
-- ============================================================================
CREATE OR REPLACE FUNCTION load_orders(p_limit INTEGER DEFAULT 500)
RETURNS TABLE (rows_processed INTEGER, rows_failed INTEGER, rows_superseded INTEGER)
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = processed, staging, public, pg_catalog
AS $f$
DECLARE
    v_row      RECORD;
    v_tid      UUID;
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
    -- Mark older pending rows for the same key as superseded.
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

    -- Drain the latest pending row per key.
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

            -- v4: clean state replacement. Cascade DELETE of order_line fires
            -- trigger 3 which releases the previous reservation on whichever
            -- subinv it was held in.
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

                -- Insert the order line. Trigger 3 fires AFTER INSERT and reserves
                -- on the specific (subinv, stock_status) row.
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

-- ============================================================================
-- READ: fetch_inventory — simplified, no POOL special case
-- Each (subinventory, stock_status) row reports its own on_hand and reserved.
-- ============================================================================
CREATE OR REPLACE FUNCTION fetch_inventory(
    p_tenant_code    TEXT,
    p_product_code   TEXT DEFAULT NULL,
    p_warehouse_code TEXT DEFAULT NULL,
    p_subinventory   TEXT DEFAULT NULL,
    p_stock_status   TEXT DEFAULT 'LIBERATED'
)
RETURNS TABLE (
    tenant_code      TEXT,
    product_code     TEXT,
    warehouse_code   TEXT,
    subinventory     TEXT,
    stock_status     TEXT,
    on_hand_qty      NUMERIC,
    reserved_qty     NUMERIC,
    atp              NUMERIC,
    uom_code         TEXT,
    last_updated_at  TIMESTAMPTZ
)
LANGUAGE sql
SECURITY DEFINER
SET search_path = processed, public, pg_catalog
AS $f$
    SELECT sb.tenant_code, sb.product_code, sb.warehouse_code,
           CASE WHEN p_subinventory IS NULL THEN 'ALL' ELSE sb.subinventory END AS subinventory,
           sb.stock_status,
           SUM(sb.on_hand_qty)                          AS on_hand_qty,
           SUM(sb.reserved_qty)                         AS reserved_qty,
           SUM(sb.on_hand_qty) - SUM(sb.reserved_qty)   AS atp,
           MAX(sb.uom_code)                             AS uom_code,
           MAX(sb.last_updated_at)                      AS last_updated_at
      FROM processed.stock_balance sb
     WHERE sb.tenant_code = p_tenant_code
       AND (p_product_code   IS NULL OR sb.product_code   = p_product_code)
       AND (p_warehouse_code IS NULL OR sb.warehouse_code = p_warehouse_code)
       AND (p_subinventory   IS NULL OR sb.subinventory   = p_subinventory)
       AND (p_stock_status = 'ALL' OR sb.stock_status = p_stock_status)
     GROUP BY sb.tenant_code, sb.product_code, sb.warehouse_code,
              CASE WHEN p_subinventory IS NULL THEN 'ALL' ELSE sb.subinventory END,
              sb.stock_status;
$f$;

-- ============================================================================
-- MAINTENANCE: recalculate_stock_balance — rebuild with per-subinv reservations
-- v4: reservations land on the specific (subinv, stock_status) row.
--     No POOL rows are generated.
-- ============================================================================
CREATE OR REPLACE FUNCTION recalculate_stock_balance(p_tenant_code TEXT)
RETURNS TABLE (rows_rebuilt INTEGER)
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = processed, public, pg_catalog
AS $f$
DECLARE
    v_tid UUID;
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
    -- v4: reservations are now per-(subinv, stock_status) — no POOL.
    reservations AS (
        SELECT sol.tenant_id, sol.product_id, sol.warehouse_id,
               sol.subinventory, sol.stock_status,
               SUM(sol.qty) AS sum_reserved
          FROM processed.sfdc_order_line sol
         WHERE sol.tenant_id = v_tid AND sol.line_state IN ('open','synced')
         GROUP BY sol.tenant_id, sol.product_id, sol.warehouse_id,
                  sol.subinventory, sol.stock_status
    ),
    -- Real stock_balance rows: one per (subinv, stock_status, lot)
    -- with both on_hand_qty and reserved_qty populated.
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

-- ============================================================================
CREATE TRIGGER trg_opening_balance_stock_apply
    AFTER INSERT ON processed.opening_balance
    FOR EACH ROW EXECUTE FUNCTION processed.f_stock_balance_opening_apply();

CREATE TRIGGER trg_inv_transaction_stock_apply
    AFTER INSERT ON processed.inv_transaction
    FOR EACH ROW EXECUTE FUNCTION processed.f_stock_balance_txn_apply();

CREATE TRIGGER trg_sfdc_order_line_reservation
    AFTER INSERT OR UPDATE OR DELETE ON processed.sfdc_order_line
    FOR EACH ROW EXECUTE FUNCTION processed.f_stock_balance_reservation_apply();

-- ============================================================================
-- 8. DATA MIGRATION
-- ============================================================================
-- a) Remove v3 POOL rows from stock_balance. They held the warehouse-level
--    reservation total. After this delete, all reservation rows for that
--    (tenant, product, warehouse) are gone; we rebuild them in step (c)
--    using per-(subinv, stock_status) summing from sfdc_order_line.
DELETE FROM processed.stock_balance WHERE subinventory = 'POOL';

-- b) Any existing sfdc_order_line rows now have subinventory='' (the column
--    default we just added). If those orders were really for specific
--    subinventories, backfill is the operator's call. Most v3 customers had
--    no orders flowing through yet, so this is a non-issue.

-- c) Rebuild stock_balance per tenant. recalculate_stock_balance is now v4-
--    aware: reservations land on the specific (subinv, stock_status) row, not
--    POOL.
DO $do$
DECLARE r RECORD;
BEGIN
    FOR r IN SELECT tenant_code FROM processed.tenant LOOP
        PERFORM recalculate_stock_balance(r.tenant_code);
        RAISE NOTICE 'recalculated stock_balance for tenant %', r.tenant_code;
    END LOOP;
END
$do$;

-- d) Rescue reservations whose (subinv, stock_status) doesn't yet have a
--    stock_balance row. Happens to v3-era orders: ALTER added subinventory=''
--    as the default, but opening_balance has no '' subinv row, so recalculate's
--    LEFT JOIN from opening_balance silently dropped those reservations.
--    This step writes them back as a row with on_hand=0 + reserved=sum(qty).
--    Warehouse-level totals are preserved; per-subinv visibility now shows
--    the residual reservation against '' as a flag for ops to backfill.
INSERT INTO processed.stock_balance (
    tenant_id, tenant_code, product_id, product_code,
    warehouse_id, warehouse_code, subinventory, stock_status,
    lot_id, lot_code, on_hand_qty, reserved_qty,
    uom_id, uom_code, last_updated_at
)
SELECT sol.tenant_id, sol.tenant_code, sol.product_id, sol.product_code,
       sol.warehouse_id, sol.warehouse_code, sol.subinventory, sol.stock_status,
       0, NULL, 0, SUM(sol.qty),
       MAX(sol.uom_id), MAX(sol.uom_code), now()
  FROM processed.sfdc_order_line sol
 WHERE sol.line_state IN ('open','synced')
 GROUP BY sol.tenant_id, sol.tenant_code, sol.product_id, sol.product_code,
          sol.warehouse_id, sol.warehouse_code, sol.subinventory, sol.stock_status
ON CONFLICT (tenant_id, product_id, warehouse_id, subinventory, stock_status, lot_id)
DO UPDATE SET
    reserved_qty    = EXCLUDED.reserved_qty,
    last_updated_at = now();

COMMIT;

-- ============================================================================
-- VERIFY post-upgrade
-- ============================================================================
-- Expected outputs after this script (assuming you had v3 with no POOL rows):
--
--   SELECT count(*) FROM processed.stock_balance WHERE subinventory = 'POOL';
--   -- 0  (POOL pattern fully removed)
--
--   SELECT trigger_name FROM information_schema.triggers
--    WHERE trigger_schema = 'processed' ORDER BY trigger_name;
--   -- 3 triggers (one row per event for the sfdc_order_line one)
--
--   \d staging.txn_inbox  -- should NOT show UNIQUE on (tenant_code, external_txn_id)
--   \d staging.order_inbox -- should NOT show UNIQUE on (tenant_code, sfdc_order_id)
