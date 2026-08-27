package org.michelin.filemanager.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.michelin.filemanager.config.Config;
import org.michelin.filemanager.db.Database;
import org.michelin.filemanager.integration.support.InventoryLedgerPgContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TC-210 — Database opens a usable connection.
 */
class DatabaseIT {

    @Test
    @DisplayName("TC-210: Database opens a connection; SELECT 1 succeeds")
    void opensConnection() throws Exception {
        PostgreSQLContainer<?> pg = InventoryLedgerPgContainer.instance();
        Config.DbConfig db = new Config.DbConfig(
            pg.getJdbcUrl(), pg.getUsername(), pg.getPassword(),
            5, 30000, "inventoryledger-test"
        );

        try (Database database = new Database(db);
             Connection c = database.connection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT 1")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(1);
        }
    }
}
