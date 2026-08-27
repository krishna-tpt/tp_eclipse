package org.michelin.filemanager.notifier;

import java.util.Locale;

public enum Severity {
    INFO, WARN, ERROR, CRITICAL;

    /**
     * Wire-format value written to {@code processed.notification_outbox.severity}.
     * The customer schema's CHECK constraint accepts only INFO/WARN/ERROR (uppercase),
     * so we send the enum name directly. CRITICAL maps to ERROR for storage since
     * the customer schema doesn't carry a separate CRITICAL state.
     */
    public String wireName() {
        return this == CRITICAL ? "ERROR" : name();
    }

    public static Severity from(String s) {
        if (s == null) return INFO;
        return switch (s.trim().toUpperCase(Locale.ROOT)) {
            case "INFO"     -> INFO;
            case "WARN", "WARNING" -> WARN;
            case "ERROR"    -> ERROR;
            case "CRITICAL", "CRIT", "FATAL" -> CRITICAL;
            default -> INFO;
        };
    }
}
