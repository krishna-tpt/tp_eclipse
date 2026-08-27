package org.michelin.filemanager.notifier;

/**
 * Outcome of attempting to deliver a notification. Promoted from a nested type
 * on Notifier so it can be referenced unqualified package-wide.
 */
public enum DeliveryResult {
    DELIVERED,
    FAILED,
    FAILED_PERMANENT
}
