package org.michelin.filemanager.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Emits {@code daemon.heartbeat} events on a fixed cadence so the absence of
 * the service is unambiguously detectable in the event stream. Only meaningful
 * in long-running mode ({@code RUN_MODE=scheduled}); never started from the
 * oneshot path.
 *
 * <p>Independent of the cron {@code SchedulerLoop} — cron ticks can be hours
 * apart, but the heartbeat needs to be fine-grained (every 60s by default) so
 * a missed heartbeat is a strong "service down" signal.
 */
public final class HeartbeatEmitter implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(HeartbeatEmitter.class);

    private final Runnable beat;
    private final Duration interval;
    private final ScheduledExecutorService scheduler;

    /**
     * @param beat     called once per interval; must be cheap and non-blocking
     * @param interval period between beats; must be positive
     */
    public HeartbeatEmitter(Runnable beat, Duration interval) {
        if (beat == null)     throw new IllegalArgumentException("beat is required");
        if (interval == null || interval.isZero() || interval.isNegative())
            throw new IllegalArgumentException("interval must be positive");
        this.beat = beat;
        this.interval = interval;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "heartbeat-emitter");
            t.setDaemon(true);
            return t;
        });
    }

    public void start() {
        long ms = interval.toMillis();
        scheduler.scheduleAtFixedRate(this::runBeat, 0L, ms, TimeUnit.MILLISECONDS);
        log.info("heartbeat emitter started (interval={} ms)", ms);
    }

    @Override
    public void close() {
        scheduler.shutdownNow();
        try {
            if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                log.warn("heartbeat emitter did not stop within 2 s");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("heartbeat emitter stopped");
    }

    private void runBeat() {
        try {
            beat.run();
        } catch (Throwable t) {
            // Belt-and-braces — any thrown error caught here so a single bad
            // beat doesn't make the scheduled executor silently stop the rest.
            log.warn("heartbeat emit failed: {}", t.getMessage());
        }
    }
}
