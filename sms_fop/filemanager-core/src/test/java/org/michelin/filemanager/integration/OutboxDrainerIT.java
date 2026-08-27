package org.michelin.filemanager.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.michelin.filemanager.config.Config;
import org.michelin.filemanager.db.Database;
import org.michelin.filemanager.db.FlywayMigrator;
import org.michelin.filemanager.integration.support.InventoryLedgerPgContainer;
import org.michelin.filemanager.notifier.DeliveryResult;
import org.michelin.filemanager.notifier.Notifier;
import org.michelin.filemanager.notifier.OutboxDrainer;
import org.michelin.filemanager.notifier.OutboxRepository;
import org.michelin.filemanager.notifier.StatusTransitionPolicy;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TC-260..TC-263 — OutboxDrainer behavior against a real outbox table.
 * Drainer now collaborates with OutboxRepository + StatusTransitionPolicy.
 */
class OutboxDrainerIT {

    private static Config.DbConfig db;

    @BeforeAll
    static void migrate() {
        PostgreSQLContainer<?> pg = InventoryLedgerPgContainer.instance();
        db = new Config.DbConfig(pg.getJdbcUrl(), pg.getUsername(), pg.getPassword(),
                                 5, 30000, "inventoryledger-test");
        new FlywayMigrator(db, new Config.FlywayConfig(true, "classpath:db/migration", false, true)).migrate();
    }

    @BeforeEach
    void truncate() throws Exception {
        try (Connection c = DriverManager.getConnection(db.url(), db.user(), db.password());
             Statement s = c.createStatement()) {
            s.execute("TRUNCATE notification_outbox");
        }
    }

    private void insertOutboxRow(String severity, String status) throws Exception {
        try (Connection c = DriverManager.getConnection(db.url(), db.user(), db.password());
             Statement s = c.createStatement()) {
            s.executeUpdate(
                "INSERT INTO notification_outbox (severity, source, message, payload, status) " +
                "VALUES ('" + severity + "', 'test', 'msg', '{}'::jsonb, '" + status + "')");
        }
    }

    private OutboxDrainer drainer(Database dbConn, Notifier notifier, int maxRetries) {
        return new OutboxDrainer(dbConn, new OutboxRepository(), notifier,
                                 new StatusTransitionPolicy(maxRetries), maxRetries, 50);
    }

    @Test
    @DisplayName("TC-260: drain selects pending + failed rows below max_retries")
    void drainsPendingAndFailed() throws Exception {
        insertOutboxRow("info", "pending");
        insertOutboxRow("warn", "failed");
        insertOutboxRow("warn", "delivered"); // should NOT be picked up

        AtomicInteger calls = new AtomicInteger();
        Notifier notifier = n -> { calls.incrementAndGet(); return DeliveryResult.DELIVERED; };

        try (Database dbConn = new Database(db)) {
            var result = drainer(dbConn, notifier, 5).drain();
            assertThat(calls.get()).isEqualTo(2);
            assertThat(result.delivered()).isEqualTo(2);
        }
    }

    @Test
    @DisplayName("TC-261: DELIVERED → row.status='delivered' and delivered_at set")
    void deliveredMarksRow() throws Exception {
        insertOutboxRow("info", "pending");
        Notifier notifier = n -> DeliveryResult.DELIVERED;

        try (Database dbConn = new Database(db)) {
            drainer(dbConn, notifier, 5).drain();
        }

        try (Connection c = DriverManager.getConnection(db.url(), db.user(), db.password());
             Statement s = c.createStatement();
             var rs = s.executeQuery("SELECT status, delivered_at FROM notification_outbox LIMIT 1")) {
            rs.next();
            assertThat(rs.getString(1)).isEqualTo("delivered");
            assertThat(rs.getTimestamp(2)).isNotNull();
        }
    }

    @Test
    @DisplayName("TC-262: FAILED → retry_count++, status='failed'")
    void failedIncrementsRetry() throws Exception {
        insertOutboxRow("info", "pending");
        Notifier notifier = n -> DeliveryResult.FAILED;

        try (Database dbConn = new Database(db)) {
            drainer(dbConn, notifier, 5).drain();
        }

        try (Connection c = DriverManager.getConnection(db.url(), db.user(), db.password());
             Statement s = c.createStatement();
             var rs = s.executeQuery("SELECT status, retry_count FROM notification_outbox LIMIT 1")) {
            rs.next();
            assertThat(rs.getString(1)).isEqualTo("failed");
            assertThat(rs.getInt(2)).isEqualTo(1);
        }
    }

    @Test
    @DisplayName("TC-263: retry_count >= max → status='failed_permanent'")
    void exhaustedRetriesMarksPermanent() throws Exception {
        try (Connection c = DriverManager.getConnection(db.url(), db.user(), db.password());
             Statement s = c.createStatement()) {
            s.executeUpdate("INSERT INTO notification_outbox (severity, source, message, payload, status, retry_count) " +
                            "VALUES ('warn', 'test', 'msg', '{}'::jsonb, 'failed', 4)");
        }

        Notifier notifier = n -> DeliveryResult.FAILED;
        try (Database dbConn = new Database(db)) {
            drainer(dbConn, notifier, 5).drain();
        }

        try (Connection c = DriverManager.getConnection(db.url(), db.user(), db.password());
             Statement s = c.createStatement();
             var rs = s.executeQuery("SELECT status, retry_count FROM notification_outbox LIMIT 1")) {
            rs.next();
            assertThat(rs.getString(1)).isEqualTo("failed_permanent");
            assertThat(rs.getInt(2)).isEqualTo(5);
        }
    }
}
