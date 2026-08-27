-- V15__load_stocklevel_function.sql
-- Promotes staging.stocklevel_inbox rows (populated by CatalogIngestPipeline)
-- into the live opening_balance table.
--
-- Called by the Java MicroService at the end of one ingest cycle, per file.
-- Idempotent: re-calling with the same file_name returns the existing batch
-- summary without double-inserting.
--
-- Auto-creates product, warehouse, uom masters per the "pure fact tables"
-- design (V13). Tenant is still provisioned out-of-band; an unknown tenant_code
-- in staging causes those rows to be counted as rejected (reason='unknown_tenant').

-- ----------------------------------------------------------------------------
-- 1. Tracking table — one row per file ingested
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS staging.stocklevel_batch (
    batch_id      BIGSERIAL PRIMARY KEY,
    file_name     TEXT        NOT NULL UNIQUE,
    started_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    completed_at  TIMESTAMPTZ,
    status        TEXT        NOT NULL DEFAULT 'pending',
    rows_accepted INTEGER     NOT NULL DEFAULT 0,
    rows_rejected INTEGER     NOT NULL DEFAULT 0
);

COMMENT ON TABLE staging.stocklevel_batch IS
  'One row per file ingested by CatalogIngestPipeline. Used by load_stocklevel for batch-level idempotency.';

-- ----------------------------------------------------------------------------
-- 2. Promoted-at marker on the inbox (per-row idempotency)
-- ----------------------------------------------------------------------------
ALTER TABLE staging.stocklevel_inbox
    ADD COLUMN IF NOT EXISTS promoted_at TIMESTAMPTZ;

CREATE INDEX IF NOT EXISTS ix_stocklevel_inbox_promotion
    ON staging.stocklevel_inbox (file_name) WHERE promoted_at IS NULL;

-- ----------------------------------------------------------------------------
-- 3. The promotion function
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION load_stocklevel(p_file_name TEXT)
RETURNS TABLE (
    batch_id      BIGINT,
    rows_accepted INTEGER,
    rows_rejected INTEGER,
    status        TEXT
)
LANGUAGE plpgsql
AS $f$
DECLARE
    v_batch_id      BIGINT;
    v_status        TEXT;
    v_accepted      INTEGER := 0;
    v_rejected      INTEGER := 0;
    v_master_row    RECORD;
    v_tenant_id     UUID;
BEGIN
    IF p_file_name IS NULL OR length(p_file_name) = 0 THEN
        RAISE EXCEPTION 'load_stocklevel: file_name is required';
    END IF;

    -- ------------------------------------------------------------------
    -- Find / create the batch row. If already 'loaded', short-circuit.
    -- ------------------------------------------------------------------
    SELECT b.batch_id, b.status
      INTO v_batch_id, v_status
      FROM staging.stocklevel_batch b
     WHERE b.file_name = p_file_name
       FOR UPDATE;

    IF v_batch_id IS NOT NULL AND v_status = 'loaded' THEN
        SELECT b.batch_id, b.rows_accepted, b.rows_rejected, b.status
          INTO v_batch_id, v_accepted, v_rejected, v_status
          FROM staging.stocklevel_batch b
         WHERE b.batch_id = v_batch_id;
        RETURN QUERY SELECT v_batch_id, v_accepted, v_rejected, v_status;
        RETURN;
    END IF;

    IF v_batch_id IS NULL THEN
        INSERT INTO staging.stocklevel_batch (file_name)
        VALUES (p_file_name)
        RETURNING staging.stocklevel_batch.batch_id INTO v_batch_id;
    END IF;

    -- ------------------------------------------------------------------
    -- Auto-create masters for distinct codes appearing in this file.
    -- Tenant is NOT auto-created — must be provisioned externally.
    -- ------------------------------------------------------------------
    FOR v_master_row IN
        SELECT DISTINCT
               s.tenant_code,
               s.item_segment1 AS product_code,
               s.warehouse     AS warehouse_code,
               s.primary_uom   AS uom_code
          FROM staging.stocklevel_inbox s
         WHERE s.file_name = p_file_name
           AND s.promoted_at IS NULL
           AND s.tenant_code IS NOT NULL
    LOOP
        SELECT t.tenant_id INTO v_tenant_id
          FROM tenant t
         WHERE t.tenant_code = v_master_row.tenant_code;
        IF v_tenant_id IS NULL THEN
            CONTINUE;  -- rows for unknown tenants fall through to reject below
        END IF;

        IF v_master_row.product_code IS NOT NULL THEN
            INSERT INTO product (tenant_id, product_code, name)
            VALUES (v_tenant_id, v_master_row.product_code, v_master_row.product_code)
            ON CONFLICT (tenant_id, product_code) DO NOTHING;
        END IF;
        IF v_master_row.warehouse_code IS NOT NULL THEN
            INSERT INTO warehouse (tenant_id, warehouse_code, name)
            VALUES (v_tenant_id, v_master_row.warehouse_code, v_master_row.warehouse_code)
            ON CONFLICT (tenant_id, warehouse_code) DO NOTHING;
        END IF;
        IF v_master_row.uom_code IS NOT NULL THEN
            INSERT INTO uom (tenant_id, uom_code, name)
            VALUES (v_tenant_id, v_master_row.uom_code, v_master_row.uom_code)
            ON CONFLICT (tenant_id, uom_code) DO NOTHING;
        END IF;
    END LOOP;

    -- ------------------------------------------------------------------
    -- Resolve codes → IDs, INSERT valid rows, count rejects.
    -- ------------------------------------------------------------------
    WITH src AS (
        SELECT
            s.inbox_id,
            s.tenant_code,
            s.item_segment1   AS product_code,
            s.warehouse       AS warehouse_code,
            s.primary_uom     AS uom_code,
            s.primary_quantity AS qty,
            s.snapshot_date,
            t.tenant_id,
            p.product_id,
            w.warehouse_id,
            u.uom_id,
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
          LEFT JOIN tenant    t ON t.tenant_code    = s.tenant_code
          LEFT JOIN product   p ON p.product_code   = s.item_segment1 AND p.tenant_id    = t.tenant_id
          LEFT JOIN warehouse w ON w.warehouse_code = s.warehouse     AND w.tenant_id    = t.tenant_id
          LEFT JOIN uom       u ON u.uom_code       = s.primary_uom   AND u.tenant_id    = t.tenant_id
         WHERE s.file_name = p_file_name
           AND s.promoted_at IS NULL
    ),
    inserted AS (
        INSERT INTO opening_balance (
            tenant_id,   tenant_code,
            product_id,  product_code,
            warehouse_id, warehouse_code,
            qty, uom_id, uom_code,
            as_of_date, batch_id
        )
        SELECT
            tenant_id,   tenant_code,
            product_id,  product_code,
            warehouse_id, warehouse_code,
            qty, uom_id, uom_code,
            snapshot_date, v_batch_id
          FROM src
         WHERE reject_reason IS NULL
        ON CONFLICT DO NOTHING
        RETURNING opening_balance_id
    ),
    counts AS (
        SELECT
            (SELECT COUNT(*)::int FROM inserted)               AS ok_count,
            (SELECT COUNT(*)::int FROM src WHERE reject_reason IS NOT NULL) AS bad_count
    )
    SELECT ok_count, bad_count INTO v_accepted, v_rejected FROM counts;

    -- Mark every inbox row touched in this run as promoted
    UPDATE staging.stocklevel_inbox
       SET promoted_at = now()
     WHERE file_name = p_file_name
       AND promoted_at IS NULL;

    -- Close the batch
    v_status := CASE
                    WHEN v_rejected = 0 THEN 'loaded'
                    WHEN v_accepted = 0 THEN 'failed'
                    ELSE 'partial'
                END;

    UPDATE staging.stocklevel_batch
       SET status        = v_status,
           completed_at  = now(),
           rows_accepted = v_accepted,
           rows_rejected = v_rejected
     WHERE staging.stocklevel_batch.batch_id = v_batch_id;

    RETURN QUERY SELECT v_batch_id, v_accepted, v_rejected, v_status;
END;
$f$;

COMMENT ON FUNCTION load_stocklevel(text) IS
  'Promotes staging.stocklevel_inbox rows for one file into opening_balance. '
  'Idempotent: re-calling with the same file_name returns the existing batch summary. '
  'Called by the Java MicroService at the end of each ingest cycle.';
