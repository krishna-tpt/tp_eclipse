package org.michelin.filemanager.notifier;

import com.fasterxml.jackson.databind.node.NullNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * TC-250..TC-253 — SingleUrlWebhookNotifier behavior with a mocked HttpClient.
 * Class name retained from pre-refactor (renamed type is SingleUrlWebhookNotifier).
 */
@ExtendWith(MockitoExtension.class)
class WebhookNotifierTest {

    private final URI url = URI.create("http://test/webhook");

    private SingleUrlWebhookNotifier notifier(HttpClient http) {
        return new SingleUrlWebhookNotifier(http, url, Duration.ofMillis(500));
    }

    private Notification anyNotification() {
        return new Notification(Severity.WARN, "tc-test", "msg", NullNode.getInstance());
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("TC-250: 2xx response → DELIVERED")
    void twoXxResponse_returnsDelivered() throws Exception {
        HttpClient http = mock(HttpClient.class);
        HttpResponse<String> resp = mock(HttpResponse.class);
        when(resp.statusCode()).thenReturn(200);
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(resp);

        assertThat(notifier(http).send(anyNotification())).isEqualTo(DeliveryResult.DELIVERED);
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("TC-251: 4xx response → FAILED_PERMANENT (no retry)")
    void fourXxResponse_returnsFailedPermanent() throws Exception {
        HttpClient http = mock(HttpClient.class);
        HttpResponse<String> resp = mock(HttpResponse.class);
        when(resp.statusCode()).thenReturn(400);
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(resp);

        assertThat(notifier(http).send(anyNotification())).isEqualTo(DeliveryResult.FAILED_PERMANENT);
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("TC-252: 5xx response → FAILED (retry allowed)")
    void fiveXxResponse_returnsFailed() throws Exception {
        HttpClient http = mock(HttpClient.class);
        HttpResponse<String> resp = mock(HttpResponse.class);
        when(resp.statusCode()).thenReturn(503);
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(resp);

        assertThat(notifier(http).send(anyNotification())).isEqualTo(DeliveryResult.FAILED);
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("TC-253: timeout → FAILED")
    void timeout_returnsFailed() throws Exception {
        HttpClient http = mock(HttpClient.class);
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenThrow(new java.net.http.HttpTimeoutException("simulated"));

        assertThat(notifier(http).send(anyNotification())).isEqualTo(DeliveryResult.FAILED);
    }
}
