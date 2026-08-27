package org.michelin.filemanager.scheduler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TC-310..TC-316 — SchedulerLoop behavior. Mix of pure-function tests
 * (computeNextFire, runOnce mutex / failure isolation) and short
 * lifecycle integration (start, stop, await termination).
 *
 * Where the loop's sleep would otherwise run for minutes, we use a
 * far-future cron and verify lifecycle without ever firing — keeps the
 * test suite fast and deterministic.
 */
class SchedulerLoopTest {

    @Test
    @DisplayName("TC-310: computeNextFire picks the earliest across all schedules")
    void computeNextFire_picksEarliest() {
        CronSchedule daily  = new CronSchedule("daily",  "0 6 * * *",  ZoneOffset.UTC);
        CronSchedule hourly = new CronSchedule("hourly", "5 * * * *",  ZoneOffset.UTC);
        SchedulerLoop loop = new SchedulerLoop(List.of(daily, hourly), () -> 0, fixedClock());

        // Pick a time so hourly fires before daily.
        Instant from = LocalDateTime.of(2026, 6, 23, 14, 3).toInstant(ZoneOffset.UTC);
        Instant expected = LocalDateTime.of(2026, 6, 23, 14, 5).toInstant(ZoneOffset.UTC);

        assertThat(loop.computeNextFire(from)).isEqualTo(expected);
    }

    @Test
    @DisplayName("TC-311: empty schedule list rejected at construction")
    void emptySchedules_rejected() {
        assertThatThrownBy(() -> new SchedulerLoop(List.of(), () -> 0, fixedClock()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("TC-312: runOnce invokes work and updates lastRunCompletedAt")
    void runOnce_invokesWorkAndUpdatesTimestamp() {
        AtomicInteger calls = new AtomicInteger();
        SchedulerLoop loop = new SchedulerLoop(
                List.of(farFutureSchedule()),
                () -> { calls.incrementAndGet(); return 0; },
                fixedClock());

        loop.runOnce();

        assertThat(calls.get()).isEqualTo(1);
        assertThat(loop.lastRunCompletedAtMillis()).isGreaterThan(0);
    }

    @Test
    @DisplayName("TC-313: runOnce swallows thrown exceptions so the loop survives")
    void runOnce_swallowsExceptions() {
        SchedulerLoop loop = new SchedulerLoop(
                List.of(farFutureSchedule()),
                () -> { throw new RuntimeException("boom"); },
                fixedClock());

        // Should not throw.
        loop.runOnce();

        // Timestamp still recorded even on failure — proves the finally block ran.
        assertThat(loop.lastRunCompletedAtMillis()).isGreaterThan(0);
    }

    @Test
    @DisplayName("TC-314: concurrent runOnce calls serialize via the mutex")
    void runOnce_serializesViaMutex() throws Exception {
        CountDownLatch insideFirstRun = new CountDownLatch(1);
        CountDownLatch releaseFirstRun = new CountDownLatch(1);
        AtomicInteger overlapDetected = new AtomicInteger();
        AtomicInteger inFlight = new AtomicInteger();

        SchedulerLoop loop = new SchedulerLoop(
                List.of(farFutureSchedule()),
                () -> {
                    int now = inFlight.incrementAndGet();
                    if (now > 1) overlapDetected.incrementAndGet();
                    insideFirstRun.countDown();
                    try { releaseFirstRun.await(2, TimeUnit.SECONDS); }
                    catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    inFlight.decrementAndGet();
                    return 0;
                },
                fixedClock());

        // Caller A holds the mutex...
        Thread a = new Thread(loop::runOnce, "test-runner-a");
        a.start();
        assertThat(insideFirstRun.await(2, TimeUnit.SECONDS)).isTrue();

        // ...caller B must block on the mutex until A finishes.
        AtomicReference<Long> bStarted = new AtomicReference<>();
        AtomicReference<Long> bFinished = new AtomicReference<>();
        Thread b = new Thread(() -> {
            bStarted.set(System.nanoTime());
            loop.runOnce();
            bFinished.set(System.nanoTime());
        }, "test-runner-b");
        b.start();

        // Give B time to attempt to enter and block.
        Thread.sleep(50);
        // While B is blocked, only A is inside the work.
        assertThat(inFlight.get()).isEqualTo(1);

        releaseFirstRun.countDown();
        a.join(2_000);
        b.join(2_000);

        assertThat(overlapDetected.get()).isZero();
    }

    @Test
    @DisplayName("TC-315: start then stop completes within a short window")
    void startThenStop_isClean() throws InterruptedException {
        SchedulerLoop loop = new SchedulerLoop(
                List.of(farFutureSchedule()),
                () -> 0,
                Clock.systemDefaultZone());

        loop.start();
        assertThat(loop.isAlive()).isTrue();

        loop.stop();
        assertThat(loop.awaitTermination(2_000)).isTrue();
        assertThat(loop.isAlive()).isFalse();
    }

    @Test
    @DisplayName("TC-316: double start throws IllegalStateException")
    void doubleStart_throws() {
        SchedulerLoop loop = new SchedulerLoop(
                List.of(farFutureSchedule()),
                () -> 0,
                Clock.systemDefaultZone());
        loop.start();
        try {
            assertThatThrownBy(loop::start).isInstanceOf(IllegalStateException.class);
        } finally {
            loop.stop();
        }
    }

    // --------------------------------------------------------------
    // helpers
    // --------------------------------------------------------------

    private static Clock fixedClock() {
        // 2026-06-23 14:00:00 UTC — stable point in time.
        return Clock.fixed(
                LocalDateTime.of(2026, 6, 23, 14, 0).toInstant(ZoneOffset.UTC),
                ZoneOffset.UTC);
    }

    /**
     * A cron that fires only on Feb 29 — effectively never within the test
     * window. Lets us start the loop without it actually firing during tests.
     */
    private static CronSchedule farFutureSchedule() {
        return new CronSchedule("never-in-this-test", "0 0 29 2 *", ZoneOffset.UTC);
    }
}
