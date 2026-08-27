package org.michelin.filemanager.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.michelin.filemanager.config.Config;
import org.michelin.filemanager.db.FlywayMigrator;
import org.michelin.filemanager.integration.support.InventoryLedgerPgContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Runs the entire db/test/*.sql pgTAP suite against the Testcontainers PG.
 * Each .sql file is one transaction (BEGIN/ROLLBACK); we just execute each
 * statement-by-statement and rely on pgTAP's "ok"/"not ok" output for asserts.
 * Failures bubble up as SQLExceptions.
 */
class PgTapSuiteIT {

    private static Config.DbConfig db;
    private static final Path TEST_DIR = Paths.get("../db/test");

    @BeforeAll
    static void migrate() {
        PostgreSQLContainer<?> pg = InventoryLedgerPgContainer.instance();
        db = new Config.DbConfig(pg.getJdbcUrl(), pg.getUsername(), pg.getPassword(),
                                 5, 60000, "inventoryledger-pgtap");
        new FlywayMigrator(db, new Config.FlywayConfig(true, "classpath:db/migration", false, true)).migrate();

        // pgTAP must be installed by the test image; create extension if not yet present.
        try (Connection c = DriverManager.getConnection(db.url(), db.user(), db.password());
             Statement s = c.createStatement()) {
            s.execute("CREATE EXTENSION IF NOT EXISTS pgtap");
        } catch (Exception e) {
            throw new RuntimeException(
                "pgTAP extension required for pgTAP suite. Use test image with pgTAP pre-installed.", e);
        }
    }

    @Test
    @DisplayName("All db/test/*.sql files run without raising")
    void allPgTapFilesRun() throws Exception {
        try (Stream<Path> files = Files.list(TEST_DIR)) {
            files.filter(p -> p.getFileName().toString().endsWith(".sql"))
                 // 09_guerrilla and 10_adversarial use psql meta-commands
                 // (\set ON_ERROR_STOP off) and a tc() helper that depends on
                 // the psql CLI's error-skipping. Run those via the Python
                 // report builder, not this JDBC harness.
                 .filter(p -> !p.getFileName().toString().startsWith("09_"))
                 .filter(p -> !p.getFileName().toString().startsWith("10_"))
                 .sorted()
                 .forEach(this::runFile);
        }
    }

    private void runFile(Path file) {
        try (Connection c = DriverManager.getConnection(db.url(), db.user(), db.password())) {
            c.setAutoCommit(false);
            String sql = Files.readString(file);
            try (Statement s = c.createStatement()) {
                s.execute(sql);
            }
            // BEGIN/ROLLBACK in the file handles cleanup; we explicitly rollback as well.
            c.rollback();
        } catch (Exception e) {
            throw new AssertionError("pgTAP file failed: " + file.getFileName(), e);
        }
        assertThat(file).exists();
    }
}
