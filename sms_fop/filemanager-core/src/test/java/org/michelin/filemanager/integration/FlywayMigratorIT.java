package org.michelin.filemanager.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.michelin.filemanager.config.Config;
import org.michelin.filemanager.db.FlywayMigrator;
import org.michelin.filemanager.integration.support.InventoryLedgerPgContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TC-211, TC-212 — FlywayMigrator behavior.
 */
class FlywayMigratorIT {

    private Config.DbConfig dbConfig() {
        PostgreSQLContainer<?> pg = InventoryLedgerPgContainer.instance();
        return new Config.DbConfig(
            pg.getJdbcUrl(), pg.getUsername(), pg.getPassword(),
            5, 30000, "inventoryledger-test"
        );
    }

    private Config.FlywayConfig flywayConfig() {
        return new Config.FlywayConfig(true, "classpath:db/migration", false, true);
    }

    @Test
    @DisplayName("TC-211: applies all V*.sql to clean schema")
    void appliesAllMigrations() throws Exception {
        FlywayMigrator m = new FlywayMigrator(dbConfig(), flywayConfig());
        m.migrate();

        try (Connection c = DriverManager.getConnection(dbConfig().url(), dbConfig().user(), dbConfig().password());
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(
                 "SELECT version FROM flyway_schema_history WHERE success ORDER BY installed_rank")) {
            int count = 0;
            while (rs.next()) {
                count++;
            }
            assertThat(count).isGreaterThanOrEqualTo(3);
        }
    }

    @Test
    @DisplayName("TC-212: re-run is idempotent (no errors)")
    void reRunIsIdempotent() {
        FlywayMigrator m = new FlywayMigrator(dbConfig(), flywayConfig());
        m.migrate();
        m.migrate();
        m.migrate();
        // Three calls without exception is the assertion.
    }
}
