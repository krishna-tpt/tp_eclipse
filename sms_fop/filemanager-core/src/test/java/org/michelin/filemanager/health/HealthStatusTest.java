package org.michelin.filemanager.health;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TC-330..TC-333 — HealthStatus pure-data behavior + JSON serialization.
 */
class HealthStatusTest {

    @Test
    @DisplayName("TC-330: all UP components yield status=UP")
    void allUp_aggregatesUp() {
        HealthStatus s = HealthStatus.builder()
                .check("db",        HealthStatus.ComponentResult.passing())
                .check("scheduler", HealthStatus.ComponentResult.passing())
                .build();

        assertThat(s.up()).isTrue();
    }

    @Test
    @DisplayName("TC-331: any DOWN component yields status=DOWN")
    void anyDown_aggregatesDown() {
        HealthStatus s = HealthStatus.builder()
                .check("db",        HealthStatus.ComponentResult.failing("nope"))
                .check("scheduler", HealthStatus.ComponentResult.passing())
                .build();

        assertThat(s.up()).isFalse();
    }

    @Test
    @DisplayName("TC-332: JSON output preserves insertion order and includes reason on DOWN")
    void json_preservesOrder_andReason() {
        Map<String, HealthStatus.ComponentResult> ordered = new LinkedHashMap<>();
        ordered.put("first",  HealthStatus.ComponentResult.passing());
        ordered.put("second", HealthStatus.ComponentResult.failing("bad"));
        ordered.put("third",  HealthStatus.ComponentResult.passing());

        String json = HealthStatus.from(ordered).toJson();

        // Order check: index-of must follow insertion order.
        int i1 = json.indexOf("\"first\"");
        int i2 = json.indexOf("\"second\"");
        int i3 = json.indexOf("\"third\"");
        assertThat(i1).isLessThan(i2);
        assertThat(i2).isLessThan(i3);

        assertThat(json).contains("\"status\":\"DOWN\"")
                .contains("\"second\":{\"status\":\"DOWN\",\"reason\":\"bad\"}");
    }

    @Test
    @DisplayName("TC-333: JSON escapes embedded quotes and newlines in reason text")
    void json_escapesSpecialChars() {
        HealthStatus s = HealthStatus.builder()
                .check("ix", HealthStatus.ComponentResult.failing("he said \"nope\"\nand left"))
                .build();

        String json = s.toJson();

        assertThat(json).contains("\\\"nope\\\"")
                .contains("\\nand left");
    }
}
