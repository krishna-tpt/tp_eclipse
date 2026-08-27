package org.michelin.filemanager.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TC-200..TC-204 — ConfigLoader behavior.
 *
 * RED phase: these tests reference Config and ConfigLoader which don't exist yet.
 * Compilation must fail until GREEN phase creates the classes.
 */
class ConfigLoaderTest {

    private static final Path VALID_YAML        = Paths.get("src/test/resources/fixtures/config/valid.yaml");
    private static final Path MISSING_DB_URL    = Paths.get("src/test/resources/fixtures/config/missing_db_url.yaml");

    @Test
    @DisplayName("TC-200: Config record exposes loaded fields and toString masks secrets")
    void configRecordExposesFields_andTostringMasksSecrets() {
        Config cfg = new ConfigLoader().loadFromPath(VALID_YAML, Map.of());

        assertThat(cfg.profile()).isEqualTo("test");
        assertThat(cfg.db().url()).contains("jdbc:postgresql");
        assertThat(cfg.db().user()).isEqualTo("il_app");
        assertThat(cfg.db().password()).isEqualTo("testpassword123");

        // toString must mask the password (not the user; user is not a secret)
        assertThat(cfg.db().toString()).doesNotContain("testpassword123");
        assertThat(cfg.db().toString()).contains("password=***");
    }

    @Test
    @DisplayName("TC-201: env var override wins over YAML value")
    void envOverride_winsOverYaml() {
        Config cfg = new ConfigLoader().loadFromPath(VALID_YAML, Map.of(
            "DB_URL", "jdbc:postgresql://override-host:5432/db"
        ));

        assertThat(cfg.db().url()).isEqualTo("jdbc:postgresql://override-host:5432/db");
    }

    @Test
    @DisplayName("TC-202: missing required field fails fast with named error")
    void missingRequiredField_failsFastWithNamedError() {
        assertThatThrownBy(() -> new ConfigLoader().loadFromPath(MISSING_DB_URL, Map.of()))
            .isInstanceOf(ConfigValidationException.class)
            .hasMessageContaining("db.url");
    }

    @Test
    @DisplayName("TC-203: unknown YAML key does not fail the build (warn only)")
    void unknownYamlKey_doesNotFail() {
        Config cfg = new ConfigLoader().loadFromPath(VALID_YAML,
            Map.of("DB_URL", "jdbc:postgresql://h/db", "UNKNOWN_KEY", "ignored"));

        assertThat(cfg).isNotNull();
        // Unknown env keys are ignored; the loader logs but does not throw.
    }

    @Test
    @DisplayName("TC-204: APP_PROFILE selects the profile YAML")
    void appProfile_selectsProfileYaml() {
        Config cfg = new ConfigLoader().load(Map.of(
            "APP_PROFILE", "dev",
            // dev profile requires no other env vars thanks to defaults
            "WEBHOOK_URL_PRIMARY", "http://localhost:8080/webhook"
        ));

        assertThat(cfg.profile()).isEqualTo("dev");
    }

    // -----------------------------------------------------------------
    // TC-210..TC-217 — CONFIG_DIR overlay (DevOps centralized config repo)
    // -----------------------------------------------------------------

    @Test
    @DisplayName("TC-210: empty CONFIG_DIR returns no overlays (no error)")
    void configDir_empty_returnsNoOverlays(@TempDir Path dir) {
        List<Map<String, Object>> overlays = new ConfigLoader().loadOverlaysFromDir(dir);
        assertThat(overlays).isEmpty();
    }

    @Test
    @DisplayName("TC-211: single YAML file is loaded as one overlay")
    void configDir_singleFile_loaded(@TempDir Path dir) throws IOException {
        Files.writeString(dir.resolve("override.yaml"),
            "db:\n  user: from-overlay\n");

        List<Map<String, Object>> overlays = new ConfigLoader().loadOverlaysFromDir(dir);

        assertThat(overlays).hasSize(1);
        @SuppressWarnings("unchecked")
        Map<String, Object> db = (Map<String, Object>) overlays.get(0).get("db");
        assertThat(db.get("user")).isEqualTo("from-overlay");
    }

    @Test
    @DisplayName("TC-212: multiple files loaded in alphabetical order")
    void configDir_multipleFiles_alphabeticalOrder(@TempDir Path dir) throws IOException {
        Files.writeString(dir.resolve("z-last.yaml"),  "marker: z\n");
        Files.writeString(dir.resolve("a-first.yaml"), "marker: a\n");
        Files.writeString(dir.resolve("m-middle.yaml"), "marker: m\n");

        List<Map<String, Object>> overlays = new ConfigLoader().loadOverlaysFromDir(dir);

        assertThat(overlays).hasSize(3);
        assertThat(overlays.get(0).get("marker")).isEqualTo("a");
        assertThat(overlays.get(1).get("marker")).isEqualTo("m");
        assertThat(overlays.get(2).get("marker")).isEqualTo("z");
    }

    @Test
    @DisplayName("TC-213: non-yaml files (txt/json/properties) are ignored")
    void configDir_nonYamlFiles_ignored(@TempDir Path dir) throws IOException {
        Files.writeString(dir.resolve("keep.yaml"),       "k: 1\n");
        Files.writeString(dir.resolve("keep.yml"),        "k: 2\n");
        Files.writeString(dir.resolve("ignored.txt"),     "not yaml\n");
        Files.writeString(dir.resolve("ignored.json"),    "{}\n");
        Files.writeString(dir.resolve("ignored.properties"), "k=v\n");

        List<Map<String, Object>> overlays = new ConfigLoader().loadOverlaysFromDir(dir);

        assertThat(overlays).hasSize(2);
    }

    @Test
    @DisplayName("TC-214: missing CONFIG_DIR throws ConfigValidationException")
    void configDir_missingDir_throws(@TempDir Path dir) {
        Path nonexistent = dir.resolve("nope");

        assertThatThrownBy(() -> new ConfigLoader().loadOverlaysFromDir(nonexistent))
            .isInstanceOf(ConfigValidationException.class)
            .hasMessageContaining("does not exist");
    }

    @Test
    @DisplayName("TC-215: CONFIG_DIR pointing at a file (not a directory) throws")
    void configDir_pointsAtFile_throws(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("not-a-dir.yaml");
        Files.writeString(file, "k: v\n");

        assertThatThrownBy(() -> new ConfigLoader().loadOverlaysFromDir(file))
            .isInstanceOf(ConfigValidationException.class)
            .hasMessageContaining("not a directory");
    }

    @Test
    @DisplayName("TC-216: empty YAML file is skipped silently, doesn't break the merge")
    void configDir_emptyYamlFile_skipped(@TempDir Path dir) throws IOException {
        Files.writeString(dir.resolve("empty.yaml"), "# only a comment\n");
        Files.writeString(dir.resolve("real.yaml"),  "marker: present\n");

        List<Map<String, Object>> overlays = new ConfigLoader().loadOverlaysFromDir(dir);

        assertThat(overlays).hasSize(1);
        assertThat(overlays.get(0).get("marker")).isEqualTo("present");
    }

    @Test
    @DisplayName("TC-217: YAML with non-map top level (a list) throws clearly")
    void configDir_nonMapTopLevel_throws(@TempDir Path dir) throws IOException {
        Files.writeString(dir.resolve("bad.yaml"), "- one\n- two\n");

        assertThatThrownBy(() -> new ConfigLoader().loadOverlaysFromDir(dir))
            .isInstanceOf(ConfigValidationException.class)
            .hasMessageContaining("must be a YAML map");
    }

    @Test
    @DisplayName("TC-218: CONFIG_DIR overlay merges into full load(), service file wins over common")
    void configDir_endToEnd_serviceOverridesCommon(@TempDir Path dir) throws IOException {
        // common-* sorts before psql-* alphabetically, so service file is applied last
        // (last-wins on conflicting keys — service file should win).
        Files.writeString(dir.resolve("common-service-configs.yaml"),
            "logging:\n  level: DEBUG\n  format: text\n");
        Files.writeString(dir.resolve("psql-inventory-integration-service.yaml"),
            "logging:\n  level: WARN\n");

        Config cfg = new ConfigLoader().load(Map.of(
            "APP_PROFILE", "dev",
            "CONFIG_DIR", dir.toString(),
            "WEBHOOK_URL_PRIMARY", "http://localhost:8080/webhook"
        ));

        // service file overrode the level
        assertThat(cfg.logging().level()).isEqualTo("WARN");
        // common file's format survived because service file didn't touch it
        assertThat(cfg.logging().format()).isEqualTo("text");
    }
}
