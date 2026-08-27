package org.michelin.filemanager.config;

import java.util.Locale;

public enum Profile {
    DEV,
    TEST,
    PROD;

    public static Profile from(String s) {
        if (s == null || s.isBlank()) return PROD;
        return switch (s.trim().toLowerCase(Locale.ROOT)) {
            case "dev"  -> DEV;
            case "test" -> TEST;
            case "prod", "production" -> PROD;
            default -> throw new ConfigValidationException("Unknown profile: " + s);
        };
    }

    public String displayName() { return name().toLowerCase(Locale.ROOT); }
}
