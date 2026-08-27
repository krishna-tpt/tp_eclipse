package org.michelin.filemanager.notifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.michelin.filemanager.db.Database;

import java.sql.Timestamp;
import java.util.List;

/**
 * Orchestrates one outbox drain pass. Delegates SQL to {@link OutboxRepository}
 * and status-transition logic to {@link StatusTransitionPolicy}; this class is
 * just the loop. Everything happens inside a single transaction so
 * SELECT ... FOR UPDATE SKIP LOCKED rows stay locked until we commit.
 */
public class OutboxDrainer {

    private static final Logger log = LoggerFactory.getLogger(OutboxDrainer.class);

    private final Database db;
    private final OutboxRepository repo;
    private final Notifier notifier;
    private final StatusTransitionPolicy policy;
    private final int maxRetries;
    private final int batchSize;

    public OutboxDrainer(Database db,
                         OutboxRepository repo,
                         Notifier notifier,
                         StatusTransitionPolicy policy,
                         int maxRetries,
                         int batchSize) {
        this.db = db;
        this.repo = repo;
        this.notifier = notifier;
        this.policy = policy;
        this.maxRetries = maxRetries;
        this.batchSize = batchSize;
    }

    public DrainResult drain() {
        return db.inTransaction(c -> {
            int delivered = 0;
            int failed = 0;
            int failedPermanent = 0;

            List<OutboxRepository.Row> rows = repo.fetchPending(c, maxRetries, batchSize);
            for (OutboxRepository.Row row : rows) {
                DeliveryResult result = notifier.send(row.notification());
                Timestamp now = OutboxRepository.now();

                if (result == DeliveryResult.DELIVERED) {
                    repo.markDelivered(c, row.id(), now);
                    delivered++;
                    continue;
                }

                OutboxStatus next = policy.next(row.retryCount(), result);
                repo.markNonDelivered(c, row.id(), next, now);
                if (next == OutboxStatus.FAILED_PERMANENT) failedPermanent++;
                else failed++;
            }

            log.info("outbox drain pass: scanned={} delivered={} failed={} permanent={}",
                    rows.size(), delivered, failed, failedPermanent);
            return new DrainResult(delivered, failed, failedPermanent);
        });
    }

    public record DrainResult(int delivered, int failed, int failedPermanent) {}
}
