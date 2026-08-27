package org.michelin.filemanager.notifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;

/**
 * Delivers a single Notification to a single URL via HTTP POST.
 * Pure delivery — no retry, no multi-target. Compose with MultiUrlNotifier
 * for fan-out or wrap with a Decorator for retry.
 */
public class SingleUrlWebhookNotifier implements Notifier {

    private static final Logger log = LoggerFactory.getLogger(SingleUrlWebhookNotifier.class);
    private static final ObjectMapper JSON = new ObjectMapper();

    private final HttpClient http;
    private final URI url;
    private final Duration timeout;

    public SingleUrlWebhookNotifier(HttpClient http, URI url, Duration timeout) {
        this.http = http;
        this.url = url;
        this.timeout = timeout;
    }

    @Override
    public DeliveryResult send(Notification n) {
        String body = toJson(n);
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(url)
                    .timeout(timeout)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            int code = resp.statusCode();
            if (code >= 200 && code < 300) return DeliveryResult.DELIVERED;
            if (code >= 400 && code < 500) return DeliveryResult.FAILED_PERMANENT;
            return DeliveryResult.FAILED;
        } catch (HttpTimeoutException e) {
            log.warn("webhook timeout {}", url);
            return DeliveryResult.FAILED;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("webhook interrupted {}: {}", url, e.getMessage());
            return DeliveryResult.FAILED;
        } catch (IOException e) {
            log.warn("webhook IO error {}: {}", url, e.getMessage());
            return DeliveryResult.FAILED;
        }
    }

    private String toJson(Notification n) {
        try {
            ObjectNode node = JSON.createObjectNode();
            node.put("severity", n.severity().wireName());
            node.put("source",   n.source());
            node.put("message",  n.message());
            node.set("payload",  n.payload());
            return JSON.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            // Jackson should never fail building a flat object. If it does, bail loud.
            throw new RuntimeException("Failed to serialize notification", e);
        }
    }
}
