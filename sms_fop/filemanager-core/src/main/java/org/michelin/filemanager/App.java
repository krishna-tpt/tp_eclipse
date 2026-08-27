package org.michelin.filemanager;

import org.michelin.filemanager.audit.EventLogWriter;
import org.michelin.filemanager.audit.HeartbeatEmitter;
import org.michelin.filemanager.config.Config;
import org.michelin.filemanager.config.ConfigLoader;
import org.michelin.filemanager.config.ConfigValidationException;
import org.michelin.filemanager.db.Database;
import org.michelin.filemanager.health.HealthEndpoint;
import org.michelin.filemanager.health.HealthStatus;
import org.michelin.filemanager.lifecycle.ExitCode;
import org.michelin.filemanager.scheduler.CronSchedule;
import org.michelin.filemanager.scheduler.SchedulerLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Bootstrap. Branches on {@link Config.RunMode}:
 *   ONESHOT   — original ADR-0003 batch behavior: one pipeline pass, exit.
 *   SCHEDULED — ADR-0014: long-running JVM with internal cron scheduler +
 *               /actuator/health HTTP server. SIGTERM triggers graceful
 *               shutdown via the JVM shutdown hook.
 *
 * Tests call {@link #run(String[], Map)} which always takes the oneshot path
 * regardless of the configured run_mode — keeps test semantics unchanged.
 */
public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);
    private static final long SCHEDULER_SHUTDOWN_GRACE_MS = 60_000;
    private static final Duration HEARTBEAT_INTERVAL = Duration.ofSeconds(60);

    public static void main(String[] args) {
        Map<String, String> env = System.getenv();
        Config cfg;
        try {
            cfg = new ConfigLoader().load(env);
        } catch (ConfigValidationException e) {
            log.error("config error: {}", e.getMessage());
            System.exit(ExitCode.CONFIG_ERROR.code());
            return;
        }

        if (cfg.runMode() == Config.RunMode.SCHEDULED) {
            System.exit(runScheduled(cfg, env));
        } else {
            System.exit(new Application(env).run());
        }
    }

    /** Test-friendly entry: always oneshot, regardless of configured run_mode. */
    public static int run(String[] args, Map<String, String> env) {
        return new Application(env).run();
    }

    /**
     * Long-running mode. Builds the scheduler + health endpoint, starts them,
     * parks the main thread until SIGTERM triggers the shutdown hook. The
     * shutdown hook is responsible for stopping everything cleanly.
     */
    private static int runScheduled(Config cfg, Map<String, String> env) {
        List<CronSchedule> schedules = buildSchedules(cfg);
        if (schedules.isEmpty()) {
            log.error("RUN_MODE=scheduled requires at least one of SCHEDULE_DAILY or SCHEDULE_HOURLY to be set");
            return ExitCode.CONFIG_ERROR.code();
        }

        // Long-lived DB connection for the health probe only. Each scheduled
        // run still opens its own connection inside Application.run() — keeps
        // the existing transaction lifecycle untouched.
        Database healthDb;
        try {
            healthDb = new Database(cfg.db());
        } catch (Exception e) {
            log.error("failed to open health DB connection: {}", e.getMessage(), e);
            return ExitCode.DB_ERROR.code();
        }

        SchedulerLoop scheduler = new SchedulerLoop(
                schedules,
                () -> new Application(env).run(),
                Clock.systemDefaultZone()
        );

        // Heartbeat is independent of cron — cron ticks can be hours apart, but
        // we want a fine-grained "service alive" signal in the event stream.
        EventLogWriter eventLog = new EventLogWriter(healthDb);
        HeartbeatEmitter heartbeat = new HeartbeatEmitter(eventLog::heartbeat, HEARTBEAT_INTERVAL);

        HealthEndpoint health = new HealthEndpoint(cfg.health().host(), cfg.health().port())
                .addCheck("db",        () -> pingDb(healthDb))
                .addCheck("scheduler", () -> scheduler.isAlive()
                        ? HealthStatus.ComponentResult.passing()
                        : HealthStatus.ComponentResult.failing("scheduler thread not alive"));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("shutdown signal received; stopping scheduler, heartbeat, and health endpoint");
            heartbeat.close();
            scheduler.stop();
            try {
                if (!scheduler.awaitTermination(SCHEDULER_SHUTDOWN_GRACE_MS)) {
                    log.warn("scheduler did not terminate within {} ms — exiting anyway",
                            SCHEDULER_SHUTDOWN_GRACE_MS);
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            health.stop();
            healthDb.close();
            log.info("shutdown complete");
        }, "shutdown-hook"));

        try {
            health.start();
            scheduler.start();
            heartbeat.start();
        } catch (Exception e) {
            log.error("startup failed: {}", e.getMessage(), e);
            return ExitCode.UNKNOWN_ERROR.code();
        }

        log.info("RUN_MODE=scheduled with {} schedule(s); pod alive — awaiting SIGTERM",
                schedules.size());

        try {
            new CountDownLatch(1).await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.info("main thread interrupted; exiting");
        }
        return ExitCode.SUCCESS.code();
    }

    static List<CronSchedule> buildSchedules(Config cfg) {
        ZoneId zone = ZoneId.systemDefault();
        List<CronSchedule> out = new ArrayList<>();
        String daily  = cfg.schedule().dailyCron();
        String hourly = cfg.schedule().hourlyCron();
        if (daily  != null && !daily.isBlank())
            out.add(new CronSchedule("daily",  daily,  zone));
        if (hourly != null && !hourly.isBlank())
            out.add(new CronSchedule("hourly", hourly, zone));
        return out;
    }

    /** SELECT 1 against the health DB connection. Reports DOWN on any SQLException. */
    private static HealthStatus.ComponentResult pingDb(Database healthDb) {
        try (PreparedStatement ps = healthDb.connection().prepareStatement("SELECT 1");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getInt(1) == 1) {
                return HealthStatus.ComponentResult.passing();
            }
            return HealthStatus.ComponentResult.failing("SELECT 1 returned unexpected result");
        } catch (SQLException e) {
            return HealthStatus.ComponentResult.failing(e.getMessage() == null
                    ? e.getClass().getSimpleName() : e.getMessage());
        }
    }
}
