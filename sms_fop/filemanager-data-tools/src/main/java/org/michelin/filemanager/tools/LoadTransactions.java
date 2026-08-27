package org.michelin.filemanager.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.michelin.filemanager.tools.config.ToolsConfig;
import org.michelin.filemanager.tools.config.ToolsConfigLoader;
import org.michelin.filemanager.tools.util.Rng;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates synthetic transaction payloads and inserts them via post_transaction(jsonb).
 * Queries existing master data from the DB; skips if any master table is empty.
 */
public class LoadTransactions {

    private static final Logger log = LoggerFactory.getLogger(LoadTransactions.class);

    private final ToolsConfig cfg;

    public LoadTransactions(ToolsConfig cfg) {
        this.cfg = cfg;
    }

    public static void main(String[] args) {
        ToolsConfig cfg = new ToolsConfigLoader().load(System.getenv());
        int n = new LoadTransactions(cfg).run();
        log.info("inserted {} transactions", n);
    }

    public int run() {
        Rng rng = new Rng(cfg.seed() ^ 0x7A1E);  // sub-stream so it diverges from GenerateOpeningBalance

        try (Connection c = DriverManager.getConnection(
                cfg.db().url(), cfg.db().user(), cfg.db().password())) {

            List<MasterRef> masters = loadMasters(c);
            if (masters.isEmpty()) {
                throw new IllegalStateException("No master data found. Seed tenant/product/warehouse/uom first.");
            }

            int inserted = 0;
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT accepted FROM post_transaction(?::jsonb)")) {
                for (int i = 0; i < cfg.transactions().count(); i++) {
                    MasterRef m = rng.pick(masters);
                    boolean inbound = rng.nextDouble() < cfg.transactions().inboundRatio();
                    double qty = (1 + rng.nextInt(100)) * (inbound ? 1.0 : -1.0);
                    String payload = buildPayload(m, qty, inbound, "TOOLS-" + Instant.now().toEpochMilli() + "-" + i);
                    ps.setString(1, payload);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next() && rs.getBoolean(1)) {
                            inserted++;
                        }
                    }
                }
            }
            return inserted;

        } catch (SQLException e) {
            throw new RuntimeException("LoadTransactions failed: " + e.getMessage(), e);
        }
    }

    private List<MasterRef> loadMasters(Connection c) throws SQLException {
        List<MasterRef> out = new ArrayList<>();
        String sql =
            "SELECT t.tenant_code, p.product_code, w.warehouse_code, u.uom_code "
            + "  FROM tenant t "
            + "  JOIN product   p ON p.tenant_id = t.tenant_id "
            + "  JOIN warehouse w ON w.tenant_id = t.tenant_id "
            + "  JOIN uom       u ON u.tenant_id = t.tenant_id "
            + " WHERE t.is_active";
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new MasterRef(
                        rs.getString(1), rs.getString(2),
                        rs.getString(3), rs.getString(4)));
            }
        }
        return out;
    }

    private String buildPayload(MasterRef m, double signedQty, boolean inbound, String externalId) {
        return "{"
                + "\"tenant_code\":\""    + m.tenantCode    + "\","
                + "\"external_txn_id\":\"" + externalId      + "\","
                + "\"product_code\":\""   + m.productCode   + "\","
                + "\"warehouse_code\":\"" + m.warehouseCode + "\","
                + "\"uom_code\":\""       + m.uomCode       + "\","
                + "\"signed_qty\":"       + signedQty + ","
                + "\"txn_type\":\""       + (inbound ? "receipt" : "issue") + "\","
                + "\"source_system\":\"data_tools\","
                + "\"posted_at\":\""      + Instant.now()   + "\""
                + "}";
    }

    private record MasterRef(String tenantCode, String productCode, String warehouseCode, String uomCode) {}
}
