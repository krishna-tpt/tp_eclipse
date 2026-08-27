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
 * Generates synthetic SFDC orders and calls upsert_order(jsonb).
 * Line counts vary uniformly between orders.minLines and orders.maxLines (inclusive).
 */
public class LoadOrders {

    private static final Logger log = LoggerFactory.getLogger(LoadOrders.class);

    private final ToolsConfig cfg;

    public LoadOrders(ToolsConfig cfg) {
        this.cfg = cfg;
    }

    public static void main(String[] args) {
        ToolsConfig cfg = new ToolsConfigLoader().load(System.getenv());
        int n = new LoadOrders(cfg).run();
        log.info("inserted {} orders", n);
    }

    public int run() {
        Rng rng = new Rng(cfg.seed() ^ 0x0DBE);  // sub-stream so it diverges from LoadTransactions and GenerateOpeningBalance

        try (Connection c = DriverManager.getConnection(
                cfg.db().url(), cfg.db().user(), cfg.db().password())) {

            List<MasterRef> masters = loadMasters(c);
            if (masters.isEmpty()) {
                throw new IllegalStateException("No master data found. Seed tenant/product/warehouse/uom first.");
            }

            int orders = 0;
            try (PreparedStatement ps = c.prepareStatement("SELECT upsert_order(?::jsonb)")) {
                for (int i = 0; i < cfg.orders().count(); i++) {
                    int lineCount = rng.nextIntBetween(cfg.orders().minLines(), cfg.orders().maxLines());
                    String tenantCode = masters.get(0).tenantCode;  // single-tenant per order
                    String orderId = "TOOLS-ORD-" + Instant.now().toEpochMilli() + "-" + i;
                    String payload = buildOrderPayload(orderId, tenantCode, lineCount, rng, masters);
                    ps.setString(1, payload);
                    ps.execute();
                    orders++;
                }
            }
            return orders;

        } catch (SQLException e) {
            throw new RuntimeException("LoadOrders failed: " + e.getMessage(), e);
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

    private String buildOrderPayload(String orderId, String tenantCode, int lineCount,
                                     Rng rng, List<MasterRef> masters) {
        StringBuilder sb = new StringBuilder();
        sb.append("{")
          .append("\"tenant_code\":\"").append(tenantCode).append("\",")
          .append("\"sfdc_order_id\":\"").append(orderId).append("\",")
          .append("\"customer_id\":\"CUST-").append(rng.nextInt(10000)).append("\",")
          .append("\"order_state\":\"").append(cfg.orders().initialState()).append("\",")
          .append("\"lines\":[");
        for (int j = 0; j < lineCount; j++) {
            if (j > 0) sb.append(",");
            MasterRef m = rng.pick(masters);
            sb.append("{")
              .append("\"line_no\":").append(j + 1).append(",")
              .append("\"product_code\":\"").append(m.productCode).append("\",")
              .append("\"warehouse_code\":\"").append(m.warehouseCode).append("\",")
              .append("\"qty\":").append(1 + rng.nextInt(20)).append(",")
              .append("\"uom_code\":\"").append(m.uomCode).append("\",")
              .append("\"line_state\":\"open\"")
              .append("}");
        }
        sb.append("]}");
        return sb.toString();
    }

    private record MasterRef(String tenantCode, String productCode, String warehouseCode, String uomCode) {}
}
