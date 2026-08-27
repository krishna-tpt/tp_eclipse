-- V9__functions_security_definer.sql
-- Critical code-review finding revealed by adversarial TC-D02:
-- post_transaction / post_transaction_bulk / upsert_order have been
-- SECURITY INVOKER, meaning the calling role (inventoryledger_writer)
-- needs INSERT/UPDATE on the underlying tables. That role only has EXECUTE
-- on the functions — so any real production call from the FOP service or
-- the SFDC integration would have failed with "permission denied".
--
-- Earlier tests passed because they ran as postgres superuser.
--
-- Fix: declare these functions SECURITY DEFINER + SET search_path. Function
-- owner is postgres (superuser); EXECUTE on the function is the privilege
-- the caller needs. This is the canonical pattern for "writes through
-- functions only".
--
-- We also re-declare load_opening_balance the same way — the same logic
-- applies for the inventoryledger_app role.
--
-- IMPORTANT: SECURITY DEFINER requires SET search_path to prevent
-- search-path attacks (caller plants a malicious 'tenant' table in their
-- schema; without fixed search_path the function might find that instead).

ALTER FUNCTION post_transaction(jsonb)
    SECURITY DEFINER
    SET search_path = public, pg_catalog;

ALTER FUNCTION post_transaction_bulk(jsonb[])
    SECURITY DEFINER
    SET search_path = public, pg_catalog;

ALTER FUNCTION upsert_order(jsonb)
    SECURITY DEFINER
    SET search_path = public, pg_catalog;

ALTER FUNCTION load_opening_balance(bigint)
    SECURITY DEFINER
    SET search_path = public, pg_catalog, audit, staging;

-- Also lock down: revoke EXECUTE FROM PUBLIC on these functions, then grant
-- back to the specific roles. By default plpgsql functions are EXECUTE-able
-- by PUBLIC.
REVOKE EXECUTE ON FUNCTION post_transaction(jsonb)        FROM PUBLIC;
REVOKE EXECUTE ON FUNCTION post_transaction_bulk(jsonb[]) FROM PUBLIC;
REVOKE EXECUTE ON FUNCTION upsert_order(jsonb)            FROM PUBLIC;
REVOKE EXECUTE ON FUNCTION load_opening_balance(bigint)   FROM PUBLIC;

GRANT EXECUTE ON FUNCTION post_transaction(jsonb)        TO inventoryledger_writer;
GRANT EXECUTE ON FUNCTION post_transaction_bulk(jsonb[]) TO inventoryledger_writer;
GRANT EXECUTE ON FUNCTION upsert_order(jsonb)            TO inventoryledger_writer;
GRANT EXECUTE ON FUNCTION load_opening_balance(bigint)   TO inventoryledger_app;
