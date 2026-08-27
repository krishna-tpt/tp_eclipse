package org.michelin.filemanager.file;

import org.michelin.filemanager.config.ConfigValidationException;

import java.util.Locale;

public enum FileSourceType {
    LOCAL,
    SFTP,
    FILESCOM;

    public static FileSourceType from(String s) {
        if (s == null || s.isBlank()) {
            throw new ConfigValidationException("file.source is required (local|sftp|filescom)");
        }
        return switch (s.trim().toLowerCase(Locale.ROOT)) {
            case "local"    -> LOCAL;
            case "sftp"     -> SFTP;
            case "filescom" -> FILESCOM;
            default -> throw new ConfigValidationException("Unknown file.source: " + s);
        };
    }

    public String wireName() { return name().toLowerCase(Locale.ROOT); }
}
