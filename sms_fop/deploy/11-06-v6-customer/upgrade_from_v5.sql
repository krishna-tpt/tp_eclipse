-- ============================================================================
-- Michelin Inventory Ledger — In-place upgrade v5 → v6
-- Tag: 11-06-v6-customer (2026-06-11)
-- ============================================================================
-- Use this when the customer DB is on v5 and has live data. For fresh
-- environments, prefer DROP SCHEMA + customer_install.sql.
--
-- v6 additions (all backward-compatible / additive):
--   * sfdc_order        + erp_external_id   (FOP/Oracle SO header ID)
--   * sfdc_order_line   + erp_external_id   (FOP/Oracle SO line ID)
--                       + shipped_qty       (partial-ship tracker)
--   * inv_transaction   + erp_line_id       (= Oracle SOURCE_LINE_ID)
--                       + erp_header_id     (= Oracle TRANSACTION_SOURCE_ID)
--   * f_stock_balance_txn_apply             rewritten — line-precise cascade
--                                            via erp_line_id; updates shipped_qty;
--                                            auto-closes line when fully shipped
--   * f_stock_balance_reservation_apply     rewritten — uses (qty - shipped_qty)
--                                            for reservation target; incremental
--                                            release per partial shipment
--   * promote_one_txn                       Oracle native: extracts SOURCE_LINE_ID
--                                            + TRANSACTION_SOURCE_ID; drops old
--                                            TRANSACTION_REF placeholder
--   * promote_one_order                     SFDC native: extracts erpExternalId__c
--                                            at header + line level
--   * load_transactions / load_orders       slim wrappers that delegate to
--                                            promote_one_* (single source of truth)
--
-- Idempotent — running twice is safe. Back up first:
--     pg_dump -Fc -f pre_v6.dump <dbname>
-- ============================================================================

BEGIN;

-- ============================================================================
-- 1. Schema additions
-- ============================================================================
ALTER TABLE processed.sfdc_order
    ADD COLUMN IF NOT EXISTS erp_external_id TEXT;

ALTER TABLE processed.sfdc_order_line
    ADD COLUMN IF NOT EXISTS erp_external_id TEXT;
ALTER TABLE processed.sfdc_order_line
    ADD COLUMN IF NOT EXISTS shipped_qty NUMERIC(18, 4) NOT NULL DEFAULT 0;

ALTER TABLE processed.inv_transaction
    ADD COLUMN IF NOT EXISTS erp_line_id TEXT;
ALTER TABLE processed.inv_transaction
    ADD COLUMN IF NOT EXISTS erp_header_id TEXT;

CREATE INDEX IF NOT EXISTS idx_sfdc_order_erp_external_id
    ON processed.sfdc_order (erp_external_id) WHERE erp_external_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_sfdc_order_line_erp_external_id
    ON processed.sfdc_order_line (erp_external_id) WHERE erp_external_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_inv_transaction_erp_line_link
    ON processed.inv_transaction (erp_line_id) WHERE erp_line_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_inv_transaction_erp_header_link
    ON processed.inv_transaction (erp_header_id) WHERE erp_header_id IS NOT NULL;

-- ============================================================================
-- 2. Stock-balance trigger functions — line-precise cascade + incremental release
-- ============================================================================
CREATE OR REPLACE FUNCTION processed.f_stock_balance_txn_apply()
RETURNS trigger LANGUAGE plpgsql AS $f$
DECLARE
    v_lot_key      BIGINT := COALESCE(NEW.lot_id, 0);
    v_subinv       TEXT   := COALESCE(NEW.subinventory, '');
    v_stock_status TEXT   := COALESCE(NEW.stock_status, 'LIBERATED');
    v_ship_qty     NUMERIC := ABS(NEW.signed_qty);
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

    IF NEW.signed_qty < 0 THEN
        IF NEW.erp_line_id IS NOT NULL THEN
            UPDATE processed.sfdc_order_line
               SET shipped_qty = shipped_qty + v_ship_qty,
                   line_state  = CASE WHEN shipped_qty + v_ship_qty >= qty THEN 'closed'
                                      ELSE line_state END,
                   updated_at  = now()
             WHERE erp_external_id = NEW.erp_line_id
               AND line_state IN ('open','synced');
        ELSIF NEW.sfdc_line_id IS NOT NULL THEN
            UPDATE processed.sfdc_order_line
               SET shipped_qty = shipped_qty + v_ship_qty,
                   line_state  = CASE WHEN shipped_qty + v_ship_qty >= qty THEN 'closed'
                                      ELSE line_state END,
                   updated_at  = now()
             WHERE sfdc_line_id = NEW.sfdc_line_id
               AND line_state IN ('open','synced');
        ELSIF NEW.sfdc_order_id IS NOT NULL THEN
            UPDATE processed.sfdc_order_line
               SET shipped_qty = shipped_qty + v_ship_qty,
                   line_state  = CASE WHEN shipped_qty + v_ship_qty >= qty THEN 'closed'
                                      ELSE line_state END,
                   updated_at  = now()
             WHERE sfdc_order_id = NEW.sfdc_order_id
               AND line_state IN ('open','synced');
        END IF;
    END IF;

    RETURN NEW;
END;
$f$;

CREATE OR REPLACE FUNCTION processed.f_stock_balance_reservation_apply()
RETURNS trigger LANGUAGE plpgsql AS $f$
DECLARE
    v_active_states TEXT[] := ARRAY['open','synced'];
    v_old_target    NUMERIC := 0;
    v_new_target    NUMERIC := 0;
    v_old_subinv    TEXT;
    v_old_status    TEXT;
    v_subinv_moved  BOOLEAN := false;
    v_release_qty   NUMERIC := 0;
    v_reserve_qty   NUMERIC := 0;
BEGIN
    IF TG_OP IN ('DELETE','UPDATE') THEN
        IF OLD.line_state = ANY(v_active_states) THEN
            v_old_target := GREATEST(OLD.qty - COALESCE(OLD.shipped_qty, 0), 0);
            v_old_subinv := OLD.subinventory;
            v_old_status := OLD.stock_status;
        END IF;
    END IF;

    IF TG_OP IN ('INSERT','UPDATE') THEN
        IF NEW.line_state = ANY(v_active_states) THEN
            v_new_target := GREATEST(NEW.qty - COALESCE(NEW.shipped_qty, 0), 0);
        END IF;
    END IF;

    IF TG_OP = 'UPDATE' THEN
        v_subinv_moved := (OLD.subinventory <> NEW.subinventory OR OLD.stock_status <> NEW.stock_status);
    END IF;

    IF v_subinv_moved THEN
        v_release_qty := v_old_target;
        v_reserve_qty := v_new_target;
    ELSE
        IF v_new_target > v_old_target THEN
            v_reserve_qty := v_new_target - v_old_target;
        ELSE
            v_release_qty := v_old_target - v_new_target;
        END IF;
        IF v_old_subinv IS NULL THEN v_old_subinv := NEW.subinventory; END IF;
        IF v_old_status IS NULL THEN v_old_status := NEW.stock_status; END IF;
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

-- ============================================================================
-- 3. Promote procedures — native extractors include erp_*
-- ============================================================================
CREATE OR REPLACE FUNCTION staging.promote_one_txn(p_inbox_id BIGINT)
RETURNS VOID
LANGUAGE plpgsql AS $f$
DECLARE
    v_row             staging.txn_inbox%ROWTYPE;
    v_payload         JSONB;
    v_tid             TEXT;
    v_pid             BIGINT;
    v_wid             BIGINT;
    v_uid             BIGINT;
    v_product_code    TEXT;
    v_warehouse_code  TEXT;
    v_uom_code        TEXT;
    v_subinventory    TEXT;
    v_stock_status    TEXT;
    v_signed_qty      NUMERIC;
    v_txn_type        TEXT;
    v_posted_at       TIMESTAMPTZ;
    v_sfdc_order_id   TEXT;
    v_sfdc_line_id    TEXT;
    v_erp_line_id     TEXT;
    v_erp_header_id   TEXT;
BEGIN
    SELECT * INTO v_row FROM staging.txn_inbox WHERE inbox_id = p_inbox_id;
    IF NOT FOUND THEN RETURN; END IF;
    v_payload := v_row.payload;

    IF v_payload ? 'TRANSACTION_TYPE' OR v_payload ? 'TRANSACTION_ID' THEN
        v_product_code   := v_payload->>'ITEM_CODE';
        v_warehouse_code := v_payload->>'ORGANIZATION_NAME';
        v_uom_code       := v_payload->>'PRIMARY_UOM_CODE';
        v_subinventory   := v_payload->>'SOURCE_SUBINVENTORY';
        v_stock_status   := 'LIBERATED';
        v_signed_qty     := (v_payload->>'PRIMARY_QTY')::numeric;
        v_txn_type       := COALESCE(v_payload->>'TRANSACTION_TYPE', 'unknown');
        v_posted_at      := COALESCE((v_payload->>'TRX_DATE_TIME')::timestamptz, now());
        v_erp_line_id    := (v_payload->>'SOURCE_LINE_ID')::text;
        v_erp_header_id  := (v_payload->>'TRANSACTION_SOURCE_ID')::text;
        v_sfdc_order_id  := v_payload->>'sfdc_order_id';
        v_sfdc_line_id   := v_payload->>'sfdc_line_id';
    ELSE
        v_product_code   := v_payload->>'product_code';
        v_warehouse_code := v_payload->>'warehouse_code';
        v_uom_code       := v_payload->>'uom_code';
        v_subinventory   := v_payload->>'subinventory';
        v_stock_status   := COALESCE(v_payload->>'stock_status', 'LIBERATED');
        v_signed_qty     := (v_payload->>'signed_qty')::numeric;
        v_txn_type       := COALESCE(v_payload->>'txn_type', 'unknown');
        v_posted_at      := COALESCE((v_payload->>'posted_at')::timestamptz, now());
        v_sfdc_order_id  := v_payload->>'sfdc_order_id';
        v_sfdc_line_id   := v_payload->>'sfdc_line_id';
        v_erp_line_id    := v_payload->>'erp_line_id';
        v_erp_header_id  := v_payload->>'erp_header_id';
    END IF;

    SELECT t.tenant_id INTO v_tid
      FROM processed.tenant t WHERE t.tenant_code = v_row.tenant_code;
    IF v_tid IS NULL THEN
        UPDATE staging.txn_inbox SET status='rejected', processed_at=now(),
               reject_reason='unknown_tenant'
         WHERE inbox_id = p_inbox_id;
        RETURN;
    END IF;

    SELECT p.product_id   INTO v_pid FROM processed.product   p WHERE p.tenant_id = v_tid AND p.product_code   = v_product_code;
    SELECT w.warehouse_id INTO v_wid FROM processed.warehouse w WHERE w.tenant_id = v_tid AND w.warehouse_code = v_warehouse_code;
    SELECT u.uom_id       INTO v_uid FROM processed.uom       u WHERE u.tenant_id = v_tid AND u.uom_code       = v_uom_code;

    IF v_pid IS NULL OR v_wid IS NULL OR v_uid IS NULL THEN
        UPDATE staging.txn_inbox SET status='rejected', processed_at=now(),
               reject_reason=CASE WHEN v_pid IS NULL THEN 'unknown_product:'   || COALESCE(v_product_code,   '<null>')
                                  WHEN v_wid IS NULL THEN 'unknown_warehouse:' || COALESCE(v_warehouse_code, '<null>')
                                  ELSE                    'unknown_uom:'       || COALESCE(v_uom_code,       '<null>') END
         WHERE inbox_id = p_inbox_id;
        RETURN;
    END IF;

    INSERT INTO processed.inv_transaction (
        tenant_id, tenant_code, external_txn_id,
        product_id, product_code, warehouse_id, warehouse_code,
        subinventory, stock_status,
        signed_qty, uom_id, uom_code, txn_type,
        posted_at, sfdc_order_id, sfdc_line_id,
        erp_line_id, erp_header_id, payload
    )
    SELECT v_tid, v_row.tenant_code, v_row.external_txn_id,
           v_pid, v_product_code, v_wid, v_warehouse_code,
           v_subinventory, v_stock_status,
           v_signed_qty, v_uid, v_uom_code, v_txn_type,
           v_posted_at, v_sfdc_order_id, v_sfdc_line_id,
           v_erp_line_id, v_erp_header_id, v_payload
     WHERE NOT EXISTS (
         SELECT 1 FROM processed.inv_transaction it
          WHERE it.tenant_id = v_tid AND it.external_txn_id = v_row.external_txn_id
     );

    UPDATE staging.txn_inbox SET status='processed', processed_at=now()
     WHERE inbox_id = p_inbox_id;
EXCEPTION WHEN OTHERS THEN
    UPDATE staging.txn_inbox SET status='rejected', processed_at=now(),
           reject_reason=SQLERRM
     WHERE inbox_id = p_inbox_id;
END;
$f$;

CREATE OR REPLACE FUNCTION staging.promote_one_order(p_inbox_id BIGINT)
RETURNS VOID
LANGUAGE plpgsql AS $f$
DECLARE
    v_row             staging.order_inbox%ROWTYPE;
    v_payload         JSONB;
    v_tid             TEXT;
    v_lines           JSONB;
    v_line            JSONB;
    v_line_no         INT := 0;
    v_pid             BIGINT;
    v_wid             BIGINT;
    v_uid             BIGINT;
    v_customer_id     TEXT;
    v_order_state     TEXT;
    v_order_erp_ext   TEXT;
    v_line_sfdc_id    TEXT;
    v_line_erp_ext    TEXT;
    v_line_product    TEXT;
    v_line_warehouse  TEXT;
    v_line_uom        TEXT;
    v_line_subinv     TEXT;
    v_line_status     TEXT;
    v_line_qty        NUMERIC;
    v_is_sfdc_native  BOOLEAN;
BEGIN
    SELECT * INTO v_row FROM staging.order_inbox WHERE inbox_id = p_inbox_id;
    IF NOT FOUND THEN RETURN; END IF;
    v_payload := v_row.payload;

    SELECT t.tenant_id INTO v_tid
      FROM processed.tenant t WHERE t.tenant_code = v_row.tenant_code;
    IF v_tid IS NULL THEN
        UPDATE staging.order_inbox SET status='rejected', processed_at=now(),
               reject_reason='unknown_tenant'
         WHERE inbox_id = p_inbox_id;
        RETURN;
    END IF;

    v_is_sfdc_native := v_payload ? 'sfRecordId__c' OR v_payload ? '_ObjectType';

    IF v_is_sfdc_native THEN
        v_customer_id   := COALESCE(v_payload->>'soldToAccount__c', 'unknown');
        v_order_state   := CASE COALESCE(v_payload->>'status__c', '')
                              WHEN 'Cancelled' THEN 'cancelled'
                              WHEN 'Closed'    THEN 'closed'
                              WHEN 'Completed' THEN 'closed'
                              ELSE 'open'
                           END;
        v_order_erp_ext := v_payload->>'erpExternalId__c';
        v_lines := COALESCE((v_payload->>'workOrderLineItems__c')::jsonb, '[]'::jsonb);
    ELSE
        v_customer_id   := COALESCE(v_payload->>'customer_id', 'unknown');
        v_order_state   := COALESCE(v_payload->>'order_state', 'open');
        v_order_erp_ext := v_payload->>'erp_external_id';
        v_lines         := COALESCE(v_payload->'lines', '[]'::jsonb);
    END IF;

    DELETE FROM processed.sfdc_order_line WHERE sfdc_order_id = v_row.sfdc_order_id;

    INSERT INTO processed.sfdc_order (
        sfdc_order_id, tenant_id, tenant_code, customer_id,
        erp_external_id, order_state, payload, received_at
    )
    VALUES (
        v_row.sfdc_order_id, v_tid, v_row.tenant_code,
        v_customer_id, v_order_erp_ext, v_order_state,
        v_payload, v_row.received_at
    )
    ON CONFLICT (sfdc_order_id) DO UPDATE
    SET tenant_id       = EXCLUDED.tenant_id,
        tenant_code     = EXCLUDED.tenant_code,
        customer_id     = EXCLUDED.customer_id,
        erp_external_id = EXCLUDED.erp_external_id,
        order_state     = EXCLUDED.order_state,
        payload         = EXCLUDED.payload,
        received_at     = EXCLUDED.received_at,
        updated_at      = now();

    FOR v_line IN SELECT * FROM jsonb_array_elements(v_lines)
    LOOP
        v_line_no := v_line_no + 1;

        IF v_line ? 'Product_Code__c' OR (v_line ? 'Id' AND v_line ? 'WorkOrderId') THEN
            v_line_sfdc_id   := v_line->>'Id';
            v_line_erp_ext   := v_line->>'erpExternalId__c';
            v_line_product   := v_line->>'Product_Code__c';
            v_line_warehouse := v_line->>'Location_External_Id__c';
            v_line_subinv    := v_line->>'Storage_Location__c';
            v_line_uom       := COALESCE(v_line->>'uom_code', 'EA');
            v_line_status    := CASE COALESCE(v_line->>'Status', '')
                                    WHEN 'Cancelled' THEN 'cancelled'
                                    WHEN 'Closed'    THEN 'closed'
                                    WHEN 'Completed' THEN 'closed'
                                    ELSE 'open'
                                END;
            v_line_qty       := (v_line->>'Quantity')::numeric;
        ELSE
            v_line_sfdc_id   := v_line->>'sfdc_line_id';
            v_line_erp_ext   := v_line->>'erp_external_id';
            v_line_product   := v_line->>'product_code';
            v_line_warehouse := v_line->>'warehouse_code';
            v_line_subinv    := COALESCE(v_line->>'subinventory', '');
            v_line_uom       := v_line->>'uom_code';
            v_line_status    := COALESCE(v_line->>'line_state', 'open');
            v_line_qty       := (v_line->>'qty')::numeric;
        END IF;

        SELECT p.product_id   INTO v_pid FROM processed.product   p WHERE p.tenant_id = v_tid AND p.product_code   = v_line_product;
        SELECT w.warehouse_id INTO v_wid FROM processed.warehouse w WHERE w.tenant_id = v_tid AND w.warehouse_code = v_line_warehouse;
        SELECT u.uom_id       INTO v_uid FROM processed.uom       u WHERE u.tenant_id = v_tid AND u.uom_code       = v_line_uom;

        IF v_pid IS NULL OR v_wid IS NULL OR v_uid IS NULL THEN
            RAISE EXCEPTION 'line %: %',
                v_line_no,
                CASE WHEN v_pid IS NULL THEN 'unknown_product:'   || COALESCE(v_line_product,   '<null>')
                     WHEN v_wid IS NULL THEN 'unknown_warehouse:' || COALESCE(v_line_warehouse, '<null>')
                     ELSE                    'unknown_uom:'       || COALESCE(v_line_uom,       '<null>') END;
        END IF;

        INSERT INTO processed.sfdc_order_line (
            sfdc_order_id, line_no, sfdc_line_id, erp_external_id,
            tenant_id, tenant_code,
            product_id, product_code, warehouse_id, warehouse_code,
            subinventory, stock_status,
            qty, uom_id, uom_code, line_state, payload
        )
        VALUES (
            v_row.sfdc_order_id, v_line_no, v_line_sfdc_id, v_line_erp_ext,
            v_tid, v_row.tenant_code,
            v_pid, v_line_product, v_wid, v_line_warehouse,
            COALESCE(v_line_subinv, ''),
            'LIBERATED',
            v_line_qty, v_uid, v_line_uom, v_line_status,
            v_line
        );
    END LOOP;

    UPDATE staging.order_inbox SET status='processed', processed_at=now()
     WHERE inbox_id = p_inbox_id;
EXCEPTION WHEN OTHERS THEN
    UPDATE staging.order_inbox SET status='rejected', processed_at=now(),
           reject_reason=SQLERRM
     WHERE inbox_id = p_inbox_id;
END;
$f$;

COMMIT;
