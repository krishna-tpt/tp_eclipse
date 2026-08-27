package org.michelin.filemanager.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.michelin.filemanager.catalog.Catalog;
import org.michelin.filemanager.catalog.CatalogLoader;
import org.michelin.filemanager.ingest.CatalogIngestPipeline;
import org.michelin.filemanager.ingest.IngestResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TC-310 — end-to-end: real Michelin BATCH_408 sample file (65 stock rows) →
 * CatalogIngestPipeline → staging.stocklevel_inbox populated.
 *
 * TC-311 — promotion: calling load_stocklevel(file_name) promotes staged rows
 * into the live opening_balance table; the batch row records the outcome.
 *
 * <p>Uses a stock postgres:16-alpine container, applies V14 + a minimal live
 * schema fixture + V15, so the test runs in any local dev environment without
 * pulling pg_partman / pg_cron / pgTAP.
 */
class CatalogIngestPipelineIT {

    private static PostgreSQLContainer<?> pg;

    @BeforeAll
    static void startContainer() throws Exception {
        pg = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("ingest_test")
            .withUsername("postgres")
            .withPassword("postgres")
            .withUrlParam("sslmode", "disable");
        pg.start();
        applyDdl("/db/migration/V14__stocklevel_staging.sql");
        applyDdl("fixture:src/test/resources/fixtures/integration/test_live_schema.sql");
        applyDdl("/db/migration/V15__load_stocklevel_function.sql");
    }

    @AfterAll
    static void stopContainer() {
        if (pg != null) pg.stop();
    }

    private static void applyDdl(String location) throws Exception {
        String ddl;
        if (location.startsWith("fixture:")) {
            ddl = Files.readString(Paths.get(location.substring("fixture:".length())));
        } else {
            try (InputStream in = CatalogIngestPipelineIT.class.getResourceAsStream(location)) {
                if (in == null) throw new IllegalStateException("not on classpath: " + location);
                ddl = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
        try (Connection c = DriverManager.getConnection(pg.getJdbcUrl(), pg.getUsername(), pg.getPassword());
             Statement s = c.createStatement()) {
            s.execute(ddl);
        }
    }

    @Test
    @DisplayName("TC-310/311: BATCH_408 → 65 staging rows → load_stocklevel promotes into opening_balance")
    void fullPipeline_writesToStaging_andPromotesToLive() throws Exception {
        // Clean both staging and live for a clean run
        try (Connection c = DriverManager.getConnection(pg.getJdbcUrl(), pg.getUsername(), pg.getPassword());
             Statement s = c.createStatement()) {
            s.execute("TRUNCATE TABLE opening_balance RESTART IDENTITY CASCADE");
            s.execute("TRUNCATE TABLE staging.stocklevel_inbox RESTART IDENTITY CASCADE");
            s.execute("TRUNCATE TABLE staging.stocklevel_batch RESTART IDENTITY CASCADE");
            // Keep tenant; truncate product/warehouse/uom so auto-create is exercised
            s.execute("TRUNCATE TABLE product, warehouse, uom RESTART IDENTITY CASCADE");
        }

        Catalog catalog = new CatalogLoader().loadFromClasspath("interfaces");
        CatalogIngestPipeline pipeline = new CatalogIngestPipeline(catalog);

        Path file = Paths.get(
            "src/test/resources/fixtures/integration/MICH_INV_STOCKLEVEL_BATCH_408_20260603160800.cfo");
        String filename = file.getFileName().toString();

        try (Connection c = DriverManager.getConnection(pg.getJdbcUrl(), pg.getUsername(), pg.getPassword())) {
            // ============ STAGE: parse + INSERT into staging ============
            c.setAutoCommit(false);
            IngestResult result = pipeline.ingest(file, filename, c);
            c.commit();
            c.setAutoCommit(true);

            assertThat(result).isInstanceOf(IngestResult.Success.class);
            IngestResult.Success ok = (IngestResult.Success) result;
            assertThat(ok.totalRowsWritten()).isEqualTo(65);

            // ============ PROMOTE: call load_stocklevel via SQL ============
            long batchId; int accepted; int rejected; String status;
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT batch_id, rows_accepted, rows_rejected, status FROM load_stocklevel(?)")) {
                ps.setString(1, filename);
                try (ResultSet rs = ps.executeQuery()) {
                    assertThat(rs.next()).isTrue();
                    batchId  = rs.getLong("batch_id");
                    accepted = rs.getInt("rows_accepted");
                    rejected = rs.getInt("rows_rejected");
                    status   = rs.getString("status");
                }
            }

            System.out.println();
            System.out.printf("===== promotion summary =====%n  batch_id=%d  accepted=%d  rejected=%d  status=%s%n%n",
                batchId, accepted, rejected, status);

            assertThat(status).isEqualTo("loaded");
            assertThat(accepted).isEqualTo(65);
            assertThat(rejected).isEqualTo(0);

            // ============ ASSERT: opening_balance populated ============
            try (Statement s = c.createStatement();
                 ResultSet rs = s.executeQuery(
                     "SELECT COUNT(*) FROM opening_balance WHERE batch_id = " + batchId)) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getInt(1)).isEqualTo(65);
            }

            // Spot-check the same item we used for the staging assertion: 459473_101
            try (Statement s = c.createStatement();
                 ResultSet rs = s.executeQuery(
                     "SELECT qty, uom_code, as_of_date, product_code, warehouse_code, tenant_code " +
                     "  FROM opening_balance WHERE product_code = '459473_101'")) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getBigDecimal("qty")).isEqualByComparingTo(new BigDecimal("2"));
                assertThat(rs.getString("uom_code")).isEqualTo("EA");
                assertThat(rs.getDate("as_of_date").toLocalDate()).isEqualTo(LocalDate.of(2026, 6, 3));
                assertThat(rs.getString("tenant_code")).isEqualTo("IFOPEUR");
                assertThat(rs.getString("warehouse_code")).isEqualTo("408_ES_P57_P57_WH_CO");
            }

            // Print first/last live rows so we can eyeball
            try (Statement s = c.createStatement();
                 ResultSet rs = s.executeQuery(
                     "SELECT opening_balance_id, product_code, qty, uom_code, as_of_date " +
                     "  FROM opening_balance ORDER BY opening_balance_id")) {
                System.out.println("===== opening_balance (first 5 + last 3 of 65) =====");
                System.out.printf("%-6s %-13s %5s %-4s %s%n",
                    "ob_id", "product", "qty", "uom", "as_of_date");
                java.util.ArrayList<String> rows = new java.util.ArrayList<>();
                while (rs.next()) {
                    rows.add(String.format("%-6d %-13s %5s %-4s %s",
                        rs.getLong(1),
                        rs.getString("product_code"),
                        rs.getBigDecimal("qty"),
                        rs.getString("uom_code"),
                        rs.getDate("as_of_date")));
                }
                for (int i = 0; i < Math.min(5, rows.size()); i++) System.out.println(rows.get(i));
                if (rows.size() > 8) System.out.println("  ... (" + (rows.size() - 8) + " more rows) ...");
                for (int i = Math.max(rows.size() - 3, 5); i < rows.size(); i++) System.out.println(rows.get(i));
                System.out.println();
            }

            // Optional sidecar exports for review
            String stagingExport = System.getProperty("staging.export.csv");
            if (stagingExport != null) {
                dumpQueryToCsv(c, Paths.get(stagingExport),
                    "SELECT * FROM staging.stocklevel_inbox ORDER BY line_number");
                System.out.println("staging.stocklevel_inbox exported to " + stagingExport);
            }
            String liveExport = System.getProperty("live.export.csv");
            if (liveExport != null) {
                dumpQueryToCsv(c, Paths.get(liveExport),
                    "SELECT * FROM opening_balance ORDER BY opening_balance_id");
                System.out.println("opening_balance exported to " + liveExport);
            }
        }
    }

    private static void dumpQueryToCsv(Connection c, Path target, String sql) throws Exception {
        try (Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql);
             BufferedWriter w = Files.newBufferedWriter(target, StandardCharsets.UTF_8)) {
            ResultSetMetaData md = rs.getMetaData();
            int n = md.getColumnCount();
            for (int i = 1; i <= n; i++) {
                if (i > 1) w.write(",");
                w.write(md.getColumnName(i));
            }
            w.newLine();
            while (rs.next()) {
                for (int i = 1; i <= n; i++) {
                    if (i > 1) w.write(",");
                    Object v = rs.getObject(i);
                    if (v != null) w.write(csvEscape(v.toString()));
                }
                w.newLine();
            }
        }
    }

    private static String csvEscape(String s) {
        if (s.indexOf(',') < 0 && s.indexOf('"') < 0 && s.indexOf('\n') < 0) return s;
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }
}
