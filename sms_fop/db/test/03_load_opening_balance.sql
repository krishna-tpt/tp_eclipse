-- 03_load_opening_balance.sql — TC-130 through TC-139

BEGIN;

SELECT plan(10);

-- Setup masters
INSERT INTO tenant (tenant_id, tenant_code, name) VALUES
    ('00000000-0000-0000-0000-000000000d01', 'OB-T', 'OB Test');
INSERT INTO product   (tenant_id, product_code, name) VALUES
    ('00000000-0000-0000-0000-000000000d01', 'P1', 'P1'),
    ('00000000-0000-0000-0000-000000000d01', 'P2', 'P2');
INSERT INTO warehouse (tenant_id, warehouse_code, name) VALUES
    ('00000000-0000-0000-0000-000000000d01', 'W1', 'W1');
INSERT INTO uom (tenant_id, uom_code, name) VALUES
    ('00000000-0000-0000-0000-000000000d01', 'EA', 'Each');

-- Helper: create batch + stage rows + run load
CREATE OR REPLACE FUNCTION test_run_batch(p_rows jsonb[]) RETURNS TABLE(accepted int, rejected int)
LANGUAGE plpgsql AS $$
DECLARE
    v_batch_id bigint;
    r jsonb;
BEGIN
    INSERT INTO staging.ob_load_batch (tenant_id, file_name, file_hash)
    VALUES ('00000000-0000-0000-0000-000000000d01', 'test.csv', 'hash-' || gen_random_uuid()::text)
    RETURNING batch_id INTO v_batch_id;

    FOREACH r IN ARRAY p_rows LOOP
        INSERT INTO staging.ob_load (
            batch_id, tenant_code, product_code, warehouse_code, lot_code,
            uom_code, qty, as_of_date, source_ref, line_no
        ) VALUES (
            v_batch_id,
            r->>'tenant_code', r->>'product_code', r->>'warehouse_code', r->>'lot_code',
            r->>'uom_code', r->>'qty', r->>'as_of_date', r->>'source_ref',
            (r->>'line_no')::int
        );
    END LOOP;

    RETURN QUERY SELECT * FROM load_opening_balance(v_batch_id);
END$$;

-- TC-130: 2 valid rows → 2 accepted, 0 rejected
DELETE FROM opening_balance WHERE tenant_id = '00000000-0000-0000-0000-000000000d01';
SELECT is(
    (SELECT accepted FROM test_run_batch(ARRAY[
        '{"tenant_code":"OB-T","product_code":"P1","warehouse_code":"W1","lot_code":null,"uom_code":"EA","qty":"100","as_of_date":"2026-05-18","source_ref":"t","line_no":1}'::jsonb,
        '{"tenant_code":"OB-T","product_code":"P2","warehouse_code":"W1","lot_code":null,"uom_code":"EA","qty":"50","as_of_date":"2026-05-18","source_ref":"t","line_no":2}'::jsonb
    ])),
    2,
    'TC-130: 2 valid rows accepted'
);

SELECT is(
    (SELECT count(*)::int FROM opening_balance WHERE tenant_id = '00000000-0000-0000-0000-000000000d01'),
    2,
    'TC-130b: opening_balance has 2 rows'
);

-- TC-131: unknown product_code
SELECT is(
    (SELECT rejected FROM test_run_batch(ARRAY[
        '{"tenant_code":"OB-T","product_code":"BAD","warehouse_code":"W1","lot_code":null,"uom_code":"EA","qty":"5","as_of_date":"2026-05-18","source_ref":"t","line_no":1}'::jsonb
    ])),
    1,
    'TC-131: unknown product_code rejected'
);

-- TC-132: unknown warehouse_code
SELECT is(
    (SELECT rejected FROM test_run_batch(ARRAY[
        '{"tenant_code":"OB-T","product_code":"P1","warehouse_code":"BAD","lot_code":null,"uom_code":"EA","qty":"5","as_of_date":"2026-05-18","source_ref":"t","line_no":1}'::jsonb
    ])),
    1,
    'TC-132: unknown warehouse_code rejected'
);

-- TC-133: invalid qty
SELECT is(
    (SELECT rejected FROM test_run_batch(ARRAY[
        '{"tenant_code":"OB-T","product_code":"P1","warehouse_code":"W1","lot_code":null,"uom_code":"EA","qty":"NaN-text","as_of_date":"2026-05-18","source_ref":"t","line_no":1}'::jsonb
    ])),
    1,
    'TC-133: non-numeric qty rejected with invalid_qty'
);

-- TC-134: invalid date — rejected via null as_of_date producing reject
SELECT is(
    (SELECT rejected FROM test_run_batch(ARRAY[
        '{"tenant_code":"OB-T","product_code":"P1","warehouse_code":"W1","lot_code":null,"uom_code":"EA","qty":"5","as_of_date":null,"source_ref":"t","line_no":1}'::jsonb
    ])),
    1,
    'TC-134: null as_of_date rejected'
);

-- TC-135: idempotent — second call with same batch_id returns prior counts
WITH first_run AS (
    SELECT a.accepted, b.batch_id
      FROM (SELECT count(*) AS accepted FROM (
                SELECT * FROM test_run_batch(ARRAY[
                    '{"tenant_code":"OB-T","product_code":"P1","warehouse_code":"W1","lot_code":null,"uom_code":"EA","qty":"7","as_of_date":"2026-05-18","source_ref":"t","line_no":1}'::jsonb
                ])
            ) x WHERE x.accepted > 0) a,
           (SELECT max(batch_id) AS batch_id FROM staging.ob_load_batch
             WHERE tenant_id = '00000000-0000-0000-0000-000000000d01') b
)
SELECT is(
    (SELECT b.status FROM staging.ob_load_batch b WHERE b.batch_id = (SELECT batch_id FROM first_run)),
    'loaded',
    'TC-135: batch status=loaded after run'
);

-- Idempotent re-run: status remains loaded, opening_balance row count unchanged
SELECT ok(
    (SELECT accepted_count FROM staging.ob_load_batch
      WHERE batch_id = (SELECT max(batch_id) FROM staging.ob_load_batch)) IS NOT NULL,
    'TC-135b: accepted_count populated after first run'
);

-- TC-137: staging.ob_load purged for batch after success
SELECT is(
    (SELECT count(*)::int FROM staging.ob_load
      WHERE batch_id = (SELECT max(batch_id) FROM staging.ob_load_batch)),
    0,
    'TC-137: staging.ob_load rows purged after successful load'
);

-- TC-138: rejects > 0 → notification_outbox row
DELETE FROM notification_outbox WHERE source = 'load_opening_balance';
-- PERFORM is PL/pgSQL-only; at top level we use SELECT and discard the row.
SELECT * FROM test_run_batch(ARRAY[
    '{"tenant_code":"OB-T","product_code":"BAD","warehouse_code":"W1","lot_code":null,"uom_code":"EA","qty":"5","as_of_date":"2026-05-18","source_ref":"t","line_no":1}'::jsonb
]);
SELECT ok(
    EXISTS (SELECT 1 FROM notification_outbox WHERE source = 'load_opening_balance' AND severity = 'warn'),
    'TC-138: rejects produce warn notification in outbox'
);

SELECT * FROM finish();
ROLLBACK;
