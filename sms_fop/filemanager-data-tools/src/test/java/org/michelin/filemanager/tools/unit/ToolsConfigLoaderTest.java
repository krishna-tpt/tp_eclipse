package org.michelin.filemanager.tools.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.michelin.filemanager.tools.config.ToolsConfig;
import org.michelin.filemanager.tools.config.ToolsConfigLoader;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ToolsConfigLoaderTest {

    @Test
    @DisplayName("Loads tools.yaml from classpath with env overrides")
    void loadsToolsYaml() {
        ToolsConfig cfg = new ToolsConfigLoader().load(Map.of(
            "TOOLS_SEED", "1234",
            "DB_URL", "jdbc:postgresql://localhost:5432/test",
            "DB_USER", "user",
            "DB_PASSWORD", "pw"
        ));
        assertThat(cfg.seed()).isEqualTo(1234L);
        assertThat(cfg.db().url()).contains("postgresql");
        assertThat(cfg.tenants().count()).isGreaterThan(0);
    }

    @Test
    @DisplayName("DbConfig toString masks password")
    void maskedPassword() {
        ToolsConfig cfg = new ToolsConfigLoader().load(Map.of(
            "DB_URL", "jdbc:postgresql://h/db",
            "DB_USER", "u",
            "DB_PASSWORD", "very-secret-password-xyz"
        ));
        assertThat(cfg.db().toString()).doesNotContain("very-secret-password-xyz");
        assertThat(cfg.db().toString()).contains("password=***");
    }
}
