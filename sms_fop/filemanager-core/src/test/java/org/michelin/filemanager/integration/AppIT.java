package org.michelin.filemanager.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.michelin.filemanager.App;
import org.michelin.filemanager.integration.support.InventoryLedgerPgContainer;
import org.michelin.filemanager.lifecycle.ExitCode;
import org.testcontainers.containers.PostgreSQLContainer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TC-270..TC-273 — App.run exit codes via the full catalog-driven loader.
 *
 * <p>Uses the catalog-era BATCH_408 fixture (Michelin sample). Filenames must
 * match the catalog's filename_pattern for variant detection to succeed; the
 * partial-failure case mixes a BATCH-matching file (success) with one whose
 * name does not match (rejected by VariantDetector with reason "no variant
 * matches filename").
 *
 * <p>Note: this IT also depends on InventoryLedgerPgContainer which uses a
 * private Docker image and may not start in every dev environment. See
 * docs/CLEANUP-CANDIDATES.md for the planned refactor to stock postgres.
 */
class AppIT {

    private static final Path BATCH_FIXTURE = Paths.get(
        "src/test/resources/fixtures/integration/MICH_INV_STOCKLEVEL_BATCH_408_20260603160800.cfo");
    private static final String BATCH_FILENAME = "MICH_INV_STOCKLEVEL_BATCH_408_20260603160800.cfo";

    private Map<String,String> baseEnv(Path root) {
        PostgreSQLContainer<?> pg = InventoryLedgerPgContainer.instance();
        Map<String,String> env = new HashMap<>();
        env.put("APP_PROFILE",        "test");
        env.put("DB_URL",             pg.getJdbcUrl());
        env.put("DB_USER",            pg.getUsername());
        env.put("DB_PASSWORD",        pg.getPassword());
        env.put("FILE_SOURCE",        "local");
        env.put("LOCAL_PICKUP_PATH",  root.resolve("inbound").toString());
        env.put("LOCAL_ARCHIVE_PATH", root.resolve("archive").toString());
        env.put("LOCAL_REJECT_PATH",  root.resolve("rejected").toString());
        env.put("WEBHOOK_URL_PRIMARY","http://localhost:1");
        return env;
    }

    @Test
    @DisplayName("TC-270: exit code 0 on success — one matching BATCH file → archived")
    void exitZeroOnSuccess(@TempDir Path root) throws Exception {
        Files.createDirectories(root.resolve("inbound"));
        Files.createDirectories(root.resolve("archive"));
        Files.createDirectories(root.resolve("rejected"));
        Files.copy(BATCH_FIXTURE, root.resolve("inbound").resolve(BATCH_FILENAME));

        int code = App.run(new String[]{}, baseEnv(root));
        assertThat(code).isEqualTo(ExitCode.SUCCESS.code());
    }

    @Test
    @DisplayName("TC-271: exit code 1 on missing required config")
    void exitOneOnConfigError(@TempDir Path root) {
        Map<String,String> env = baseEnv(root);
        env.remove("DB_URL");
        int code = App.run(new String[]{}, env);
        assertThat(code).isEqualTo(ExitCode.CONFIG_ERROR.code());
    }

    @Test
    @DisplayName("TC-272: exit code 2 on DB unreachable")
    void exitTwoOnDbError(@TempDir Path root) {
        Map<String,String> env = baseEnv(root);
        env.put("DB_URL", "jdbc:postgresql://localhost:1/nope");
        int code = App.run(new String[]{}, env);
        assertThat(code).isEqualTo(ExitCode.DB_ERROR.code());
    }

    @Test
    @DisplayName("TC-273: exit code 4 on partial failure — one matching + one non-matching name")
    void exitFourOnPartialFailure(@TempDir Path root) throws Exception {
        Path inbound = root.resolve("inbound");
        Files.createDirectories(inbound);
        Files.createDirectories(root.resolve("archive"));
        Files.createDirectories(root.resolve("rejected"));

        // Good — matches BATCH filename_pattern + header_confirms
        Files.copy(BATCH_FIXTURE, inbound.resolve(BATCH_FILENAME));
        // Bad — filename does not match any catalog variant → variant detector rejects
        Files.copy(BATCH_FIXTURE, inbound.resolve("some_other_file_2026.txt"));

        int code = App.run(new String[]{}, baseEnv(root));
        assertThat(code).isEqualTo(ExitCode.PARTIAL_FAILURE.code());
    }
}
