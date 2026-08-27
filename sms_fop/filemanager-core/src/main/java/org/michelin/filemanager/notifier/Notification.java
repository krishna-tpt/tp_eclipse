package org.michelin.filemanager.notifier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;

import java.util.Objects;

/**
 * Top-level notification record. Replaces Notifier.NotificationPayload.
 * payload is a typed JsonNode (Jackson) so we never carry stringly-typed JSON
 * through the system — malformed JSON fails fast at the point of construction.
 */
public record Notification(
        Severity severity,
        String source,
        String message,
        JsonNode payload) {

    public Notification {
        Objects.requireNonNull(severity, "severity");
        Objects.requireNonNull(source,   "source");
        Objects.requireNonNull(message,  "message");
        if (payload == null) payload = NullNode.getInstance();
    }
}
