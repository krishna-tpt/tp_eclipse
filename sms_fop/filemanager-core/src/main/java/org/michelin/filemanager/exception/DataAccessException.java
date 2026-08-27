package org.michelin.filemanager.exception;

import java.sql.SQLException;

/**
 * Wraps any JDBC / SQL failure so callers don't have to catch checked SQLException
 * across the codebase. Preserves the original via getCause() and exposes the
 * SQLSTATE code when known.
 */
public class DataAccessException extends InventoryLedgerException {
    private final String sqlState;

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
        this.sqlState = (cause instanceof SQLException sqle) ? sqle.getSQLState() : null;
    }

    public DataAccessException(String message) {
        super(message);
        this.sqlState = null;
    }

    public String sqlState() { return sqlState; }
}
