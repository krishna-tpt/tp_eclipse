package org.michelin.filemanager.tools.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.michelin.filemanager.config.Config;
import org.michelin.filemanager.db.FlywayMigrator;
import org.michelin.filemanager.tools.LoadTransactions;
import org.michelin.filemanager.tools.config.ToolsConfig;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

class LoadTransactionsIT {

    private static ToolsConfig.DbConfig db;
    private static String jdbcUrl;
    private static String user;
    private static String password;

    @BeforeAll
    static void setUp() {
        PostgreSQLContainer<?> pg = PgTestContainer.instance();
        jdbcUrl = pg.getJdbcUrl();
        user = pg.getUsername();
        password = pg.getPassword();
        db = new ToolsConfig.DbConfig(jdbcUrl, user, password);

        Config.DbConfig coreDb = new Config.DbConfig(jdbcUrl, user, password, 5, 30000, "tools-it");
        new FlywayMigrator(coreDb, new Config.FlywayConfig(true, "classpath:db/migration", false, true)).migrate();
    }

    @BeforeEach
    void seedMasters() throws Exception {
        try (Connection c = DriverManager.getConnection(jdbcUrl, user, password);
             Statement s = c.createStatement()) {
            s.execute("TRUNCATE inv_transaction, stock_balance, sfdc_order_line, sfdc_order, "
                    + "opening_balance, lot, product, warehouse, uom, tenant, notification_outbox CASCADE");
            s.execute("INSERT INTO tenant (tenant_id, tenant_code, name) VALUES "
                    + "('00000000-0000-0000-0000-000000000001', 'ACME', 'Acme')");
            s.execute("INSERT INTO product (tenant_id, product_code, name) "
                    + "VALUES ('00000000-0000-0000-0000-000000000001', 'P1001', 'P1'),"
                    + "       ('00000000-0000-0000-0000-000000000001', 'P1002', 'P2')");
            s.execute("INSERT INTO warehouse (tenant_id, warehouse_code, name) "
                    + "VALUES ('00000000-0000-0000-0000-000000000001', 'WH-NL-01', 'NL1')");
            s.execute("INSERT INTO uom (tenant_id, uom_code, name) "
                    + "VALUES ('00000000-0000-0000-0000-000000000001', 'EA', 'Each')");
        }
    }

    @Test
    @DisplayName("TC-602: LoadTransactions inserts the configured number of inv_transaction rows")
    void loadsConfiguredCount() throws Exception {
        ToolsConfig cfg = new ToolsConfig(
            42L, db,
            new ToolsConfig.Tenants(1),
            new ToolsConfig.MasterData(2, 1, 0, 1),
            new ToolsConfig.OpeningBalance(0, "/tmp", "x.csv", ";", "2026-05-18"),
            new ToolsConfig.Transactions(20, 0.6, 0.0, true),
            new ToolsConfig.Orders(0, 1, 1, "open")
        );

        int inserted = new LoadTransactions(cfg).run();

        assertThat(inserted).isEqualTo(20);
        try (Connection c = DriverManager.getConnection(jdbcUrl, user, password);
             Statement s = c.createStatement();
             var rs = s.executeQuery("SELECT count(*) FROM inv_transaction")) {
            rs.next();
            assertThat(rs.getInt(1)).isEqualTo(20);
        }
    }
}
