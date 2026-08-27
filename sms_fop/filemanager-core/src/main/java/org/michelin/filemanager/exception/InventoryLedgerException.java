package org.michelin.filemanager.exception;

/**
 * Root of the project's exception hierarchy. All domain exceptions extend
 * this so callers can catch broadly when needed, or narrow when they know.
 */
public class InventoryLedgerException extends RuntimeException {
    public InventoryLedgerException(String message) { super(message); }
    public InventoryLedgerException(String message, Throwable cause) { super(message, cause); }
}
