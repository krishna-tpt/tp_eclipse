package org.michelin.filemanager.audit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TC-350..TC-353 — HeartbeatEmitter behavior.
 *
 * Uses short intervals (millis, not seconds) so each test completes fast
 * without sacrificing the lifecycle correctness it's exercising.
 */
class HeartbeatEmitterTest {

    @Test
    @DisplayName("TC-350: emitter fires at the configured interval until stopped")
    void firesAtInterval() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        CountDownLatch threeBeats = new CountDownLatch(3);
        Runnable beat = () -> { calls.incrementAndGet(); threeBeats.countDown(); };

        try (HeartbeatEmitter emitter = new HeartbeatEmitter(beat, Duration.ofMillis(20))) {
            emitter.start();
            // Should hit 3 beats within a second on any sane CI box.
            assertThat(threeBeats.await(1, TimeUnit.SECONDS)).isTrue();
        }
        assertThat(calls.get()).isGreaterThanOrEqualTo(3);
    }

    @Test
    @DisplayName("TC-351: close() stops the schedule promptly")
    void closeStops() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        Runnable beat = calls::incrementAndGet;

        HeartbeatEmitter emitter = new HeartbeatEmitter(beat, Duration.ofMillis(10));
        emitter.start();
        Thread.sleep(50);
        emitter.close();

        int afterClose = calls.get();
        Thread.sleep(80);   // any further beats here would be a regression
        assertThat(calls.get()).isEqualTo(afterClose);
    }

    @Test
    @DisplayName("TC-352: a beat that throws does not kill the schedule")
    void thrownBeatDoesNotKillSchedule() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        CountDownLatch threeBeats = new CountDownLatch(3);
        Runnable badBeat = () -> {
            calls.incrementAndGet();
            threeBeats.countDown();
            throw new RuntimeException("boom");
        };

        try (HeartbeatEmitter emitter = new HeartbeatEmitter(badBeat, Duration.ofMillis(20))) {
            emitter.start();
            assertThat(threeBeats.await(1, TimeUnit.SECONDS)).isTrue();
        }
        assertThat(calls.get()).isGreaterThanOrEqualTo(3);
    }

    @Test
    @DisplayName("TC-353: invalid arguments rejected at construction")
    void invalidArgsRejected() {
        assertThatThrownBy(() -> new HeartbeatEmitter(null, Duration.ofMillis(10)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new HeartbeatEmitter(() -> {}, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new HeartbeatEmitter(() -> {}, Duration.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new HeartbeatEmitter(() -> {}, Duration.ofMillis(-1)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
