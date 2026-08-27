package org.michelin.filemanager.exception;

public class NotificationException extends InventoryLedgerException {
    public NotificationException(String message) { super(message); }
    public NotificationException(String message, Throwable cause) { super(message, cause); }
}
