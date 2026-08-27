package org.michelin.filemanager.notifier;

/**
 * Maps a (currentRetryCount, deliveryResult) pair to the next OutboxStatus.
 * Pulled out of OutboxDrainer so the rule is testable in isolation and the
 * drainer reads as orchestration only.
 */
public class StatusTransitionPolicy {

    private final int maxRetries;

    public StatusTransitionPolicy(int maxRetries) {
        if (maxRetries < 0) throw new IllegalArgumentException("maxRetries < 0");
        this.maxRetries = maxRetries;
    }

    public OutboxStatus next(int currentRetryCount, DeliveryResult result) {
        return switch (result) {
            case DELIVERED        -> OutboxStatus.DELIVERED;
            case FAILED_PERMANENT -> OutboxStatus.FAILED_PERMANENT;
            case FAILED           -> (currentRetryCount + 1 >= maxRetries)
                                         ? OutboxStatus.FAILED_PERMANENT
                                         : OutboxStatus.FAILED;
        };
    }
}
