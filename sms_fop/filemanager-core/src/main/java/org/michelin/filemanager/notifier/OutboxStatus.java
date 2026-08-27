package org.michelin.filemanager.notifier;

import org.michelin.filemanager.exception.NotificationException;

import java.util.Locale;

/**
 * Mirrors the CHECK constraint on notification_outbox.status. Centralising the
 * conversion in one place means stringly-typed values never escape this class.
 */
public enum OutboxStatus {
    PENDING,
    DELIVERED,
    FAILED,
    FAILED_PERMANENT;

    public String wireName() { return name().toLowerCase(Locale.ROOT); }

    public static OutboxStatus from(String s) {
        if (s == null) throw new NotificationException("OutboxStatus value is null");
        return switch (s.trim().toUpperCase(Locale.ROOT)) {
            case "PENDING"          -> PENDING;
            case "DELIVERED"        -> DELIVERED;
            case "FAILED"           -> FAILED;
            case "FAILED_PERMANENT" -> FAILED_PERMANENT;
            default -> throw new NotificationException("Unknown outbox status: " + s);
        };
    }
}
