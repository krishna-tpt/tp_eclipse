package org.michelin.filemanager.scheduler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.michelin.filemanager.config.ConfigValidationException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TC-300..TC-306 — CronSchedule behavior. Pure-function tests; no threads.
 */
class CronScheduleTest {

    private static final ZoneId UTC = ZoneOffset.UTC;

    @Test
    @DisplayName("TC-300: daily 06:00 cron yields next 06:00 after current 05:59")
    void daily0600_fromBefore_returnsToday0600() {
        CronSchedule daily = new CronSchedule("daily", "0 6 * * *", UTC);
        Instant from = LocalDateTime.of(2026, 6, 23, 5, 59).toInstant(ZoneOffset.UTC);

        Instant next = daily.nextFireAfter(from);

        assertThat(next).isEqualTo(
                LocalDateTime.of(2026, 6, 23, 6, 0).toInstant(ZoneOffset.UTC));
    }

    @Test
    @DisplayName("TC-301: daily 06:00 cron from 06:00 yields tomorrow 06:00 (strictly after)")
    void daily0600_fromExactFire_returnsTomorrow() {
        CronSchedule daily = new CronSchedule("daily", "0 6 * * *", UTC);
        Instant from = LocalDateTime.of(2026, 6, 23, 6, 0).toInstant(ZoneOffset.UTC);

        Instant next = daily.nextFireAfter(from);

        assertThat(next).isEqualTo(
                LocalDateTime.of(2026, 6, 24, 6, 0).toInstant(ZoneOffset.UTC));
    }

    @Test
    @DisplayName("TC-302: hourly :05 cron from :03 yields :05 this hour")
    void hourlyAt05_fromBefore_returnsThisHour05() {
        CronSchedule hourly = new CronSchedule("hourly", "5 * * * *", UTC);
        Instant from = LocalDateTime.of(2026, 6, 23, 14, 3).toInstant(ZoneOffset.UTC);

        Instant next = hourly.nextFireAfter(from);

        assertThat(next).isEqualTo(
                LocalDateTime.of(2026, 6, 23, 14, 5).toInstant(ZoneOffset.UTC));
    }

    @Test
    @DisplayName("TC-303: hourly :05 cron from :05 yields next hour :05")
    void hourlyAt05_fromExactFire_returnsNextHour() {
        CronSchedule hourly = new CronSchedule("hourly", "5 * * * *", UTC);
        Instant from = LocalDateTime.of(2026, 6, 23, 14, 5).toInstant(ZoneOffset.UTC);

        Instant next = hourly.nextFireAfter(from);

        assertThat(next).isEqualTo(
                LocalDateTime.of(2026, 6, 23, 15, 5).toInstant(ZoneOffset.UTC));
    }

    @Test
    @DisplayName("TC-304: invalid cron expression throws ConfigValidationException with the name")
    void invalidCron_throwsWithName() {
        assertThatThrownBy(() -> new CronSchedule("daily", "this is not cron", UTC))
                .isInstanceOf(ConfigValidationException.class)
                .hasMessageContaining("daily")
                .hasMessageContaining("invalid cron");
    }

    @Test
    @DisplayName("TC-305: blank cron expression rejected at construction")
    void blankCron_rejected() {
        assertThatThrownBy(() -> new CronSchedule("daily", "", UTC))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new CronSchedule("daily", "  ", UTC))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("TC-306: name/expression/zone exposed via accessors")
    void accessors_exposeStateForLogs() {
        CronSchedule s = new CronSchedule("hourly-catchup", "5 * * * *", UTC);

        assertThat(s.name()).isEqualTo("hourly-catchup");
        assertThat(s.expression()).isEqualTo("5 * * * *");
        assertThat(s.zone()).isEqualTo(UTC);
        assertThat(s.toString()).contains("hourly-catchup").contains("5 * * * *");
    }
}
