package org.michelin.filemanager.lifecycle;

import org.michelin.filemanager.config.ConfigValidationException;
import org.michelin.filemanager.exception.DataAccessException;
import org.michelin.filemanager.exception.FileSourceException;
import org.michelin.filemanager.exception.ValidationException;

import java.sql.SQLException;

/**
 * Maps a thrown exception (or null = success) to an ExitCode. Strategy lookup
 * instead of nested if/instanceof chains; one place to register new types.
 */
public class ExitCodeMapper {

    public ExitCode map(Throwable t) {
        if (t == null) return ExitCode.SUCCESS;
        if (causedBy(t, ConfigValidationException.class)) return ExitCode.CONFIG_ERROR;
        if (causedBy(t, ValidationException.class))       return ExitCode.CONFIG_ERROR;
        if (causedBy(t, DataAccessException.class))       return ExitCode.DB_ERROR;
        if (causedBy(t, SQLException.class))              return ExitCode.DB_ERROR;
        if (causedBy(t, FileSourceException.class))       return ExitCode.FILE_SOURCE_ERROR;
        return ExitCode.PARTIAL_FAILURE;
    }

    private boolean causedBy(Throwable t, Class<? extends Throwable> klass) {
        for (Throwable cur = t; cur != null; cur = cur.getCause()) {
            if (klass.isInstance(cur)) return true;
        }
        return false;
    }
}
