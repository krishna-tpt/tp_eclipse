package org.michelin.filemanager.config;

import java.util.List;
import java.util.Objects;

/**
 * Immutable, validated config record. Compact constructors enforce invariants
 * so a malformed Config can't exist at runtime. Collections are defensively
 * copied to keep the record genuinely immutable.
 */
public record Config(
        String profile,
        RunMode runMode,
        DbConfig db,
        FlywayConfig flyway,
        FileConfig file,
        NotifierConfig notifier,
        JobConfig job,
        LoggingConfig logging,
        ScheduleConfig schedule,
        HealthConfig health) {

    public Config {
        Objects.requireNonNull(profile,  "profile");
        Objects.requireNonNull(runMode,  "runMode");
        Objects.requireNonNull(db,       "db");
        Objects.requireNonNull(flyway,   "flyway");
        Objects.requireNonNull(file,     "file");
        Objects.requireNonNull(notifier, "notifier");
        Objects.requireNonNull(job,      "job");
        Objects.requireNonNull(logging,  "logging");
        Objects.requireNonNull(schedule, "schedule");
        Objects.requireNonNull(health,   "health");
    }

    /** Chooses between single-shot (legacy ADR-0003) and long-running (ADR-0014). */
    public enum RunMode {
        ONESHOT, SCHEDULED;

        public static RunMode parse(String s) {
            if (s == null || s.isBlank()) return ONESHOT;
            String n = s.trim().toUpperCase();
            try { return RunMode.valueOf(n); }
            catch (IllegalArgumentException e) {
                throw new ConfigValidationException(
                    "run_mode must be 'oneshot' or 'scheduled' (got: " + s + ")");
            }
        }
    }

    public record DbConfig(
            String url,
            String user,
            String password,
            int connectTimeoutS,
            int statementTimeoutMs,
            String applicationName) {

        public DbConfig {
            Objects.requireNonNull(applicationName, "applicationName");
            if (url == null || url.isBlank())
                throw new ConfigValidationException("db.url is required");
            if (connectTimeoutS < 0)
                throw new ConfigValidationException("db.connect_timeout_s must be >= 0");
            if (statementTimeoutMs < 0)
                throw new ConfigValidationException("db.statement_timeout_ms must be >= 0");
        }

        @Override
        public String toString() {
            return "DbConfig[url=" + url
                    + ", user=" + user
                    + ", password=***"
                    + ", connectTimeoutS=" + connectTimeoutS
                    + ", statementTimeoutMs=" + statementTimeoutMs
                    + ", applicationName=" + applicationName + "]";
        }
    }

    public record FlywayConfig(
            boolean enabled,
            String locations,
            boolean baselineOnMigrate,
            boolean validateOnMigrate) {

        public FlywayConfig {
            if (locations == null || locations.isBlank())
                throw new ConfigValidationException("flyway.locations is required");
        }
    }

    public record FileConfig(
            String source,
            String namePattern,
            SftpConfig sftp,
            LocalConfig local,
            FilesComConfig filescom) {

        public FileConfig {
            if (source == null || source.isBlank())
                throw new ConfigValidationException("file.source is required (local|sftp|filescom)");
            if (namePattern == null || namePattern.isBlank())
                throw new ConfigValidationException("file.name_pattern is required");
        }

        public record SftpConfig(
                String host,
                int port,
                String username,
                String password,
                String privateKeyPath,
                String privateKeyPassphrase,
                String knownHostsPath,
                String pickupPath,
                String archivePath,
                String rejectPath,
                int connectTimeoutMs) {
            @Override
            public String toString() {
                return "SftpConfig[host=" + host
                        + ", port=" + port
                        + ", username=" + username
                        + ", password=***"
                        + ", privateKeyPath=" + privateKeyPath
                        + ", privateKeyPassphrase=***"
                        + ", knownHostsPath=" + knownHostsPath
                        + ", pickupPath=" + pickupPath
                        + ", archivePath=" + archivePath
                        + ", rejectPath=" + rejectPath
                        + ", connectTimeoutMs=" + connectTimeoutMs + "]";
            }
        }

        public record LocalConfig(
                String pickupPath,
                String archivePath,
                String rejectPath) {

            public LocalConfig {
                Objects.requireNonNull(pickupPath,  "pickupPath");
                Objects.requireNonNull(archivePath, "archivePath");
                Objects.requireNonNull(rejectPath,  "rejectPath");
            }
        }

        /**
         * Files.com (HTTPS / Java SDK) backend. The SDK authenticates via a single
         * API key — passed through {@code apiKey} after env-var resolution. {@code baseUrl}
         * is optional; blank means the SDK default ({@code https://app.files.com}). When this
         * backend is selected, blank apiKey/pickupPath fail loudly at connect time, not here.
         */
        public record FilesComConfig(
                String apiKey,
                String baseUrl,
                String pickupPath,
                String archivePath,
                String rejectPath,
                int connectTimeoutMs,
                int readTimeoutMs,
                int pageSize) {

            public FilesComConfig {
                if (connectTimeoutMs < 0)
                    throw new ConfigValidationException("file.filescom.connect_timeout_ms must be >= 0");
                if (readTimeoutMs < 0)
                    throw new ConfigValidationException("file.filescom.read_timeout_ms must be >= 0");
                if (pageSize < 1)
                    throw new ConfigValidationException("file.filescom.page_size must be >= 1");
            }

            @Override
            public String toString() {
                return "FilesComConfig[apiKey=***"
                        + ", baseUrl=" + baseUrl
                        + ", pickupPath=" + pickupPath
                        + ", archivePath=" + archivePath
                        + ", rejectPath=" + rejectPath
                        + ", connectTimeoutMs=" + connectTimeoutMs
                        + ", readTimeoutMs=" + readTimeoutMs
                        + ", pageSize=" + pageSize + "]";
            }
        }

        public static FileConfig forLocalTesting() {
            return new FileConfig(
                    "local",
                    "^MICH_INV_STOCKLEVEL_413\\d{14}\\.dat$",
                    null,
                    new LocalConfig("./inbound", "./archive", "./rejected"),
                    null);
        }
    }

    public record NotifierConfig(
            List<String> webhookUrls,
            int maxRetries,
            int timeoutMs,
            int retryBackoffMs,
            int batchSize,
            int connectTimeoutMs) {

        public NotifierConfig {
            webhookUrls = (webhookUrls == null) ? List.of() : List.copyOf(webhookUrls);
            if (maxRetries < 0) throw new ConfigValidationException("notifier.max_retries < 0");
            if (timeoutMs < 0) throw new ConfigValidationException("notifier.timeout_ms < 0");
            if (retryBackoffMs < 0) throw new ConfigValidationException("notifier.retry_backoff_ms < 0");
            if (batchSize < 1) throw new ConfigValidationException("notifier.batch_size < 1");
            if (connectTimeoutMs < 0) throw new ConfigValidationException("notifier.connect_timeout_ms < 0");
        }
    }

    public record JobConfig(
            boolean exitOnFirstFailure,
            boolean drainOutbox,
            String tempFilePrefix,
            String timestampPattern) {

        public JobConfig {
            if (tempFilePrefix == null || tempFilePrefix.isBlank())
                throw new ConfigValidationException("job.temp_file_prefix is required");
            if (timestampPattern == null || timestampPattern.isBlank())
                throw new ConfigValidationException("job.timestamp_pattern is required");
        }
    }

    public record LoggingConfig(
            String format,
            String level) {

        public LoggingConfig {
            if (format == null || format.isBlank())
                throw new ConfigValidationException("logging.format is required");
            if (level == null || level.isBlank())
                throw new ConfigValidationException("logging.level is required");
        }
    }

    /**
     * Cron schedules for the long-running mode (ADR-0014). Either string may
     * be blank to disable that schedule. At least one must be non-blank when
     * runMode = SCHEDULED — enforced by SchedulerLoop at startup, not here,
     * so callers using the oneshot mode don't have to populate this.
     */
    public record ScheduleConfig(
            String dailyCron,
            String hourlyCron) {

        public ScheduleConfig {
            // Empty strings are valid (means "disabled"). Null is normalized
            // to empty so downstream parsing has a single shape to deal with.
            if (dailyCron  == null) dailyCron  = "";
            if (hourlyCron == null) hourlyCron = "";
        }

        public boolean isEmpty() {
            return dailyCron.isBlank() && hourlyCron.isBlank();
        }
    }

    /**
     * HTTP server config for /actuator/health (ADR-0014). Only honoured when
     * runMode = SCHEDULED; in oneshot mode the server is never started.
     */
    public record HealthConfig(
            String host,
            int port) {

        public HealthConfig {
            if (host == null || host.isBlank())
                throw new ConfigValidationException("health.host is required");
            if (port < 1 || port > 65535)
                throw new ConfigValidationException("health.port must be 1..65535 (got: " + port + ")");
        }
    }

    // Test builder — production code never calls this. Kept for backward
    // compatibility with existing integration tests; consider moving to a
    // ConfigFixtures helper in src/test/java in a future cleanup.
    public static Builder builderForTest() {
        return new Builder();
    }

    public static final class Builder {
        private String profile = "test";
        private RunMode runMode = RunMode.ONESHOT;
        private DbConfig db;
        private FlywayConfig flyway = new FlywayConfig(true, "classpath:db/migration", false, true);
        private FileConfig file = FileConfig.forLocalTesting();
        private NotifierConfig notifier = new NotifierConfig(List.of("http://localhost:1"), 1, 1000, 100, 10, 5000);
        private JobConfig job = new JobConfig(false, true, "ob-", "yyyyMMdd_HHmmss");
        private LoggingConfig logging = new LoggingConfig("text", "DEBUG");
        private ScheduleConfig schedule = new ScheduleConfig("", "");
        private HealthConfig health = new HealthConfig("127.0.0.1", 8080);

        public Builder profile(String v) { this.profile = v; return this; }
        public Builder runMode(RunMode v) { this.runMode = v; return this; }
        public Builder db(DbConfig v) { this.db = v; return this; }
        public Builder flyway(FlywayConfig v) { this.flyway = v; return this; }
        public Builder file(FileConfig v) { this.file = v; return this; }
        public Builder notifier(NotifierConfig v) { this.notifier = v; return this; }
        public Builder job(JobConfig v) { this.job = v; return this; }
        public Builder logging(LoggingConfig v) { this.logging = v; return this; }
        public Builder schedule(ScheduleConfig v) { this.schedule = v; return this; }
        public Builder health(HealthConfig v) { this.health = v; return this; }

        public Config build() {
            if (db == null) throw new ConfigValidationException("db is required");
            return new Config(profile, runMode, db, flyway, file, notifier, job, logging, schedule, health);
        }
    }
}
