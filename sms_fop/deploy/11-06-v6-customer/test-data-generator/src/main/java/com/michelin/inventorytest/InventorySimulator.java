package com.michelin.inventorytest;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Scenario-driven simulator for the Michelin Inventory Ledger v6.
 *
 * Each row in the scenarios CSV defines ONE test case. The engine:
 *   1. Auto-creates a unique product (PROD_&lt;tc_id&gt;) so each TC is isolated
 *   2. Loads opening_balance directly into processed.opening_balance
 *   3. Submits the order via staging.order_inbox (auto-promote trigger fires)
 *   4. Submits the shipment txn via staging.txn_inbox (auto-promote trigger fires)
 *   5. Optionally — second txn (returns/receipts/adjustments) at a different
 *      subinventory / warehouse / stock_status (subinv moves, warehouse moves,
 *      QC holds)
 *   6. Optionally — N concurrent shipments or orders on a thread pool
 *      (race-condition tests)
 *   7. Reads back stock_balance + sfdc_order_line state
 *   8. Compares against the expected values; emits PASS/FAIL
 *   9. Writes a CSV result report and prints a console summary
 *
 * Naming convention:
 *   Product  PROD_&lt;tc_id&gt;
 *   Order    SIM_&lt;batch&gt;_&lt;tc_id&gt;_ORD          (+ _C1, _C2... for concurrent)
 *   Txn      SIM_&lt;batch&gt;_&lt;tc_id&gt;_TXN[_TXN2|_T1..TN]
 *   ERP line ERP_LINE_&lt;tc_id&gt;[_L2 for second order line]
 */
public class InventorySimulator {

    record Scenario(
        String tcId,
        String name,
        String tenant,
        String warehouse,
        String subinv,
        BigDecimal openingQty,
        BigDecimal orderQty,
        BigDecimal shipQty,
        String lineState,
        BigDecimal expOnHand,
        BigDecimal expReserved,
        BigDecimal expAtp,
        String expLineState,
        // ── Optional second txn: returns / receipts / adjustments ────────
        BigDecimal extraTxnQty,
        boolean extraTxnLink,
        String finalLineState,
        // ── Optional second-txn destination (for moves) ───────────────────
        String extraTxnSubinv,      // default = subinv
        String extraTxnWarehouse,   // default = warehouse
        String extraTxnStatus,      // default = "LIBERATED"
        BigDecimal expDestOnHand,   // null = don't check
        BigDecimal expDestReserved, // null = don't check
        // ── Optional second line on the same order ────────────────────────
        BigDecimal extraLineQty,
        // ── Optional concurrency ──────────────────────────────────────────
        int concurrentCount,
        BigDecimal concurrentQty,
        String concurrentOp,        // "ship" | "order"
        // ── Optional physical-count override (recount) ────────────────────
        // If non-zero, inserts a SECOND opening_balance row dated
        // CURRENT_DATE + 1, qty=recountQty. The apply trigger overwrites
        // stock_balance.on_hand to this value (does NOT add).
        BigDecimal recountQty,
        String notes
    ) {
        String productCode() { return "PROD_" + tcId; }
        String effectiveExtraSubinv()    { return extraTxnSubinv    == null || extraTxnSubinv.isBlank()    ? subinv    : extraTxnSubinv;    }
        String effectiveExtraWarehouse() { return extraTxnWarehouse == null || extraTxnWarehouse.isBlank() ? warehouse : extraTxnWarehouse; }
        String effectiveExtraStatus()    { return extraTxnStatus    == null || extraTxnStatus.isBlank()    ? "LIBERATED" : extraTxnStatus;  }
        boolean destDiffers() {
            return !effectiveExtraSubinv().equals(subinv)
                || !effectiveExtraWarehouse().equals(warehouse)
                || !effectiveExtraStatus().equals("LIBERATED");
        }
    }

    record Actual(
        BigDecimal onHand,
        BigDecimal reserved,
        BigDecimal atp,
        String lineState,
        boolean stockBalanceMissing,
        boolean orderLineMissing,
        BigDecimal destOnHand,
        BigDecimal destReserved,
        boolean destMissing,
        String inboxStatus,
        String rejectReason
    ) {}

    record Result(Scenario s, Actual a, boolean pass, String reason) {}

    record CliArgs(
        String scenariosPath, String dbUrl, String dbUser, String dbPass,
        boolean clean, String batchTag, String tenantOverride, String reportPath
    ) {}

    public static void main(String[] args) throws Exception {
        if (args.length == 0) { usage(); System.exit(0); }
        CliArgs cli = parseArgs(args);

        System.out.println("=== Inventory Simulator (scenario engine) ===");
        System.out.println("Scenarios:  " + cli.scenariosPath);
        System.out.println("DB URL:     " + cli.dbUrl);
        System.out.println("Batch tag:  " + cli.batchTag);
        System.out.println("Clean:      " + cli.clean);
        System.out.println("Report:     " + (cli.reportPath != null ? cli.reportPath : "<none>"));
        System.out.println();

        List<Scenario> scenarios = readScenarios(cli.scenariosPath, cli.tenantOverride);
        System.out.println("Loaded " + scenarios.size() + " scenarios");

        List<Result> results = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(cli.dbUrl, cli.dbUser, cli.dbPass)) {
            conn.setAutoCommit(false);

            if (cli.clean) {
                cleanScenarios(conn, scenarios, cli.batchTag);
                conn.commit();
                System.out.println("Cleaned prior state for " + scenarios.size() + " scenarios");
            }

            ensureMasters(conn, scenarios);
            conn.commit();

            for (Scenario s : scenarios) {
                try {
                    runOne(conn, s, cli.batchTag, cli);
                    conn.commit();
                } catch (Exception e) {
                    try { conn.rollback(); } catch (Exception ignore) {}
                    System.err.println("  ! " + s.tcId + " failed to load: " + e.getMessage());
                }
                Actual a = readActual(conn, s, cli.batchTag);
                Result r = evaluate(s, a);
                results.add(r);
            }
        }

        printReport(results);
        if (cli.reportPath != null) writeCsvReport(results, cli.reportPath);

        long fails = results.stream().filter(r -> !r.pass).count();
        if (fails > 0) System.exit(1);
    }

    // ─── CSV ────────────────────────────────────────────────────────────────
    private static List<Scenario> readScenarios(String path, String tenantOverride) throws Exception {
        List<Scenario> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String header = null;
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank() || line.startsWith("#")) continue;
                header = line;
                break;
            }
            if (header == null) throw new IllegalStateException("Empty scenarios CSV: " + path);
            Map<String,Integer> idx = headerIndex(header);
            for (String col : List.of("tc_id","name","tenant_code","warehouse_code","subinventory",
                    "opening_qty","order_qty","ship_qty","line_state",
                    "expected_on_hand","expected_reserved","expected_atp","expected_line_st"))
                if (!idx.containsKey(col))
                    throw new IllegalStateException("scenarios CSV missing column: " + col);

            while ((line = br.readLine()) != null) {
                if (line.isBlank() || line.startsWith("#")) continue;
                String[] f = line.split(",", -1);
                String tenant = tenantOverride != null ? tenantOverride : f[idx.get("tenant_code")].trim();
                out.add(new Scenario(
                    f[idx.get("tc_id")].trim(),
                    f[idx.get("name")].trim(),
                    tenant,
                    f[idx.get("warehouse_code")].trim(),
                    f[idx.get("subinventory")],
                    new BigDecimal(f[idx.get("opening_qty")].trim()),
                    new BigDecimal(f[idx.get("order_qty")].trim()),
                    new BigDecimal(f[idx.get("ship_qty")].trim()),
                    f[idx.get("line_state")].trim(),
                    new BigDecimal(f[idx.get("expected_on_hand")].trim()),
                    new BigDecimal(f[idx.get("expected_reserved")].trim()),
                    new BigDecimal(f[idx.get("expected_atp")].trim()),
                    f[idx.get("expected_line_st")].trim(),
                    optionalBd(f, idx, "extra_txn_qty", BigDecimal.ZERO),
                    optionalBool(f, idx, "extra_txn_link", false),
                    optionalStr(f, idx, "final_line_state", ""),
                    optionalStr(f, idx, "extra_txn_subinv", "").trim(),
                    optionalStr(f, idx, "extra_txn_warehouse", "").trim(),
                    optionalStr(f, idx, "extra_txn_status", "").trim(),
                    optionalBdOrNull(f, idx, "expected_dest_on_hand"),
                    optionalBdOrNull(f, idx, "expected_dest_reserved"),
                    optionalBd(f, idx, "extra_line_qty", BigDecimal.ZERO),
                    optionalInt(f, idx, "concurrent_count", 0),
                    optionalBd(f, idx, "concurrent_qty", BigDecimal.ZERO),
                    optionalStr(f, idx, "concurrent_op", "ship").trim(),
                    optionalBd(f, idx, "recount_qty", BigDecimal.ZERO),
                    optionalStr(f, idx, "notes", "")
                ));
            }
        }
        return out;
    }

    private static Map<String,Integer> headerIndex(String header) {
        Map<String,Integer> out = new HashMap<>();
        String[] cols = header.split(",", -1);
        for (int i = 0; i < cols.length; i++) out.put(cols[i].trim().toLowerCase(), i);
        return out;
    }

    private static BigDecimal optionalBd(String[] f, Map<String,Integer> idx, String col, BigDecimal dflt) {
        Integer i = idx.get(col);
        if (i == null || i >= f.length) return dflt;
        String v = f[i].trim();
        return v.isEmpty() ? dflt : new BigDecimal(v);
    }
    private static BigDecimal optionalBdOrNull(String[] f, Map<String,Integer> idx, String col) {
        Integer i = idx.get(col);
        if (i == null || i >= f.length) return null;
        String v = f[i].trim();
        return v.isEmpty() ? null : new BigDecimal(v);
    }
    private static int optionalInt(String[] f, Map<String,Integer> idx, String col, int dflt) {
        Integer i = idx.get(col);
        if (i == null || i >= f.length) return dflt;
        String v = f[i].trim();
        return v.isEmpty() ? dflt : Integer.parseInt(v);
    }
    private static boolean optionalBool(String[] f, Map<String,Integer> idx, String col, boolean dflt) {
        Integer i = idx.get(col);
        if (i == null || i >= f.length) return dflt;
        String v = f[i].trim().toLowerCase();
        if (v.isEmpty()) return dflt;
        return v.equals("true") || v.equals("yes") || v.equals("1");
    }
    private static String optionalStr(String[] f, Map<String,Integer> idx, String col, String dflt) {
        Integer i = idx.get(col);
        if (i == null || i >= f.length) return dflt;
        return f[i];
    }

    // ─── Masters ────────────────────────────────────────────────────────────
    private static void ensureMasters(Connection conn, List<Scenario> scenarios) throws SQLException {
        String upP =
            "INSERT INTO processed.product (tenant_id, product_code, name) " +
            "SELECT t.tenant_id, ?, ? FROM processed.tenant t WHERE t.tenant_code = ? " +
            "ON CONFLICT (tenant_id, product_code) DO NOTHING";
        String upW =
            "INSERT INTO processed.warehouse (tenant_id, warehouse_code, name) " +
            "SELECT t.tenant_id, ?, ? FROM processed.tenant t WHERE t.tenant_code = ? " +
            "ON CONFLICT (tenant_id, warehouse_code) DO NOTHING";
        String upU =
            "INSERT INTO processed.uom (tenant_id, uom_code, name) " +
            "SELECT t.tenant_id, 'EA', 'EA' FROM processed.tenant t WHERE t.tenant_code = ? " +
            "ON CONFLICT (tenant_id, uom_code) DO NOTHING";
        try (PreparedStatement p = conn.prepareStatement(upP);
             PreparedStatement w = conn.prepareStatement(upW);
             PreparedStatement u = conn.prepareStatement(upU)) {
            for (Scenario s : scenarios) {
                p.setString(1, s.productCode()); p.setString(2, s.productCode()); p.setString(3, s.tenant); p.addBatch();
                w.setString(1, s.warehouse);     w.setString(2, s.warehouse);     w.setString(3, s.tenant); w.addBatch();
                // also ensure destination warehouse exists if it differs (TC17 inter-warehouse move)
                if (!s.effectiveExtraWarehouse().equals(s.warehouse)) {
                    w.setString(1, s.effectiveExtraWarehouse()); w.setString(2, s.effectiveExtraWarehouse());
                    w.setString(3, s.tenant); w.addBatch();
                }
                u.setString(1, s.tenant); u.addBatch();
            }
            p.executeBatch(); w.executeBatch(); u.executeBatch();
        }
    }

    // ─── Per-scenario execution ─────────────────────────────────────────────
    private static void runOne(Connection conn, Scenario s, String batchTag, CliArgs cli) throws Exception {
        if (s.openingQty.signum() > 0) insertOpeningBalance(conn, s);
        if (s.orderQty.signum() > 0)   insertOrder(conn, s, batchTag);
        if (s.shipQty.signum() > 0)    insertTxn(conn, s, batchTag);
        if (s.extraTxnQty.signum() != 0) insertExtraTxn(conn, s, batchTag);
        if (s.finalLineState != null && !s.finalLineState.isBlank()) updateFinalLineState(conn, s);
        // Concurrency must happen on its OWN connections (the main conn is
        // mid-transaction). Commit current work first so concurrent threads
        // see the committed seed.
        if (s.concurrentCount > 0) {
            conn.commit();
            runConcurrency(s, batchTag, cli);
        }
        if (s.recountQty.signum() > 0) insertRecount(conn, s);
    }

    private static void insertRecount(Connection conn, Scenario s) throws SQLException {
        String sql =
            "INSERT INTO processed.opening_balance " +
            "(tenant_id, tenant_code, product_id, product_code, " +
            " warehouse_id, warehouse_code, subinventory, stock_status, " +
            " qty, uom_id, uom_code, as_of_date, batch_id, source_file) " +
            "SELECT t.tenant_id, t.tenant_code, p.product_id, p.product_code, " +
            "       w.warehouse_id, w.warehouse_code, ?, 'LIBERATED', " +
            "       ?, u.uom_id, u.uom_code, CURRENT_DATE + INTERVAL '1 day', 2, 'simulator-recount' " +
            "  FROM processed.tenant t " +
            "  JOIN processed.product   p ON p.tenant_id = t.tenant_id AND p.product_code = ? " +
            "  JOIN processed.warehouse w ON w.tenant_id = t.tenant_id AND w.warehouse_code = ? " +
            "  JOIN processed.uom       u ON u.tenant_id = t.tenant_id AND u.uom_code = 'EA' " +
            " WHERE t.tenant_code = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.subinv);
            ps.setBigDecimal(2, s.recountQty);
            ps.setString(3, s.productCode());
            ps.setString(4, s.warehouse);
            ps.setString(5, s.tenant);
            ps.executeUpdate();
        }
    }

    private static String inferExtraTxnType(BigDecimal signed, boolean linked) {
        boolean positive = signed.signum() > 0;
        if (linked && positive)  return "return";
        if (linked && !positive) return "shipment";
        if (positive)            return "receipt";
        return "adjustment_out";
    }

    private static void insertOpeningBalance(Connection conn, Scenario s) throws SQLException {
        String sql =
            "INSERT INTO processed.opening_balance " +
            "(tenant_id, tenant_code, product_id, product_code, " +
            " warehouse_id, warehouse_code, subinventory, stock_status, " +
            " qty, uom_id, uom_code, as_of_date, batch_id, source_file) " +
            "SELECT t.tenant_id, t.tenant_code, p.product_id, p.product_code, " +
            "       w.warehouse_id, w.warehouse_code, ?, 'LIBERATED', " +
            "       ?, u.uom_id, u.uom_code, CURRENT_DATE, 1, 'simulator' " +
            "  FROM processed.tenant t " +
            "  JOIN processed.product   p ON p.tenant_id = t.tenant_id AND p.product_code = ? " +
            "  JOIN processed.warehouse w ON w.tenant_id = t.tenant_id AND w.warehouse_code = ? " +
            "  JOIN processed.uom       u ON u.tenant_id = t.tenant_id AND u.uom_code = 'EA' " +
            " WHERE t.tenant_code = ? " +
            "   AND NOT EXISTS (" +
            "     SELECT 1 FROM processed.opening_balance ob " +
            "      WHERE ob.tenant_id = t.tenant_id " +
            "        AND ob.product_id = p.product_id " +
            "        AND ob.warehouse_id = w.warehouse_id " +
            "        AND ob.subinventory = ? " +
            "        AND ob.stock_status = 'LIBERATED' " +
            "        AND COALESCE(ob.lot_id, 0::bigint) = 0 " +
            "        AND ob.as_of_date = CURRENT_DATE)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.subinv);
            ps.setBigDecimal(2, s.openingQty);
            ps.setString(3, s.productCode());
            ps.setString(4, s.warehouse);
            ps.setString(5, s.tenant);
            ps.setString(6, s.subinv);
            ps.executeUpdate();
        }
    }

    private static String orderLinePayload(Scenario s, String erpLineSuffix, BigDecimal qty) {
        String erpLine = "ERP_LINE_" + s.tcId + erpLineSuffix;
        return "{" +
            jq("product_code")    + ":" + jq(s.productCode()) + "," +
            jq("warehouse_code")  + ":" + jq(s.warehouse)     + "," +
            jq("subinventory")    + ":" + jq(s.subinv)        + "," +
            jq("erp_external_id") + ":" + jq(erpLine)         + "," +
            jq("qty")             + ":" + qty.toPlainString() + "," +
            jq("uom_code")        + ":" + jq("EA")            + "," +
            jq("line_state")      + ":" + jq(s.lineState)     +
        "}";
    }

    private static void insertOrder(Connection conn, Scenario s, String batchTag) throws SQLException {
        String orderId = "SIM_" + batchTag + "_" + s.tcId + "_ORD";
        String erpHdr  = "ERP_HDR_"  + s.tcId;
        StringBuilder lines = new StringBuilder("[");
        lines.append(orderLinePayload(s, "", s.orderQty));
        if (s.extraLineQty.signum() > 0) {
            lines.append(",").append(orderLinePayload(s, "_L2", s.extraLineQty));
        }
        lines.append("]");
        String payload =
            "{" +
              jq("customer_id")     + ":" + jq("SIM_CUST") + "," +
              jq("order_state")     + ":" + jq("open") + "," +
              jq("erp_external_id") + ":" + jq(erpHdr) + "," +
              jq("source")          + ":" + jq("simulator/" + batchTag) + "," +
              jq("lines") + ":" + lines.toString() +
            "}";
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) " +
                "VALUES (?, ?, ?::jsonb)")) {
            ps.setString(1, s.tenant);
            ps.setString(2, orderId);
            ps.setString(3, payload);
            ps.executeUpdate();
        }
    }

    private static void insertTxn(Connection conn, Scenario s, String batchTag) throws SQLException {
        String txnId = "SIM_" + batchTag + "_" + s.tcId + "_TXN";
        String erpLine = "ERP_LINE_" + s.tcId;
        String erpHdr  = "ERP_HDR_"  + s.tcId;
        String payload =
            "{" +
              jq("product_code")    + ":" + jq(s.productCode()) + "," +
              jq("warehouse_code")  + ":" + jq(s.warehouse)     + "," +
              jq("subinventory")    + ":" + jq(s.subinv)        + "," +
              jq("signed_qty")      + ":-" + s.shipQty.toPlainString() + "," +
              jq("uom_code")        + ":" + jq("EA")            + "," +
              jq("txn_type")        + ":" + jq("shipment")      + "," +
              jq("erp_line_id")     + ":" + jq(erpLine)         + "," +
              jq("erp_header_id")   + ":" + jq(erpHdr)          + "," +
              jq("source")          + ":" + jq("simulator/" + batchTag) +
            "}";
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO staging.txn_inbox (tenant_code, external_txn_id, payload) " +
                "VALUES (?, ?, ?::jsonb)")) {
            ps.setString(1, s.tenant);
            ps.setString(2, txnId);
            ps.setString(3, payload);
            ps.executeUpdate();
        }
    }

    private static void insertExtraTxn(Connection conn, Scenario s, String batchTag) throws SQLException {
        String txnId   = "SIM_" + batchTag + "_" + s.tcId + "_TXN2";
        String erpLine = "ERP_LINE_" + s.tcId;
        String erpHdr  = "ERP_HDR_"  + s.tcId;
        String txnType = inferExtraTxnType(s.extraTxnQty, s.extraTxnLink);
        // Move-style txns (different subinv/warehouse/status) override txn_type label
        if (s.destDiffers()) txnType = "move_in";
        StringBuilder p = new StringBuilder("{");
        p.append(jq("product_code")).append(":").append(jq(s.productCode())).append(",");
        p.append(jq("warehouse_code")).append(":").append(jq(s.effectiveExtraWarehouse())).append(",");
        p.append(jq("subinventory")).append(":").append(jq(s.effectiveExtraSubinv())).append(",");
        p.append(jq("stock_status")).append(":").append(jq(s.effectiveExtraStatus())).append(",");
        p.append(jq("signed_qty")).append(":").append(s.extraTxnQty.toPlainString()).append(",");
        p.append(jq("uom_code")).append(":").append(jq("EA")).append(",");
        p.append(jq("txn_type")).append(":").append(jq(txnType));
        if (s.extraTxnLink) {
            p.append(",").append(jq("erp_line_id")).append(":").append(jq(erpLine));
            p.append(",").append(jq("erp_header_id")).append(":").append(jq(erpHdr));
        }
        p.append(",").append(jq("source")).append(":").append(jq("simulator/" + batchTag));
        p.append("}");
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO staging.txn_inbox (tenant_code, external_txn_id, payload) " +
                "VALUES (?, ?, ?::jsonb)")) {
            ps.setString(1, s.tenant);
            ps.setString(2, txnId);
            ps.setString(3, p.toString());
            ps.executeUpdate();
        }
    }

    private static void updateFinalLineState(Connection conn, Scenario s) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE processed.sfdc_order_line SET line_state = ? WHERE erp_external_id = ?")) {
            ps.setString(1, s.finalLineState.trim());
            ps.setString(2, "ERP_LINE_" + s.tcId);
            ps.executeUpdate();
        }
    }

    // ─── Concurrency: N threads, each its own JDBC connection ──────────────
    private static void runConcurrency(Scenario s, String batchTag, CliArgs cli) throws Exception {
        boolean isShip = "ship".equalsIgnoreCase(s.concurrentOp);
        ExecutorService pool = Executors.newFixedThreadPool(s.concurrentCount);
        AtomicInteger errors = new AtomicInteger();
        try {
            for (int i = 1; i <= s.concurrentCount; i++) {
                final int idx = i;
                pool.submit(() -> {
                    try (Connection c = DriverManager.getConnection(cli.dbUrl, cli.dbUser, cli.dbPass)) {
                        c.setAutoCommit(true);
                        if (isShip) insertConcurrentShipment(c, s, batchTag, idx);
                        else        insertConcurrentOrder   (c, s, batchTag, idx);
                    } catch (Exception e) {
                        errors.incrementAndGet();
                        System.err.println("  concurrent " + s.tcId + " thread " + idx + " failed: " + e.getMessage());
                    }
                });
            }
        } finally {
            pool.shutdown();
            pool.awaitTermination(60, TimeUnit.SECONDS);
        }
        if (errors.get() > 0) {
            System.err.println("  ! " + s.tcId + " had " + errors.get() + " concurrent thread errors");
        }
    }

    private static void insertConcurrentShipment(Connection c, Scenario s, String batchTag, int idx) throws SQLException {
        String txnId   = "SIM_" + batchTag + "_" + s.tcId + "_T" + idx;
        String erpLine = "ERP_LINE_" + s.tcId;
        String erpHdr  = "ERP_HDR_"  + s.tcId;
        String payload =
            "{" +
              jq("product_code")    + ":" + jq(s.productCode()) + "," +
              jq("warehouse_code")  + ":" + jq(s.warehouse)     + "," +
              jq("subinventory")    + ":" + jq(s.subinv)        + "," +
              jq("signed_qty")      + ":-" + s.concurrentQty.toPlainString() + "," +
              jq("uom_code")        + ":" + jq("EA")            + "," +
              jq("txn_type")        + ":" + jq("shipment")      + "," +
              jq("erp_line_id")     + ":" + jq(erpLine)         + "," +
              jq("erp_header_id")   + ":" + jq(erpHdr)          + "," +
              jq("source")          + ":" + jq("simulator-concurrent/" + batchTag) +
            "}";
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO staging.txn_inbox (tenant_code, external_txn_id, payload) " +
                "VALUES (?, ?, ?::jsonb)")) {
            ps.setString(1, s.tenant);
            ps.setString(2, txnId);
            ps.setString(3, payload);
            ps.executeUpdate();
        }
    }

    private static void insertConcurrentOrder(Connection c, Scenario s, String batchTag, int idx) throws SQLException {
        String orderId = "SIM_" + batchTag + "_" + s.tcId + "_C" + idx;
        String erpLine = "ERP_LINE_" + s.tcId + "_C" + idx;
        String erpHdr  = "ERP_HDR_"  + s.tcId  + "_C" + idx;
        String payload =
            "{" +
              jq("customer_id")     + ":" + jq("SIM_CUST_" + idx) + "," +
              jq("order_state")     + ":" + jq("open") + "," +
              jq("erp_external_id") + ":" + jq(erpHdr) + "," +
              jq("source")          + ":" + jq("simulator-concurrent/" + batchTag) + "," +
              jq("lines") + ":[{" +
                jq("product_code")    + ":" + jq(s.productCode()) + "," +
                jq("warehouse_code")  + ":" + jq(s.warehouse)     + "," +
                jq("subinventory")    + ":" + jq(s.subinv)        + "," +
                jq("erp_external_id") + ":" + jq(erpLine)         + "," +
                jq("qty")             + ":" + s.concurrentQty.toPlainString() + "," +
                jq("uom_code")        + ":" + jq("EA")            + "," +
                jq("line_state")      + ":" + jq("open")          +
              "}]" +
            "}";
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) " +
                "VALUES (?, ?, ?::jsonb)")) {
            ps.setString(1, s.tenant);
            ps.setString(2, orderId);
            ps.setString(3, payload);
            ps.executeUpdate();
        }
    }

    // ─── Read-back ──────────────────────────────────────────────────────────
    private static Actual readActual(Connection conn, Scenario s, String batchTag) throws SQLException {
        BigDecimal onHand = null, reserved = null;
        boolean sbMiss = true;
        String lineState = null;
        boolean lineMiss = true;
        BigDecimal destOnHand = null, destReserved = null;
        boolean destMiss = true;
        String inboxStatus = null, rejectReason = null;

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT on_hand_qty, reserved_qty FROM processed.stock_balance " +
                " WHERE tenant_code = ? AND product_code = ? AND warehouse_code = ? " +
                "   AND subinventory = ? AND stock_status = 'LIBERATED'")) {
            ps.setString(1, s.tenant); ps.setString(2, s.productCode());
            ps.setString(3, s.warehouse); ps.setString(4, s.subinv);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    sbMiss = false;
                    onHand = rs.getBigDecimal(1);
                    reserved = rs.getBigDecimal(2);
                }
            }
        }
        if (s.destDiffers()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT on_hand_qty, reserved_qty FROM processed.stock_balance " +
                    " WHERE tenant_code = ? AND product_code = ? AND warehouse_code = ? " +
                    "   AND subinventory = ? AND stock_status = ?")) {
                ps.setString(1, s.tenant); ps.setString(2, s.productCode());
                ps.setString(3, s.effectiveExtraWarehouse()); ps.setString(4, s.effectiveExtraSubinv());
                ps.setString(5, s.effectiveExtraStatus());
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        destMiss = false;
                        destOnHand = rs.getBigDecimal(1);
                        destReserved = rs.getBigDecimal(2);
                    }
                }
            }
        }
        if (s.orderQty.signum() > 0) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT line_state FROM processed.sfdc_order_line WHERE erp_external_id = ?")) {
                ps.setString(1, "ERP_LINE_" + s.tcId);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) { lineState = rs.getString(1); lineMiss = false; }
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT status, reject_reason FROM staging.order_inbox WHERE sfdc_order_id = ?")) {
                ps.setString(1, "SIM_" + batchTag + "_" + s.tcId + "_ORD");
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) { inboxStatus = rs.getString(1); rejectReason = rs.getString(2); }
                }
            }
        }
        BigDecimal atp = (onHand != null && reserved != null) ? onHand.subtract(reserved) : null;
        return new Actual(onHand, reserved, atp, lineState, sbMiss, lineMiss,
                          destOnHand, destReserved, destMiss, inboxStatus, rejectReason);
    }

    // ─── Compare ───────────────────────────────────────────────────────────
    private static Result evaluate(Scenario s, Actual a) {
        List<String> fails = new ArrayList<>();
        if (a.stockBalanceMissing) {
            fails.add("stock_balance row missing");
        } else {
            if (a.onHand.compareTo(s.expOnHand) != 0)
                fails.add("on_hand expected=" + s.expOnHand + " got=" + a.onHand.toPlainString());
            if (a.reserved.compareTo(s.expReserved) != 0)
                fails.add("reserved expected=" + s.expReserved + " got=" + a.reserved.toPlainString());
            if (a.atp.compareTo(s.expAtp) != 0)
                fails.add("atp expected=" + s.expAtp + " got=" + a.atp.toPlainString());
        }
        if (s.orderQty.signum() > 0) {
            if (a.orderLineMissing) {
                fails.add("sfdc_order_line row missing (inbox status=" + a.inboxStatus
                        + (a.rejectReason != null ? ", reason=" + a.rejectReason : "") + ")");
            } else if (!a.lineState.equals(s.expLineState)) {
                fails.add("line_state expected=" + s.expLineState + " got=" + a.lineState);
            }
        }
        if (s.destDiffers()) {
            if (s.expDestOnHand != null) {
                if (a.destMissing) {
                    fails.add("dest stock_balance row missing");
                } else if (a.destOnHand.compareTo(s.expDestOnHand) != 0) {
                    fails.add("dest on_hand expected=" + s.expDestOnHand + " got=" + a.destOnHand.toPlainString());
                }
            }
            if (s.expDestReserved != null && !a.destMissing) {
                if (a.destReserved.compareTo(s.expDestReserved) != 0) {
                    fails.add("dest reserved expected=" + s.expDestReserved + " got=" + a.destReserved.toPlainString());
                }
            }
        }
        return new Result(s, a, fails.isEmpty(), String.join("; ", fails));
    }

    // ─── Cleanup ────────────────────────────────────────────────────────────
    private static void cleanScenarios(Connection conn, List<Scenario> scenarios, String batchTag) throws SQLException {
        try (var st = conn.createStatement()) {
            for (Scenario s : scenarios) {
                String orderId = "SIM_" + batchTag + "_" + s.tcId + "_ORD";
                String txnId   = "SIM_" + batchTag + "_" + s.tcId + "_TXN";
                String txn2Id  = "SIM_" + batchTag + "_" + s.tcId + "_TXN2";
                String prefixT = "SIM_" + batchTag + "_" + s.tcId + "_T";
                String prefixC = "SIM_" + batchTag + "_" + s.tcId + "_C";
                st.executeUpdate("DELETE FROM staging.txn_inbox WHERE external_txn_id IN ('" + txnId + "','" + txn2Id + "') OR external_txn_id LIKE '" + prefixT + "%'");
                st.executeUpdate("DELETE FROM staging.order_inbox WHERE sfdc_order_id = '" + orderId + "' OR sfdc_order_id LIKE '" + prefixC + "%'");
                st.executeUpdate("DELETE FROM processed.inv_transaction WHERE external_txn_id IN ('" + txnId + "','" + txn2Id + "') OR external_txn_id LIKE '" + prefixT + "%'");
                st.executeUpdate("DELETE FROM processed.sfdc_order WHERE sfdc_order_id = '" + orderId + "' OR sfdc_order_id LIKE '" + prefixC + "%'");
            }
        }
        // Wipe opening_balance + stock_balance for the (tenant, product, *) keys of each TC.
        // Stock_balance wipe by product is broad enough to also hit the dest row.
        try (var st = conn.createStatement()) {
            for (Scenario s : scenarios) {
                st.executeUpdate("DELETE FROM processed.opening_balance ob USING processed.tenant t, processed.product p" +
                                 " WHERE ob.tenant_id = t.tenant_id AND t.tenant_code = '" + s.tenant + "'" +
                                 "   AND ob.product_id = p.product_id AND p.product_code = '" + s.productCode() + "'" +
                                 "   AND p.tenant_id = t.tenant_id");
                st.executeUpdate("DELETE FROM processed.stock_balance sb USING processed.tenant t, processed.product p" +
                                 " WHERE sb.tenant_id = t.tenant_id AND t.tenant_code = '" + s.tenant + "'" +
                                 "   AND sb.product_id = p.product_id AND p.product_code = '" + s.productCode() + "'" +
                                 "   AND p.tenant_id = t.tenant_id");
            }
        }
    }

    // ─── Report ────────────────────────────────────────────────────────────
    private static void printReport(List<Result> results) {
        System.out.println();
        System.out.println("=== RESULTS ===");
        System.out.printf("%-6s  %-40s  %-15s  %8s  %8s  %8s  %-9s  %-6s%n",
                "tc_id", "scenario", "product", "on_hand", "reserved", "atp", "line_st", "result");
        for (Result r : results) {
            String onHand   = r.a.onHand   == null ? "—" : r.a.onHand.toPlainString();
            String reserved = r.a.reserved == null ? "—" : r.a.reserved.toPlainString();
            String atp      = r.a.atp      == null ? "—" : r.a.atp.toPlainString();
            String lineSt   = r.a.lineState == null ? "—" : r.a.lineState;
            System.out.printf("%-6s  %-40s  %-15s  %8s  %8s  %8s  %-9s  %-6s%n",
                    r.s.tcId, truncate(r.s.name, 40), r.s.productCode(),
                    onHand, reserved, atp, lineSt, r.pass ? "PASS" : "FAIL");
            if (r.s.destDiffers() && !r.a.destMissing) {
                System.out.printf("        ↳ dest %s/%s/%s on=%s reserved=%s%n",
                        r.s.effectiveExtraWarehouse(), r.s.effectiveExtraSubinv(), r.s.effectiveExtraStatus(),
                        r.a.destOnHand, r.a.destReserved);
            }
            if (!r.pass) System.out.println("        ↳ " + r.reason);
        }
        long pass = results.stream().filter(r -> r.pass).count();
        long fail = results.size() - pass;
        System.out.println();
        System.out.println("Total: " + results.size() + " | Pass: " + pass + " | Fail: " + fail);
    }

    private static void writeCsvReport(List<Result> results, String path) throws Exception {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("tc_id,name,product_code,subinventory,opening_qty,order_qty,ship_qty," +
                       "expected_on_hand,actual_on_hand," +
                       "expected_reserved,actual_reserved," +
                       "expected_atp,actual_atp," +
                       "expected_line_state,actual_line_state," +
                       "dest_loc,expected_dest_on_hand,actual_dest_on_hand," +
                       "expected_dest_reserved,actual_dest_reserved," +
                       "result,fail_reason");
            for (Result r : results) {
                String destLoc = r.s.destDiffers()
                        ? (r.s.effectiveExtraWarehouse() + "/" + r.s.effectiveExtraSubinv() + "/" + r.s.effectiveExtraStatus())
                        : "";
                pw.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                        csv(r.s.tcId), csv(r.s.name), csv(r.s.productCode()), csv(r.s.subinv),
                        r.s.openingQty, r.s.orderQty, r.s.shipQty,
                        r.s.expOnHand,   bd(r.a.onHand),
                        r.s.expReserved, bd(r.a.reserved),
                        r.s.expAtp,      bd(r.a.atp),
                        csv(r.s.expLineState), csv(r.a.lineState),
                        csv(destLoc),
                        bd(r.s.expDestOnHand),   bd(r.a.destOnHand),
                        bd(r.s.expDestReserved), bd(r.a.destReserved),
                        r.pass ? "PASS" : "FAIL",
                        csv(r.reason));
            }
        }
        System.out.println("Wrote CSV report: " + path);
    }

    private static String bd(BigDecimal v) { return v == null ? "" : v.toPlainString(); }
    private static String csv(String s) {
        if (s == null) return "";
        if (s.indexOf(',') < 0 && s.indexOf('"') < 0 && s.indexOf('\n') < 0) return s;
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }
    private static String truncate(String s, int n) { return s.length() <= n ? s : s.substring(0, n-1) + "…"; }

    private static String jq(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        return sb.append("\"").toString();
    }

    // ─── CLI ───────────────────────────────────────────────────────────────
    private static CliArgs parseArgs(String[] args) throws Exception {
        Properties props = new Properties();
        for (int i = 0; i < args.length; i++) {
            if ("--config".equals(args[i]) && i + 1 < args.length) {
                try (FileInputStream fis = new FileInputStream(args[i+1])) {
                    props.load(fis);
                    System.out.println("Loaded config: " + args[i+1]);
                }
            }
        }
        String scenarios = props.getProperty("scenarios.path", props.getProperty("csv.path"));
        String url       = props.getProperty("db.url");
        String user      = props.getProperty("db.user");
        String pass      = props.getProperty("db.pass");
        String tag       = props.getProperty("batch.tag");
        String tenant    = props.getProperty("tenant");
        String report    = props.getProperty("report.path");
        boolean clean    = Boolean.parseBoolean(props.getProperty("clean", "false"));

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--config"     -> i++;
                case "--scenarios"  -> scenarios = args[++i];
                case "--csv"        -> scenarios = args[++i];
                case "--db-url"     -> url   = args[++i];
                case "--db-user"    -> user  = args[++i];
                case "--db-pass"    -> pass  = args[++i];
                case "--batch-tag"  -> tag   = args[++i];
                case "--tenant"     -> tenant= args[++i];
                case "--report"     -> report= args[++i];
                case "--clean"      -> clean = true;
                case "--help"       -> { usage(); System.exit(0); }
                default -> { System.err.println("Unknown arg: " + args[i]); usage(); System.exit(2); }
            }
        }
        if (scenarios == null || url == null || user == null || pass == null) {
            System.err.println("Missing required value(s). Need: scenarios.path, db.url, db.user, db.pass");
            usage();
            System.exit(2);
        }
        if (tag == null) tag = "S" + Instant.now().toEpochMilli();
        tag = tag.replaceAll("[^A-Za-z0-9_]", "_");
        return new CliArgs(scenarios, url, user, pass, clean, tag, tenant, report);
    }

    private static void usage() {
        System.err.println("""
            Inventory Simulator — runs a CSV of test scenarios end-to-end.

            Usage:
              java -cp inventory-test-data-generator.jar:lib/* \\
                   com.michelin.inventorytest.InventorySimulator \\
                   [--config <props>] \\
                   --scenarios <csv>             # required
                   --db-url jdbc:postgresql://h/d \\
                   --db-user <user> --db-pass <pass> \\
                   [--batch-tag <string>] \\
                   [--tenant <code>] \\
                   [--report <out.csv>] \\
                   [--clean]
            """);
    }
}
