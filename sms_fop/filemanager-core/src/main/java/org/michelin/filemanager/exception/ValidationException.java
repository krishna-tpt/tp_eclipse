package org.michelin.filemanager.exception;

/**
 * Validation failures — config shape, payload shape, header columns, etc.
 * ConfigValidationException extends this so existing callers stay correct.
 */
public class ValidationException extends InventoryLedgerException {
    public ValidationException(String message) { super(message); }
    public ValidationException(String message, Throwable cause) { super(message, cause); }
}
