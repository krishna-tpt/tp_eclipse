package org.michelin.filemanager.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.michelin.filemanager.audit.Event;
import org.michelin.filemanager.audit.EventLogWriter;
import org.michelin.filemanager.config.Config;
import org.michelin.filemanager.db.Database;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.sql.ResultSet;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TC-360..TC-363 — EventLogWriter writes against a real Postgres.
 *
 * The full alter_04 ships with deploy/11-06-v6-customer/; here we just create
 * the minimum schema + table inline so the test isn't coupled to the deploy
 * folder's SQL evolution.
 */
class EventLogWriterIT {

    private static final String DDL =
            "CREATE SCHEMA IF NOT EXISTS audit; "
          + "CREATE TABLE IF NOT EXISTS audit.event_log ("
          + "  event_id BIGSERIAL PRIMARY KEY,"
          + "  at TIMESTAMPTZ NOT NULL DEFAULT now(),"
          + "  event_type TEXT NOT NULL,"
          + "  severity TEXT NOT NULL DEFAULT 'info',"
          + "  source TEXT NOT NULL,"
          + "  tenant_id TEXT,"
          + "  correlation_id TEXT,"
          + "  status TEXT,"
          + "  ref_id TEXT,"
          + "  error_code TEXT,"
          + "  error_msg TEXT,"
          + "  latency_ms INTEGER,"
          + "  rows_affected INTEGER,"
          + "  bytes BIGINT,"
          + "  payload JSONB NOT NULL DEFAULT '{}'::jsonb)";

    @Test
    @DisplayName("TC-360: filePicked writes a row with bytes + ref_id + correlation_id")
    void filePicked_writesRow() throws Exception {
        try (Database db = openDb()) {
            ensureSchema(db);
            truncate(db);
            EventLogWriter w = new EventLogWriter(db);

            w.filePicked("MICH_INV_STOCKLEVEL_41320260629120000.dat",
                    412 * 1024L, "corr-abc", "filescom");

            try (Statement s = db.connection().createStatement();
                 ResultSet rs = s.executeQuery(
                     "SELECT event_type, source, severity, ref_id, correlation_id, bytes "
                   + "FROM audit.event_log ORDER BY at DESC LIMIT 1")) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getString("event_type")).isEqualTo("file.picked");
                assertThat(rs.getString("source")).isEqualTo("filescom");
                assertThat(rs.getString("severity")).isEqualTo("info");
                assertThat(rs.getString("ref_id")).isEqualTo("MICH_INV_STOCKLEVEL_41320260629120000.dat");
                assertThat(rs.getString("correlation_id")).isEqualTo("corr-abc");
                assertThat(rs.getLong("bytes")).isEqualTo(412L * 1024);
            }
        }
    }

    @Test
    @DisplayName("TC-361: fileFailed writes severity=error with error_code and message")
    void fileFailed_writesErrorRow() throws Exception {
        try (Database db = openDb()) {
            ensureSchema(db);
            truncate(db);
            EventLogWriter w = new EventLogWriter(db);

            w.fileFailed("bad.dat", "footer_count_mismatch",
                    "expected 100, got 99", "corr-xyz", "filescom");

            try (Statement s = db.connection().createStatement();
                 ResultSet rs = s.executeQuery(
                     "SELECT event_type, severity, error_code, error_msg, status "
                   + "FROM audit.event_log ORDER BY at DESC LIMIT 1")) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getString("event_type")).isEqualTo("file.failed");
                assertThat(rs.getString("severity")).isEqualTo("error");
                assertThat(rs.getString("error_code")).isEqualTo("footer_count_mismatch");
                assertThat(rs.getString("error_msg")).isEqualTo("expected 100, got 99");
                assertThat(rs.getString("status")).isEqualTo("failed");
            }
        }
    }

    @Test
    @DisplayName("TC-362: heartbeat writes a minimal row tagged source=daemon")
    void heartbeat_writesMinimalRow() throws Exception {
        try (Database db = openDb()) {
            ensureSchema(db);
            truncate(db);
            EventLogWriter w = new EventLogWriter(db);

            w.heartbeat();

            try (Statement s = db.connection().createStatement();
                 ResultSet rs = s.executeQuery(
                     "SELECT event_type, source, tenant_id, ref_id "
                   + "FROM audit.event_log ORDER BY at DESC LIMIT 1")) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getString("event_type")).isEqualTo("daemon.heartbeat");
                assertThat(rs.getString("source")).isEqualTo("daemon");
                assertThat(rs.getString("tenant_id")).isNull();
                assertThat(rs.getString("ref_id")).isNull();
            }
        }
    }

    @Test
    @DisplayName("TC-363: write of an explicit Event with payload lands JSONB intact")
    void write_jsonbPayload() throws Exception {
        try (Database db = openDb()) {
            ensureSchema(db);
            truncate(db);
            EventLogWriter w = new EventLogWriter(db);

            boolean ok = w.write(Event.builder("atp.queried", "atp")
                    .tenantId("IFOPEUR")
                    .refId("SFDC")
                    .rowsAffected(3)
                    .latencyMs(28)
                    .payloadJson("{\"warehouse_code\":\"WH-LYO\",\"caller\":\"SFDC\"}")
                    .build());

            assertThat(ok).isTrue();

            try (Statement s = db.connection().createStatement();
                 ResultSet rs = s.executeQuery(
                     "SELECT payload->>'warehouse_code' AS wh, payload->>'caller' AS caller, "
                   + "       tenant_id, rows_affected, latency_ms "
                   + "FROM audit.event_log ORDER BY at DESC LIMIT 1")) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getString("wh")).isEqualTo("WH-LYO");
                assertThat(rs.getString("caller")).isEqualTo("SFDC");
                assertThat(rs.getString("tenant_id")).isEqualTo("IFOPEUR");
                assertThat(rs.getInt("rows_affected")).isEqualTo(3);
                assertThat(rs.getInt("latency_ms")).isEqualTo(28);
            }
        }
    }

    // -----------------------------------------------------------------

    // EventLogWriter needs only a plain Postgres — no pg_cron / pg_partman /
    // pgTAP. Don't depend on the shared InventoryLedgerPgContainer image; that
    // pulls a custom build with all the extensions and is overkill here.
    private static PostgreSQLContainer<?> pg;

    @BeforeAll
    static void startPg() {
        pg = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                .withDatabaseName("audit_test")
                .withUsername("postgres")
                .withPassword("postgres")
                .withUrlParam("sslmode", "disable");
        pg.start();
    }

    @AfterAll
    static void stopPg() {
        if (pg != null) pg.stop();
    }

    private static Database openDb() {
        return new Database(new Config.DbConfig(
                pg.getJdbcUrl(), pg.getUsername(), pg.getPassword(),
                5, 30_000, "inventoryledger-test"));
    }

    private static void ensureSchema(Database db) throws Exception {
        try (Statement s = db.connection().createStatement()) {
            s.execute(DDL);
        }
    }

    private static void truncate(Database db) throws Exception {
        try (Statement s = db.connection().createStatement()) {
            s.execute("TRUNCATE audit.event_log");
        }
    }
}
