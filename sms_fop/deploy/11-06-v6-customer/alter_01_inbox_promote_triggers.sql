-- ============================================================================
-- alter_01_inbox_promote_triggers.sql
-- ============================================================================
-- Closes a gap discovered in the customer reconciliation on 2026-06-11:
--
--   The customer DB has the v6 schema (erp_external_id, erp_line_id,
--   shipped_qty etc.) and the v6 promote procedures (staging.promote_one_txn,
--   staging.promote_one_order) — BUT the inbox triggers and their wrapper
--   functions are missing. Root cause: the DB was migrated v4 → v6 directly
--   without first applying v5's upgrade (which is where the inbox triggers
--   were originally created).
--
--   Consequence today: every INSERT into staging.txn_inbox /
--   staging.order_inbox sits in 'pending' status forever — the trigger
--   that should call staging.promote_one_*() never fires. Customer DB is
--   currently empty so no rows are stuck; this is fix-before-traffic.
--
-- Idempotent: CREATE OR REPLACE for functions; DROP TRIGGER IF EXISTS before
-- CREATE TRIGGER. Running it twice is a no-op.
-- ============================================================================

BEGIN;

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

DROP TRIGGER IF EXISTS trg_txn_inbox_promote ON staging.txn_inbox;
CREATE TRIGGER trg_txn_inbox_promote
    AFTER INSERT ON staging.txn_inbox
    FOR EACH ROW EXECUTE FUNCTION staging.f_promote_txn();

DROP TRIGGER IF EXISTS trg_order_inbox_promote ON staging.order_inbox;
CREATE TRIGGER trg_order_inbox_promote
    AFTER INSERT ON staging.order_inbox
    FOR EACH ROW EXECUTE FUNCTION staging.f_promote_order();

COMMIT;
