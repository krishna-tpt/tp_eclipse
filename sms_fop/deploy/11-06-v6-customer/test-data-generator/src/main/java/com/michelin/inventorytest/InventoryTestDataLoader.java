package com.michelin.inventorytest;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test data generator for the Michelin Inventory Ledger.
 *
 * Reads a CSV of (tenant_code, product_code, warehouse_code, subinventory, quantity) rows
 * and for each row generates:
 *   1. an opening_balance row → processed.opening_balance (triggers fire on insert)
 *   2. an SFDC-shaped order   → staging.order_inbox  (auto-promote trigger fires)
 *   3. an Oracle-shaped shipment → staging.txn_inbox (auto-promote trigger fires)
 *
 * Order qty = quantity * --orders-ratio  (default 0.5)
 * Ship qty  = order_qty  * --ships-ratio (default 0.5)
 *
 * Result: a fully populated processed.stock_balance / sfdc_order / inv_transaction
 * that exercises both the reservation cascade and the partial-shipment cascade.
 */
public class InventoryTestDataLoader {

    record Row(String tenantCode, String productCode, String warehouseCode,
               String subinventory, BigDecimal qty) {}

    record CliArgs(
        String csvPath,
        String dbUrl,
        String dbUser,
        String dbPass,
        BigDecimal ordersRatio,
        BigDecimal shipsRatio,
        boolean clean,
        String batchTag,
        String tenantOverride
    ) {}

    public static void main(String[] args) throws Exception {
        if (args.length == 0) { usage(); System.exit(0); }
        CliArgs cli = parseArgs(args);
        System.out.println("=== Inventory Test Data Generator ===");
        System.out.println("CSV:          " + cli.csvPath);
        System.out.println("DB URL:       " + cli.dbUrl);
        System.out.println("Orders ratio: " + cli.ordersRatio);
        System.out.println("Ships ratio:  " + cli.shipsRatio);
        System.out.println("Batch tag:    " + cli.batchTag);
        System.out.println("Clean first:  " + cli.clean);
        System.out.println();

        List<Row> rows = readCsv(cli.csvPath, cli.tenantOverride);
        System.out.println("Read " + rows.size() + " rows from CSV");

        try (Connection conn = DriverManager.getConnection(cli.dbUrl, cli.dbUser, cli.dbPass)) {
            conn.setAutoCommit(false);

            if (cli.clean) {
                cleanPriorBatch(conn, cli.batchTag, rows);
                conn.commit();
            }

            ensureMasters(conn, rows);
            conn.commit();

            int ob = insertOpeningBalances(conn, rows, cli.batchTag);
            conn.commit();
            System.out.println("Inserted " + ob + " opening_balance rows");

            int orderCount = insertOrders(conn, rows, cli.batchTag, cli.ordersRatio);
            conn.commit();
            System.out.println("Inserted " + orderCount + " orders into staging.order_inbox");

            int txnCount = insertTxns(conn, rows, cli.batchTag, cli.ordersRatio, cli.shipsRatio);
            conn.commit();
            System.out.println("Inserted " + txnCount + " shipments into staging.txn_inbox");

            printSummary(conn, cli.batchTag);
        }
    }

    // ─── CSV ────────────────────────────────────────────────────────────────
    private static List<Row> readCsv(String path, String tenantOverride) throws Exception {
        List<Row> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String header = null;
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank() || line.startsWith("#")) continue;
                header = line;
                break;
            }
            if (header == null) throw new IllegalStateException("Empty CSV: " + path);
            Map<String,Integer> idx = headerIndex(header);
            requireColumn(idx, "tenant_code");
            requireColumn(idx, "product_code");
            requireColumn(idx, "warehouse_code");
            requireColumn(idx, "subinventory");
            requireColumn(idx, "quantity");

            int lineNo = 1;
            while ((line = br.readLine()) != null) {
                lineNo++;
                if (line.isBlank() || line.startsWith("#")) continue;
                String[] f = splitCsv(line);
                String tenant = tenantOverride != null ? tenantOverride : f[idx.get("tenant_code")].trim();
                out.add(new Row(
                    tenant,
                    f[idx.get("product_code")].trim(),
                    f[idx.get("warehouse_code")].trim(),
                    f[idx.get("subinventory")].trim(),
                    new BigDecimal(f[idx.get("quantity")].trim())
                ));
            }
        }
        return out;
    }

    private static Map<String,Integer> headerIndex(String header) {
        Map<String,Integer> out = new HashMap<>();
        String[] cols = splitCsv(header);
        for (int i = 0; i < cols.length; i++) out.put(cols[i].trim().toLowerCase(), i);
        return out;
    }

    private static void requireColumn(Map<String,Integer> idx, String name) {
        if (!idx.containsKey(name))
            throw new IllegalStateException("CSV missing required column: " + name);
    }

    private static String[] splitCsv(String line) {
        // Simple split — assumes no embedded commas in fields. For this tool that's fine.
        return line.split(",", -1);
    }

    // ─── Masters ────────────────────────────────────────────────────────────
    private static void ensureMasters(Connection conn, List<Row> rows) throws SQLException {
        // tenants: must already exist (we don't auto-create tenants)
        // products / warehouses / uom: auto-create per (tenant, code) if missing.
        String upsertProduct =
            "INSERT INTO processed.product (tenant_id, product_code, name) " +
            "SELECT t.tenant_id, ?, ? FROM processed.tenant t WHERE t.tenant_code = ? " +
            "ON CONFLICT (tenant_id, product_code) DO NOTHING";
        String upsertWarehouse =
            "INSERT INTO processed.warehouse (tenant_id, warehouse_code, name) " +
            "SELECT t.tenant_id, ?, ? FROM processed.tenant t WHERE t.tenant_code = ? " +
            "ON CONFLICT (tenant_id, warehouse_code) DO NOTHING";
        String upsertUom =
            "INSERT INTO processed.uom (tenant_id, uom_code, name) " +
            "SELECT t.tenant_id, 'EA', 'EA' FROM processed.tenant t WHERE t.tenant_code = ? " +
            "ON CONFLICT (tenant_id, uom_code) DO NOTHING";

        try (PreparedStatement p = conn.prepareStatement(upsertProduct);
             PreparedStatement w = conn.prepareStatement(upsertWarehouse);
             PreparedStatement u = conn.prepareStatement(upsertUom)) {
            for (Row r : rows) {
                p.setString(1, r.productCode);   p.setString(2, r.productCode);   p.setString(3, r.tenantCode); p.addBatch();
                w.setString(1, r.warehouseCode); w.setString(2, r.warehouseCode); w.setString(3, r.tenantCode); w.addBatch();
                u.setString(1, r.tenantCode); u.addBatch();
            }
            p.executeBatch();
            w.executeBatch();
            u.executeBatch();
        }
    }

    // ─── Opening balance ───────────────────────────────────────────────────
    // Uses WHERE NOT EXISTS so the insert is idempotent — re-runs won't fail
    // on the (tenant, product, warehouse, subinv, status, lot, date) unique key.
    private static int insertOpeningBalances(Connection conn, List<Row> rows, String batchTag) throws SQLException {
        String sql =
            "INSERT INTO processed.opening_balance " +
            "(tenant_id, tenant_code, product_id, product_code, " +
            " warehouse_id, warehouse_code, subinventory, stock_status, " +
            " qty, uom_id, uom_code, as_of_date, batch_id, source_file) " +
            "SELECT t.tenant_id, t.tenant_code, p.product_id, p.product_code, " +
            "       w.warehouse_id, w.warehouse_code, ?, 'LIBERATED', " +
            "       ?, u.uom_id, u.uom_code, CURRENT_DATE, 1, ? " +
            "  FROM processed.tenant t " +
            "  JOIN processed.product   p ON p.tenant_id = t.tenant_id AND p.product_code = ? " +
            "  JOIN processed.warehouse w ON w.tenant_id = t.tenant_id AND w.warehouse_code = ? " +
            "  JOIN processed.uom       u ON u.tenant_id = t.tenant_id AND u.uom_code = 'EA' " +
            " WHERE t.tenant_code = ? " +
            "   AND NOT EXISTS (" +
            "       SELECT 1 FROM processed.opening_balance ob " +
            "        WHERE ob.tenant_id    = t.tenant_id " +
            "          AND ob.product_id   = p.product_id " +
            "          AND ob.warehouse_id = w.warehouse_id " +
            "          AND ob.subinventory = ? " +
            "          AND ob.stock_status = 'LIBERATED' " +
            "          AND COALESCE(ob.lot_id, 0::bigint) = 0 " +
            "          AND ob.as_of_date   = CURRENT_DATE" +
            "   )";
        int count = 0, skipped = 0;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Row r : rows) {
                ps.setString(1, r.subinventory);
                ps.setBigDecimal(2, r.qty);
                ps.setString(3, batchTag);
                ps.setString(4, r.productCode);
                ps.setString(5, r.warehouseCode);
                ps.setString(6, r.tenantCode);
                ps.setString(7, r.subinventory);
                int n = ps.executeUpdate();
                count += n;
                if (n == 0) skipped++;
            }
        }
        if (skipped > 0) {
            System.out.println("  └─ " + skipped + " opening_balance row(s) skipped (already exist for today)");
        }
        return count;
    }

    // ─── Orders → staging.order_inbox ──────────────────────────────────────
    private static int insertOrders(Connection conn, List<Row> rows, String batchTag, BigDecimal ratio) throws SQLException {
        String sql =
            "INSERT INTO staging.order_inbox (tenant_code, sfdc_order_id, payload) " +
            "VALUES (?, ?, ?::jsonb)";
        AtomicInteger seq = new AtomicInteger();
        int count = 0;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Row r : rows) {
                BigDecimal orderQty = r.qty.multiply(ratio).setScale(0, RoundingMode.DOWN);
                if (orderQty.signum() <= 0) continue;
                String orderId = "SIM-" + batchTag + "-ORD-" + String.format("%04d", seq.incrementAndGet());
                String erpHeader = "ERP-HDR-" + orderId;
                String erpLine   = "ERP-LINE-" + orderId + "-1";
                String payload =
                    "{" +
                    jq("customer_id") + ":" + jq("SIM-CUST") + "," +
                    jq("order_state") + ":" + jq("open") + "," +
                    jq("erp_external_id") + ":" + jq(erpHeader) + "," +
                    jq("source") + ":" + jq("test-data-generator/" + batchTag) + "," +
                    jq("lines") + ":[" +
                        "{" +
                        jq("product_code")     + ":" + jq(r.productCode)   + "," +
                        jq("warehouse_code")   + ":" + jq(r.warehouseCode) + "," +
                        jq("subinventory")     + ":" + jq(r.subinventory)  + "," +
                        jq("erp_external_id")  + ":" + jq(erpLine)         + "," +
                        jq("qty")              + ":" + orderQty.toPlainString() + "," +
                        jq("uom_code")         + ":" + jq("EA")            + "," +
                        jq("line_state")       + ":" + jq("open")          +
                        "}" +
                    "]" +
                    "}";
                ps.setString(1, r.tenantCode);
                ps.setString(2, orderId);
                ps.setString(3, payload);
                count += ps.executeUpdate();
            }
        }
        return count;
    }

    // ─── Transactions → staging.txn_inbox ──────────────────────────────────
    private static int insertTxns(Connection conn, List<Row> rows, String batchTag,
                                  BigDecimal ordersRatio, BigDecimal shipsRatio) throws SQLException {
        String sql =
            "INSERT INTO staging.txn_inbox (tenant_code, external_txn_id, payload) " +
            "VALUES (?, ?, ?::jsonb)";
        AtomicInteger seq = new AtomicInteger();
        int count = 0;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Row r : rows) {
                BigDecimal orderQty = r.qty.multiply(ordersRatio).setScale(0, RoundingMode.DOWN);
                BigDecimal shipQty  = orderQty.multiply(shipsRatio).setScale(0, RoundingMode.DOWN);
                if (shipQty.signum() <= 0) continue;
                String txnId    = "SIM-" + batchTag + "-TXN-" + String.format("%04d", seq.incrementAndGet());
                // erp_line_id must match the corresponding order's erp_external_id
                String orderId  = "SIM-" + batchTag + "-ORD-" + String.format("%04d", seq.get());
                String erpHeader = "ERP-HDR-" + orderId;
                String erpLine   = "ERP-LINE-" + orderId + "-1";
                String payload =
                    "{" +
                    jq("product_code")    + ":" + jq(r.productCode)   + "," +
                    jq("warehouse_code")  + ":" + jq(r.warehouseCode) + "," +
                    jq("subinventory")    + ":" + jq(r.subinventory)  + "," +
                    jq("signed_qty")      + ":-" + shipQty.toPlainString() + "," +
                    jq("uom_code")        + ":" + jq("EA") + "," +
                    jq("txn_type")        + ":" + jq("shipment") + "," +
                    jq("erp_line_id")     + ":" + jq(erpLine) + "," +
                    jq("erp_header_id")   + ":" + jq(erpHeader) + "," +
                    jq("source")          + ":" + jq("test-data-generator/" + batchTag) +
                    "}";
                ps.setString(1, r.tenantCode);
                ps.setString(2, txnId);
                ps.setString(3, payload);
                count += ps.executeUpdate();
            }
        }
        return count;
    }

    private static String jq(String s) {
        // Tiny JSON-safe quote — handles backslash + quote + control chars.
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

    // ─── Cleanup ───────────────────────────────────────────────────────────
    // Wipes (a) anything tagged by this batch, and (b) opening_balance and
    // stock_balance for the (product, warehouse, subinv) keys this CSV will
    // touch. Result: products in the CSV start at zero, even if a prior run
    // (or another batch) had populated them.
    private static void cleanPriorBatch(Connection conn, String batchTag, List<Row> rows) throws SQLException {
        String prefix = "SIM-" + batchTag + "-";
        try (var s = conn.createStatement()) {
            s.executeUpdate("DELETE FROM staging.txn_inbox         WHERE external_txn_id LIKE '" + prefix + "TXN-%'");
            s.executeUpdate("DELETE FROM staging.order_inbox       WHERE sfdc_order_id   LIKE '" + prefix + "ORD-%'");
            s.executeUpdate("DELETE FROM processed.inv_transaction WHERE external_txn_id LIKE '" + prefix + "TXN-%'");
            s.executeUpdate("DELETE FROM processed.sfdc_order      WHERE sfdc_order_id   LIKE '" + prefix + "ORD-%'");
        }
        String wipeOb = "DELETE FROM processed.opening_balance ob " +
                        " USING processed.tenant t, processed.product p, processed.warehouse w " +
                        " WHERE ob.tenant_id = t.tenant_id AND t.tenant_code = ? " +
                        "   AND ob.product_id = p.product_id AND p.product_code = ? AND p.tenant_id = t.tenant_id " +
                        "   AND ob.warehouse_id = w.warehouse_id AND w.warehouse_code = ? AND w.tenant_id = t.tenant_id " +
                        "   AND ob.subinventory = ?";
        String wipeSb = "DELETE FROM processed.stock_balance sb " +
                        " USING processed.tenant t, processed.product p, processed.warehouse w " +
                        " WHERE sb.tenant_id = t.tenant_id AND t.tenant_code = ? " +
                        "   AND sb.product_id = p.product_id AND p.product_code = ? AND p.tenant_id = t.tenant_id " +
                        "   AND sb.warehouse_id = w.warehouse_id AND w.warehouse_code = ? AND w.tenant_id = t.tenant_id " +
                        "   AND sb.subinventory = ?";
        try (PreparedStatement pOb = conn.prepareStatement(wipeOb);
             PreparedStatement pSb = conn.prepareStatement(wipeSb)) {
            for (Row r : rows) {
                pOb.setString(1, r.tenantCode); pOb.setString(2, r.productCode);
                pOb.setString(3, r.warehouseCode); pOb.setString(4, r.subinventory); pOb.executeUpdate();
                pSb.setString(1, r.tenantCode); pSb.setString(2, r.productCode);
                pSb.setString(3, r.warehouseCode); pSb.setString(4, r.subinventory); pSb.executeUpdate();
            }
        }
        System.out.println("Cleaned prior batch '" + batchTag + "' + opening_balance/stock_balance for " + rows.size() + " (product,warehouse,subinv) keys");
    }

    // ─── Summary ───────────────────────────────────────────────────────────
    private static void printSummary(Connection conn, String batchTag) throws SQLException {
        System.out.println();
        System.out.println("=== POST-LOAD SUMMARY ===");
        try (var s = conn.createStatement()) {
            print(s, "staging.order_inbox rows for this batch",
                "SELECT COUNT(*) FROM staging.order_inbox WHERE sfdc_order_id LIKE 'SIM-" + batchTag + "-%'");
            print(s, "  └─ processed (auto-promoted)",
                "SELECT COUNT(*) FROM staging.order_inbox WHERE sfdc_order_id LIKE 'SIM-" + batchTag + "-%' AND status='processed'");
            print(s, "  └─ rejected",
                "SELECT COUNT(*) FROM staging.order_inbox WHERE sfdc_order_id LIKE 'SIM-" + batchTag + "-%' AND status='rejected'");

            print(s, "staging.txn_inbox rows for this batch",
                "SELECT COUNT(*) FROM staging.txn_inbox WHERE external_txn_id LIKE 'SIM-" + batchTag + "-%'");
            print(s, "  └─ processed (auto-promoted)",
                "SELECT COUNT(*) FROM staging.txn_inbox WHERE external_txn_id LIKE 'SIM-" + batchTag + "-%' AND status='processed'");
            print(s, "  └─ rejected",
                "SELECT COUNT(*) FROM staging.txn_inbox WHERE external_txn_id LIKE 'SIM-" + batchTag + "-%' AND status='rejected'");

            print(s, "processed.sfdc_order rows",
                "SELECT COUNT(*) FROM processed.sfdc_order WHERE sfdc_order_id LIKE 'SIM-" + batchTag + "-%'");
            print(s, "processed.sfdc_order_line rows",
                "SELECT COUNT(*) FROM processed.sfdc_order_line WHERE sfdc_order_id LIKE 'SIM-" + batchTag + "-%'");
            print(s, "  └─ closed lines",
                "SELECT COUNT(*) FROM processed.sfdc_order_line WHERE sfdc_order_id LIKE 'SIM-" + batchTag + "-%' AND line_state='closed'");
            print(s, "  └─ open lines",
                "SELECT COUNT(*) FROM processed.sfdc_order_line WHERE sfdc_order_id LIKE 'SIM-" + batchTag + "-%' AND line_state='open'");
            print(s, "processed.inv_transaction rows",
                "SELECT COUNT(*) FROM processed.inv_transaction WHERE external_txn_id LIKE 'SIM-" + batchTag + "-%'");
        }

        System.out.println();
        System.out.println("=== STOCK_BALANCE for products touched by this batch ===");
        String q =
            "SELECT product_code, warehouse_code, subinventory, " +
            "       on_hand_qty, reserved_qty, on_hand_qty - reserved_qty AS atp " +
            "  FROM processed.stock_balance " +
            " WHERE product_code IN ( " +
            "       SELECT DISTINCT product_code FROM processed.opening_balance " +
            "        WHERE source_file = ? ) " +
            " ORDER BY 1, 2, 3";
        try (PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setString(1, batchTag);
            try (var rs = ps.executeQuery()) {
                System.out.printf("%-15s %-15s %-15s %12s %12s %12s%n",
                    "product_code","warehouse_code","subinventory","on_hand_qty","reserved_qty","atp");
                while (rs.next()) {
                    System.out.printf("%-15s %-15s %-15s %12s %12s %12s%n",
                        rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getBigDecimal(4).toPlainString(),
                        rs.getBigDecimal(5).toPlainString(),
                        rs.getBigDecimal(6).toPlainString());
                }
            }
        }
    }

    private static void print(java.sql.Statement s, String label, String sql) throws SQLException {
        try (var rs = s.executeQuery(sql)) {
            if (rs.next()) System.out.printf("%-50s %d%n", label, rs.getLong(1));
        }
    }

    // ─── CLI ───────────────────────────────────────────────────────────────
    // Precedence: CLI flag > properties file (--config) > built-in default.
    private static CliArgs parseArgs(String[] args) throws Exception {
        Properties props = new Properties();
        // Look for --config first so CLI flags can override its values.
        for (int i = 0; i < args.length; i++) {
            if ("--config".equals(args[i]) && i + 1 < args.length) {
                try (FileInputStream fis = new FileInputStream(args[i + 1])) {
                    props.load(fis);
                    System.out.println("Loaded config: " + args[i + 1]);
                }
            }
        }
        String csv    = props.getProperty("csv.path");
        String url    = props.getProperty("db.url");
        String user   = props.getProperty("db.user");
        String pass   = props.getProperty("db.pass");
        String tag    = props.getProperty("batch.tag");
        String tenant = props.getProperty("tenant");
        BigDecimal ord = new BigDecimal(props.getProperty("orders.ratio", "0.5"));
        BigDecimal shp = new BigDecimal(props.getProperty("ships.ratio",  "0.5"));
        boolean clean  = Boolean.parseBoolean(props.getProperty("clean", "false"));

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--config"        -> i++;                 // already consumed above
                case "--csv"           -> csv  = args[++i];
                case "--db-url"        -> url  = args[++i];
                case "--db-user"       -> user = args[++i];
                case "--db-pass"       -> pass = args[++i];
                case "--orders-ratio"  -> ord  = new BigDecimal(args[++i]);
                case "--ships-ratio"   -> shp  = new BigDecimal(args[++i]);
                case "--batch-tag"     -> tag  = args[++i];
                case "--tenant"        -> tenant = args[++i];
                case "--clean"         -> clean = true;
                case "--help"          -> { usage(); System.exit(0); }
                default -> { System.err.println("Unknown arg: " + args[i]); usage(); System.exit(2); }
            }
        }
        if (csv == null || url == null || user == null || pass == null) {
            System.err.println("Missing required value(s). Need: csv.path, db.url, db.user, db.pass");
            System.err.println("Set via --config <file> or CLI flags --csv / --db-url / --db-user / --db-pass.");
            usage();
            System.exit(2);
        }
        if (tag == null) tag = "B" + Instant.now().toEpochMilli();
        return new CliArgs(csv, url, user, pass, ord, shp, clean, tag, tenant);
    }

    private static void usage() {
        System.err.println("""
            Usage:
              java -jar inventory-test-data-generator.jar \\
                   [--config <props>]             # optional: properties file with defaults
                   --csv <path>                   # required: parameters CSV
                   --db-url jdbc:postgresql://h/d # required
                   --db-user <user>               # required
                   --db-pass <pass>               # required
                   [--orders-ratio 0.5]           # fraction of qty reserved as orders
                   [--ships-ratio  0.5]           # fraction of order qty shipped
                   [--batch-tag <string>]         # default: B<epoch-millis>
                   [--tenant <code>]              # override tenant from CSV
                   [--clean]                      # delete prior batch with same tag first

            Properties file keys (any subset; CLI flags override):
              csv.path = ...
              db.url   = jdbc:postgresql://host:5432/db
              db.user  = ...
              db.pass  = ...
              orders.ratio = 0.5
              ships.ratio  = 0.5
              batch.tag    = MORNING_RUN
              tenant       = IFOPEUR
              clean        = false

            CSV header (required columns, order-free):
              tenant_code,product_code,warehouse_code,subinventory,quantity

            Each CSV row produces:
              1 opening_balance row + 1 staging.order_inbox + 1 staging.txn_inbox

            Default ratios produce: opening=qty, reserved=qty/2, shipped=qty/4.
            With 100/0.5/0.5 → on_hand=75, reserved=25, atp=50 (line partial-shipped).
            """);
    }
}
