package org.michelin.filemanager.notifier;

import java.util.List;

/**
 * Composite Notifier — fans out one Notification across multiple delegate
 * Notifiers and aggregates their delivery results.
 *
 * Aggregation policy:
 *   - any DELIVERED       → DELIVERED (at-least-once semantics; we succeeded somewhere)
 *   - else any FAILED     → FAILED (worth retrying)
 *   - else                → FAILED_PERMANENT
 */
public class MultiUrlNotifier implements Notifier {

    private final List<Notifier> delegates;

    public MultiUrlNotifier(List<Notifier> delegates) {
        this.delegates = List.copyOf(delegates);  // defensive
    }

    @Override
    public DeliveryResult send(Notification n) {
        if (delegates.isEmpty()) return DeliveryResult.FAILED_PERMANENT;

        DeliveryResult aggregate = null;
        for (Notifier d : delegates) {
            DeliveryResult r = d.send(n);
            aggregate = combine(aggregate, r);
        }
        return aggregate == null ? DeliveryResult.FAILED : aggregate;
    }

    private DeliveryResult combine(DeliveryResult current, DeliveryResult next) {
        if (current == null) return next;
        if (current == DeliveryResult.DELIVERED || next == DeliveryResult.DELIVERED) return DeliveryResult.DELIVERED;
        if (current == DeliveryResult.FAILED    || next == DeliveryResult.FAILED)    return DeliveryResult.FAILED;
        return DeliveryResult.FAILED_PERMANENT;
    }
}
