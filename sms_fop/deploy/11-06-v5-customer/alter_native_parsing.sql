-- ============================================================================
-- Michelin Inventory Ledger — v5 refinement: native payload parsing
-- Tag: 11-06-v5-customer (applied 2026-06-11)
-- ============================================================================
-- Restructures the staging → processed promotion as:
--
--   1. Two NEW procedures that own the parse + map + load:
--        staging.promote_one_txn(p_inbox_id BIGINT)
--        staging.promote_one_order(p_inbox_id BIGINT)
--      They read one inbox row by id, detect Oracle EBS / SFDC native / normalized
--      shape, map fields, look up master IDs, and INSERT/UPSERT into processed.*.
--      Each procedure has its own BEGIN/EXCEPTION block — staging row always
--      ends with terminal status ('processed' or 'rejected') and non-NULL
--      processed_at, regardless of failure.
--
--   2. The trigger functions become THIN wrappers that just call the procedure:
--        staging.f_promote_txn()   → PERFORM staging.promote_one_txn(NEW.inbox_id)
--        staging.f_promote_order() → PERFORM staging.promote_one_order(NEW.inbox_id)
--
-- BENEFITS:
--   * Procedures are directly callable for backfill / retry / testing:
--       SELECT staging.promote_one_txn(123);
--   * Drain functions (load_transactions, load_orders) can call the same
--     procedures — single source of truth for parse + map logic
--   * Test harness can exercise parsing without going through INSERT triggers
--
-- SHAPE DETECTION:
--   * Oracle EBS native       — top-level keys like TRANSACTION_TYPE, ITEM_CODE
--   * SFDC Work Order native  — top-level keys like sfRecordId__c, _ObjectType,
--                               workOrderLineItems__c is stringified JSON
--   * Normalized snake_case   — lowercase keys (forward-compatible)
--
-- KNOWN GAPS (need Michelin clarification — flagged with TODO):
--   * Q2 — Oracle ↔ SFDC line link. Currently: TRANSACTION_REF → sfdc_order_id,
--     SOURCE_LINE_ID → sfdc_line_id. Real linkage TBD.
--   * SFDC Location_External_Id__c "40" vs warehouse_code "408_ES_COS_COS_WH_CO" —
--     may need a warehouse-code-mapping table.
--   * SFDC Storage_Location__c is an SFDC record ID, not a subinv string.
--   * SFDC line UOM not in payload; defaulted to 'EA'.
-- ============================================================================

BEGIN;

-- ============================================================================
-- staging.promote_one_txn — does the work for ONE staging.txn_inbox row.
-- Called by the trigger AND callable directly for backfill / retry.
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
    -- Extracted fields (populated from whichever shape was sent)
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
BEGIN
    SELECT * INTO v_row FROM staging.txn_inbox WHERE inbox_id = p_inbox_id;
    IF NOT FOUND THEN RETURN; END IF;
    v_payload := v_row.payload;

    -- ── Shape detection ────────────────────────────────────────────────────
    IF v_payload ? 'TRANSACTION_TYPE' OR v_payload ? 'TRANSACTION_ID' THEN
        -- Oracle EBS native
        v_product_code   := v_payload->>'ITEM_CODE';
        v_warehouse_code := v_payload->>'ORGANIZATION_NAME';
        v_uom_code       := v_payload->>'PRIMARY_UOM_CODE';
        v_subinventory   := v_payload->>'SOURCE_SUBINVENTORY';
        v_stock_status   := 'LIBERATED';
        v_signed_qty     := (v_payload->>'PRIMARY_QTY')::numeric;
        v_txn_type       := COALESCE(v_payload->>'TRANSACTION_TYPE', 'unknown');
        v_posted_at      := COALESCE((v_payload->>'TRX_DATE_TIME')::timestamptz, now());
        -- TODO Q2: confirm. TRANSACTION_REF looks like WMS doc ref; SOURCE_LINE_ID
        -- is Oracle SO line ID. Placeholder until Michelin clarifies SFDC mapping.
        v_sfdc_order_id  := v_payload->>'TRANSACTION_REF';
        v_sfdc_line_id   := (v_payload->>'SOURCE_LINE_ID')::text;
    ELSE
        -- Normalized snake_case
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
    END IF;

    -- ── Master lookup ─────────────────────────────────────────────────────
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

    -- ── Idempotent promote ─────────────────────────────────────────────────
    INSERT INTO processed.inv_transaction (
        tenant_id, tenant_code, external_txn_id,
        product_id, product_code, warehouse_id, warehouse_code,
        subinventory, stock_status,
        signed_qty, uom_id, uom_code, txn_type,
        posted_at, sfdc_order_id, sfdc_line_id, payload
    )
    SELECT v_tid, v_row.tenant_code, v_row.external_txn_id,
           v_pid, v_product_code, v_wid, v_warehouse_code,
           v_subinventory, v_stock_status,
           v_signed_qty, v_uid, v_uom_code, v_txn_type,
           v_posted_at, v_sfdc_order_id, v_sfdc_line_id, v_payload
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

-- ============================================================================
-- staging.promote_one_order — does the work for ONE staging.order_inbox row.
-- ============================================================================
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
    v_line_sfdc_id    TEXT;
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

    -- ── Shape detection: header ────────────────────────────────────────────
    v_is_sfdc_native := v_payload ? 'sfRecordId__c' OR v_payload ? '_ObjectType';

    IF v_is_sfdc_native THEN
        v_customer_id := COALESCE(v_payload->>'soldToAccount__c', 'unknown');
        v_order_state := CASE COALESCE(v_payload->>'status__c', '')
                            WHEN 'Cancelled' THEN 'cancelled'
                            WHEN 'Closed'    THEN 'closed'
                            WHEN 'Completed' THEN 'closed'
                            ELSE 'open'
                         END;
        -- workOrderLineItems__c is a STRINGIFIED JSON array — parse twice
        v_lines := COALESCE((v_payload->>'workOrderLineItems__c')::jsonb, '[]'::jsonb);
    ELSE
        v_customer_id := COALESCE(v_payload->>'customer_id', 'unknown');
        v_order_state := COALESCE(v_payload->>'order_state', 'open');
        v_lines       := COALESCE(v_payload->'lines', '[]'::jsonb);
    END IF;

    -- ── DELETE old lines + UPSERT header ──────────────────────────────────
    DELETE FROM processed.sfdc_order_line WHERE sfdc_order_id = v_row.sfdc_order_id;

    INSERT INTO processed.sfdc_order (
        sfdc_order_id, tenant_id, tenant_code, customer_id,
        order_state, payload, received_at
    )
    VALUES (
        v_row.sfdc_order_id, v_tid, v_row.tenant_code,
        v_customer_id, v_order_state,
        v_payload, v_row.received_at
    )
    ON CONFLICT (sfdc_order_id) DO UPDATE
    SET tenant_id   = EXCLUDED.tenant_id,
        tenant_code = EXCLUDED.tenant_code,
        customer_id = EXCLUDED.customer_id,
        order_state = EXCLUDED.order_state,
        payload     = EXCLUDED.payload,
        received_at = EXCLUDED.received_at,
        updated_at  = now();

    -- ── Per-line: shape detect + map + INSERT ─────────────────────────────
    FOR v_line IN SELECT * FROM jsonb_array_elements(v_lines)
    LOOP
        v_line_no := v_line_no + 1;

        IF v_line ? 'Product_Code__c' OR (v_line ? 'Id' AND v_line ? 'WorkOrderId') THEN
            -- SFDC native line
            v_line_sfdc_id   := v_line->>'Id';
            v_line_product   := v_line->>'Product_Code__c';
            -- TODO: Location_External_Id__c sample value is "40"; warehouse_codes
            -- look like "408_ES_COS_COS_WH_CO". May need a mapping table.
            v_line_warehouse := v_line->>'Location_External_Id__c';
            -- TODO: Storage_Location__c is an opaque SFDC record ID
            -- ("1319Z000001l1mYQAQ"). Resolve via mapping table when one exists.
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
            -- Normalized snake_case line
            v_line_sfdc_id   := v_line->>'sfdc_line_id';
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
            sfdc_order_id, line_no, sfdc_line_id,
            tenant_id, tenant_code,
            product_id, product_code, warehouse_id, warehouse_code,
            subinventory, stock_status,
            qty, uom_id, uom_code, line_state, payload
        )
        VALUES (
            v_row.sfdc_order_id, v_line_no, v_line_sfdc_id,
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

-- ============================================================================
-- THIN TRIGGER WRAPPERS: just call the procedures
-- ============================================================================
CREATE OR REPLACE FUNCTION staging.f_promote_txn()
RETURNS trigger LANGUAGE plpgsql AS $f$
BEGIN
    PERFORM staging.promote_one_txn(NEW.inbox_id);
    RETURN NEW;
END;
$f$;

CREATE OR REPLACE FUNCTION staging.f_promote_order()
RETURNS trigger LANGUAGE plpgsql AS $f$
BEGIN
    PERFORM staging.promote_one_order(NEW.inbox_id);
    RETURN NEW;
END;
$f$;

COMMIT;
