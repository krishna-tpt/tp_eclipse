package org.michelin.filemanager.lifecycle;

public enum ExitCode {
    SUCCESS(0),
    CONFIG_ERROR(1),
    DB_ERROR(2),
    FILE_SOURCE_ERROR(3),
    PARTIAL_FAILURE(4),
    UNKNOWN_ERROR(5);

    private final int code;

    ExitCode(int code) { this.code = code; }

    public int code() { return code; }
}
