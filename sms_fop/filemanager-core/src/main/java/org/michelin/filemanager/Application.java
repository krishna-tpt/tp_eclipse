package org.michelin.filemanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.michelin.filemanager.audit.EventLogWriter;
import org.michelin.filemanager.catalog.Catalog;
import org.michelin.filemanager.catalog.CatalogLoader;
import org.michelin.filemanager.config.Config;
import org.michelin.filemanager.config.ConfigLoader;
import org.michelin.filemanager.db.Database;
import org.michelin.filemanager.db.FlywayMigrator;
import org.michelin.filemanager.file.DefaultFileSourceFactory;
import org.michelin.filemanager.file.FileSource;
import org.michelin.filemanager.file.FileSourceFactory;
import org.michelin.filemanager.ingest.CatalogFileLoader;
import org.michelin.filemanager.ingest.CatalogIngestPipeline;
import org.michelin.filemanager.ingest.IngestRunResult;
import org.michelin.filemanager.lifecycle.ExitCode;
import org.michelin.filemanager.lifecycle.ExitCodeMapper;
import org.michelin.filemanager.notifier.MultiUrlNotifier;
import org.michelin.filemanager.notifier.Notifier;
import org.michelin.filemanager.notifier.OutboxDrainer;
import org.michelin.filemanager.notifier.OutboxRepository;
import org.michelin.filemanager.notifier.SingleUrlWebhookNotifier;
import org.michelin.filemanager.notifier.StatusTransitionPolicy;
import org.michelin.filemanager.pipeline.Archiver;
import org.michelin.filemanager.pipeline.NotificationEmitter;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Composition root for one run. All collaborators wired in the constructor;
 * {@link #run()} is the small orchestrated entry point.
 *
 * <p>Pipeline (catalog-driven, replacing the retired CSV-header chain):
 * <pre>
 *   load Config → open DB → migrate (Flyway)
 *     → load catalog YAMLs → build FileSource
 *     → CatalogIngestPipeline (variant detect, parse, map, footer-validate, INSERT)
 *     → CatalogFileLoader (list, download, dedupe, transaction, archive/reject, notify)
 *     → drain notification outbox
 *     → return exit code
 * </pre>
 */
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private final Map<String, String> env;
    private final Clock clock;
    private final FileSourceFactory fileSourceFactory;
    private final ExitCodeMapper exitCodeMapper;

    public Application(Map<String, String> env) {
        this(env, Clock.systemDefaultZone(), new DefaultFileSourceFactory(), new ExitCodeMapper());
    }

    /** Full-DI constructor; used by tests to inject fake clock/factories. */
    public Application(Map<String, String> env,
                       Clock clock,
                       FileSourceFactory fileSourceFactory,
                       ExitCodeMapper exitCodeMapper) {
        this.env = env;
        this.clock = clock;
        this.fileSourceFactory = fileSourceFactory;
        this.exitCodeMapper = exitCodeMapper;
    }

    public int run() {
        Config cfg;
        try {
            cfg = new ConfigLoader().load(env);
        } catch (org.michelin.filemanager.config.ConfigValidationException e) {
            log.error("config error: {}", e.getMessage());
            return ExitCode.CONFIG_ERROR.code();
        }

        Database[] dbRef = { null };
        Thread shutdownHook = Thread.ofPlatform().name("inventoryledger-shutdown").unstarted(() -> {
            Database d = dbRef[0];
            if (d != null) {
                log.warn("shutdown signal received; closing DB connection");
                d.close();
            }
        });
        Runtime.getRuntime().addShutdownHook(shutdownHook);

        try (Database db = new Database(cfg.db())) {
            dbRef[0] = db;
            // Schema ownership lives with DBServices (deploy/<tag>/customer_install.sql).
            // The JAR runs its bundled Flyway migrations only when explicitly enabled
            // — used by dev/IT bootstraps. In customer environments the schema is
            // pre-installed and FLYWAY_ENABLED must be false to avoid touching it.
            if (cfg.flyway().enabled()) {
                new FlywayMigrator(cfg.db(), cfg.flyway()).migrate();
            } else {
                log.info("Flyway migrations skipped (flyway.enabled=false)");
            }

            FileSource fileSource;
            try {
                fileSource = fileSourceFactory.create(cfg.file());
            } catch (Exception e) {
                log.error("file source error: {}", e.getMessage(), e);
                return ExitCode.FILE_SOURCE_ERROR.code();
            }

            CatalogFileLoader loader = wireLoader(cfg, db, fileSource);
            IngestRunResult result = loader.run();
            log.info("file run complete: processed={} archived={} rejected={} skippedDup={} totalRows={}",
                    result.filesProcessed(), result.filesArchived(),
                    result.filesRejected(), result.filesSkippedDup(),
                    result.totalRowsLoaded());

            if (cfg.job().drainOutbox()) {
                wireDrainer(cfg, db).drain();
            }

            return (result.filesRejected() > 0)
                    ? ExitCode.PARTIAL_FAILURE.code()
                    : ExitCode.SUCCESS.code();

        } catch (Exception e) {
            ExitCode code = exitCodeMapper.map(e);
            log.error("{}: {}", code, e.getMessage(), e);
            return code.code();
        } finally {
            try { Runtime.getRuntime().removeShutdownHook(shutdownHook); }
            catch (IllegalStateException ignore) { /* JVM already shutting down */ }
        }
    }

    private CatalogFileLoader wireLoader(Config cfg, Database db, FileSource fileSource) {
        Catalog catalog = new CatalogLoader().loadFromClasspath("interfaces");
        CatalogIngestPipeline ingestPipeline = new CatalogIngestPipeline(catalog);
        Archiver archiver = new Archiver(fileSource, clock, cfg.job().timestampPattern());
        NotificationEmitter emitter = new NotificationEmitter(db);
        EventLogWriter eventLog = new EventLogWriter(db);
        return new CatalogFileLoader(
                fileSource, ingestPipeline, archiver, emitter,
                eventLog, db, cfg.job(), cfg.job().tempFilePrefix(),
                cfg.file().source());
    }

    private OutboxDrainer wireDrainer(Config cfg, Database db) {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(cfg.notifier().connectTimeoutMs()))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        Duration perRequest = Duration.ofMillis(cfg.notifier().timeoutMs());
        List<Notifier> delegates = cfg.notifier().webhookUrls().stream()
                .map(URI::create)
                .map(uri -> (Notifier) new SingleUrlWebhookNotifier(client, uri, perRequest))
                .collect(Collectors.toList());
        Notifier notifier = new MultiUrlNotifier(delegates);
        OutboxRepository repo = new OutboxRepository();
        StatusTransitionPolicy policy = new StatusTransitionPolicy(cfg.notifier().maxRetries());
        return new OutboxDrainer(db, repo, notifier, policy,
                cfg.notifier().maxRetries(),
                cfg.notifier().batchSize());
    }
}
