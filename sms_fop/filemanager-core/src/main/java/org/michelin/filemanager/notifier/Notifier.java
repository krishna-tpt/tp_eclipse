package org.michelin.filemanager.notifier;

/**
 * Sink for outbound delivery of a Notification. Implementations include:
 *   - SingleUrlWebhookNotifier — one HTTP endpoint
 *   - MultiUrlNotifier         — composite over many notifiers
 *   - (future) RetryingNotifier, LoggingNotifier — decorators
 *
 * Implementations must be thread-safe.
 */
public interface Notifier {
    DeliveryResult send(Notification notification);
}
