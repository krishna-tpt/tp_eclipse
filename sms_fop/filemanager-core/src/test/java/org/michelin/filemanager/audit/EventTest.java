package org.michelin.filemanager.audit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TC-340..TC-343 — Event record validation + builder ergonomics.
 * Pure-function tests; no DB.
 */
class EventTest {

    @Test
    @DisplayName("TC-340: builder produces an Event with mandatory fields and info severity by default")
    void builder_defaultSeverityInfo() {
        Event e = Event.builder("file.picked", "files").build();

        assertThat(e.eventType()).isEqualTo("file.picked");
        assertThat(e.source()).isEqualTo("files");
        assertThat(e.severity()).isEqualTo("info");
        assertThat(e.tenantId()).isNull();
        assertThat(e.payloadJson()).isNull();
    }

    @Test
    @DisplayName("TC-341: blank or null eventType is rejected at construction")
    void blankEventType_rejected() {
        assertThatThrownBy(() -> Event.builder("", "files").build())
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Event(null, "info", "files", null, null,
                null, null, null, null, null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("TC-342: blank source is rejected")
    void blankSource_rejected() {
        assertThatThrownBy(() -> Event.builder("file.picked", "").build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("TC-343: builder threads all optional fields through to the record")
    void builder_threadsAllFields() {
        Event e = Event.builder("atp.queried", "atp")
                .severity("warn")
                .tenantId("IFOPEUR")
                .correlationId("corr-123")
                .status("ok")
                .refId("SFDC-bot")
                .errorCode("EC1")
                .errorMsg("slow")
                .latencyMs(42)
                .rowsAffected(7)
                .bytes(2048L)
                .payloadJson("{\"x\":1}")
                .build();

        assertThat(e.severity()).isEqualTo("warn");
        assertThat(e.tenantId()).isEqualTo("IFOPEUR");
        assertThat(e.correlationId()).isEqualTo("corr-123");
        assertThat(e.status()).isEqualTo("ok");
        assertThat(e.refId()).isEqualTo("SFDC-bot");
        assertThat(e.errorCode()).isEqualTo("EC1");
        assertThat(e.errorMsg()).isEqualTo("slow");
        assertThat(e.latencyMs()).isEqualTo(42);
        assertThat(e.rowsAffected()).isEqualTo(7);
        assertThat(e.bytes()).isEqualTo(2048L);
        assertThat(e.payloadJson()).isEqualTo("{\"x\":1}");
    }
}
