package org.michelin.filemanager.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;

/**
 * Long-running scheduler thread (ADR-0014). On each iteration:
 *   1. Compute the earliest next-fire across all configured CronSchedules.
 *   2. Sleep until that instant (interruptible).
 *   3. Invoke the work supplier under a mutex so overlapping schedules can't
 *      run concurrently. A thrown exception is logged WARN and swallowed so
 *      one bad run does not kill the loop.
 *   4. Repeat.
 *
 * Lifecycle:
 *   start()  → spawns one non-daemon thread, returns
 *   stop()   → flips the running flag and interrupts the thread; safe to
 *              call from a shutdown hook
 *   isAlive() → liveness signal for the health endpoint
 *   awaitTermination(timeout) → blocks until the thread exits or the
 *              timeout elapses; for orderly shutdown coordination
 *
 * Thread model:
 *   - One scheduler thread runs the loop
 *   - All access to {@code running}, {@code thread}, {@code lastRunCompletedAt}
 *     goes through volatile / synchronized — no shared mutable state escapes
 *
 * The work supplier returns an exit code. The scheduler logs it but does not
 * interpret it — partial failures (non-zero exit) still leave the loop alive
 * by design, so the next scheduled tick still fires.
 */
public final class SchedulerLoop {

    private static final Logger log = LoggerFactory.getLogger(SchedulerLoop.class);

    private final List<CronSchedule> schedules;
    private final IntSupplier work;
    private final Clock clock;
    private final Object runMutex = new Object();

    private volatile Thread thread;
    private volatile boolean running = false;
    private final AtomicLong lastRunCompletedAtMillis = new AtomicLong(0);

    public SchedulerLoop(List<CronSchedule> schedules, IntSupplier work, Clock clock) {
        if (schedules == null || schedules.isEmpty())
            throw new IllegalArgumentException("at least one CronSchedule is required");
        if (work == null)  throw new IllegalArgumentException("work is required");
        if (clock == null) throw new IllegalArgumentException("clock is required");
        this.schedules = List.copyOf(schedules);
        this.work = work;
        this.clock = clock;
    }

    public synchronized void start() {
        if (running) throw new IllegalStateException("already started");
        running = true;
        thread = new Thread(this::loop, "scheduler-loop");
        thread.setDaemon(false);
        thread.start();
        log.info("scheduler started with {} schedule(s): {}",
                schedules.size(),
                schedules.stream().map(CronSchedule::name).collect(Collectors.joining(",")));
    }

    public synchronized void stop() {
        if (!running) return;
        running = false;
        Thread t = thread;
        if (t != null) t.interrupt();
    }

    /** Liveness signal: scheduler thread exists and is alive. */
    public boolean isAlive() {
        Thread t = thread;
        return t != null && t.isAlive();
    }

    /** For tests / shutdown: wait up to {@code timeoutMs} for the thread to exit. */
    public boolean awaitTermination(long timeoutMs) throws InterruptedException {
        Thread t = thread;
        if (t == null) return true;
        t.join(timeoutMs);
        return !t.isAlive();
    }

    /** Last successful or failed run completion time; 0 if no run has happened yet. */
    public long lastRunCompletedAtMillis() {
        return lastRunCompletedAtMillis.get();
    }

    /**
     * The earliest fire instant across all schedules, strictly after {@code from}.
     * Package-private so tests can verify the calculation independently of the loop.
     */
    Instant computeNextFire(Instant from) {
        return schedules.stream()
                .map(s -> s.nextFireAfter(from))
                .min(Comparator.naturalOrder())
                .orElseThrow();
    }

    /**
     * Run the work once under the mutex. Exceptions are caught, logged, and
     * swallowed so the caller (loop or test) is not affected.
     * Package-private to enable focused tests without running the loop.
     */
    void runOnce() {
        synchronized (runMutex) {
            long startedNs = System.nanoTime();
            try {
                int exit = work.getAsInt();
                long elapsedMs = (System.nanoTime() - startedNs) / 1_000_000;
                log.info("scheduled run complete: exit={} duration_ms={}", exit, elapsedMs);
            } catch (Throwable t) {
                long elapsedMs = (System.nanoTime() - startedNs) / 1_000_000;
                log.warn("scheduled run failed after {} ms: {}", elapsedMs, t.getMessage(), t);
            } finally {
                lastRunCompletedAtMillis.set(clock.millis());
            }
        }
    }

    private void loop() {
        while (running) {
            Instant now = clock.instant();
            Instant next;
            try {
                next = computeNextFire(now);
            } catch (Throwable t) {
                log.error("failed to compute next-fire; scheduler stopping: {}", t.getMessage(), t);
                break;
            }
            long sleepMs = Math.max(0, Duration.between(now, next).toMillis());
            log.info("next scheduled run at {} (sleeping {} ms)", next, sleepMs);
            try {
                if (sleepMs > 0) Thread.sleep(sleepMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                if (!running) break;
                log.warn("scheduler sleep interrupted unexpectedly; continuing");
            }
            if (!running) break;
            runOnce();
        }
        log.info("scheduler stopped");
    }
}
