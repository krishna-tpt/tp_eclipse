package org.michelin.filemanager.ingest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.michelin.filemanager.audit.EventLogWriter;
import org.michelin.filemanager.config.Config;
import org.michelin.filemanager.db.Database;
import org.michelin.filemanager.exception.DataAccessException;
import org.michelin.filemanager.exception.FileSourceException;
import org.michelin.filemanager.file.FileSource;
import org.michelin.filemanager.file.SourceFile;
import org.michelin.filemanager.notifier.Severity;
import org.michelin.filemanager.pipeline.Archiver;
import org.michelin.filemanager.pipeline.NotificationEmitter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Production orchestrator for one ingest cycle. Wraps {@link CatalogIngestPipeline}
 * with the surrounding concerns:
 * <ul>
 *   <li>List candidate files from {@link FileSource}</li>
 *   <li>Download each to a local temp file</li>
 *   <li>Dedupe — skip files already ingested (by file_name in staging.stocklevel_inbox)</li>
 *   <li>Run the catalog ingest inside a DB transaction; commit or roll back</li>
 *   <li>Archive on success, reject on failure</li>
 *   <li>Emit a notification on every failure</li>
 * </ul>
 *
 * <p>This class replaces the legacy {@code OpeningBalanceLoader} (CSV-header path)
 * and ties together the catalog pipeline with the file-management utilities
 * (Archiver, NotificationEmitter) that survived the retirement.
 *
 * <p>Promotion of staging into the live ledger (calling
 * {@code load_opening_balance(...)} or equivalent) is intentionally NOT here yet —
 * that arrives with task #44 (DBServices promotion function). For now this
 * loader stops at staging and a notification is emitted indicating
 * "staging populated; awaiting promotion".
 */
public final class CatalogFileLoader {

    private static final Logger log = LoggerFactory.getLogger(CatalogFileLoader.class);
    private static final ObjectMapper JSON = new ObjectMapper();

    private final FileSource fileSource;
    private final CatalogIngestPipeline ingestPipeline;
    private final Archiver archiver;
    private final NotificationEmitter notifier;
    private final EventLogWriter eventLog;
    private final Database db;
    private final Config.JobConfig jobConfig;
    private final String tempFilePrefix;
    private final String fileSourceLabel;

    public CatalogFileLoader(FileSource fileSource,
                             CatalogIngestPipeline ingestPipeline,
                             Archiver archiver,
                             NotificationEmitter notifier,
                             EventLogWriter eventLog,
                             Database db,
                             Config.JobConfig jobConfig,
                             String tempFilePrefix,
                             String fileSourceLabel) {
        this.fileSource      = fileSource;
        this.ingestPipeline  = ingestPipeline;
        this.archiver        = archiver;
        this.notifier        = notifier;
        this.eventLog        = eventLog;
        this.db              = db;
        this.jobConfig       = jobConfig;
        this.tempFilePrefix  = tempFilePrefix;
        this.fileSourceLabel = fileSourceLabel;
    }

    public IngestRunResult run() {
        IngestRunResult agg = IngestRunResult.empty();

        List<SourceFile> files;
        try {
            files = fileSource.list();
        } catch (IOException e) {
            throw new FileSourceException("file source listing failed", e);
        }

        for (SourceFile sf : files) {
            // One correlation id per file — propagates through file.picked /
            // file.parsed / file.failed and (later) into the inbox row written
            // by the catalog pipeline, so the whole chain joins on one key.
            String corrId = UUID.randomUUID().toString();
            IngestRunResult one;
            try {
                one = processOne(sf, corrId);
            } catch (Exception e) {
                log.error("file processing failed: {}", sf.name(), e);
                eventLog.fileFailed(sf.name(),
                        e.getClass().getSimpleName(),
                        e.getMessage(),
                        corrId, fileSourceLabel);
                notifier.emit(Severity.ERROR, "catalog_file_loader",
                        "file processing failed: " + sf.name() + " — " + e.getMessage(),
                        JSON.createObjectNode().put("file", sf.name()),
                        "loader.file_failed:" + sf.name());
                one = new IngestRunResult(1, 0, 1, 0, 0);
                if (jobConfig.exitOnFirstFailure()) {
                    return agg.plus(one);
                }
            }
            agg = agg.plus(one);
        }
        return agg;
    }

    // ------------------------------------------------------------------

    private IngestRunResult processOne(SourceFile sf, String correlationId) throws Exception {
        Path tmp = Files.createTempFile(tempFilePrefix, "-" + sf.name());
        long startedAt = System.nanoTime();
        try {
            fileSource.download(sf, tmp);

            long bytes = Files.size(tmp);
            eventLog.filePicked(sf.name(), bytes, correlationId, fileSourceLabel);

            if (alreadyIngested(sf.name())) {
                archiver.archive(sf);
                log.info("duplicate file_name; skipped re-load of {}", sf.name());
                return new IngestRunResult(1, 0, 0, 1, 0);
            }

            IngestResult result = db.inTransaction(c -> {
                try {
                    return ingestPipeline.ingest(tmp, sf.name(), c);
                } catch (IOException ioe) {
                    throw new DataAccessException("ingest IO failed for " + sf.name(), ioe);
                }
            });

            if (result instanceof IngestResult.Rejected r) {
                archiver.reject(sf);
                notifier.emit(Severity.WARN, "catalog_file_loader",
                        "file rejected: " + sf.name() + " — " + r.reason(),
                        JSON.createObjectNode().put("file", sf.name()),
                        "loader.file_rejected:" + sf.name());
                return new IngestRunResult(1, 0, 1, 0, 0);
            }

            IngestResult.Success ok = (IngestResult.Success) result;
            log.info("ingested {} as {}/{}: {} rows",
                    sf.name(), ok.interfaceName(), ok.variant(), ok.totalRowsWritten());

            // Promote staging → live via the DBServices function (V15).
            PromotionSummary promotion = callPromotion(sf.name());
            log.info("promoted {}: batch_id={} accepted={} rejected={} status={}",
                    sf.name(), promotion.batchId(), promotion.accepted(),
                    promotion.rejected(), promotion.status());

            // Audit gate — every staged row must reach processed (either accepted or
            // explicitly rejected by the promotion function). If the totals diverge,
            // load_stocklevel silently dropped rows; we DO NOT archive. The file
            // stays in Inbound as a flag for ops, and the staging dedupe prevents
            // a re-load on the next cycle.
            int promoted = promotion.accepted() + promotion.rejected();
            if (promoted != ok.totalRowsWritten()) {
                String msg = "promotion count mismatch for " + sf.name()
                        + ": staged=" + ok.totalRowsWritten()
                        + " promoted=" + promoted
                        + " (accepted=" + promotion.accepted()
                        + " rejected=" + promotion.rejected() + ")";
                log.error(msg);
                notifier.emit(Severity.ERROR, "catalog_file_loader", msg,
                        JSON.createObjectNode()
                            .put("file",     sf.name())
                            .put("staged",   ok.totalRowsWritten())
                            .put("promoted", promoted)
                            .put("accepted", promotion.accepted())
                            .put("rejected", promotion.rejected())
                            .put("batch_id", promotion.batchId()),
                        "loader.promotion_mismatch:" + sf.name());
                return new IngestRunResult(1, 0, 1, 0, 0);
            }

            archiver.archive(sf);
            int latencyMs = (int) ((System.nanoTime() - startedAt) / 1_000_000);
            eventLog.fileParsed(sf.name(),
                    ok.totalRowsWritten(),
                    latencyMs,
                    correlationId, fileSourceLabel);
            notifier.emit(Severity.INFO, "catalog_file_loader",
                    "ingested + promoted: " + sf.name() +
                    " (staged=" + ok.totalRowsWritten() +
                    ", live_accepted=" + promotion.accepted() +
                    ", live_rejected=" + promotion.rejected() + ")",
                    JSON.createObjectNode()
                        .put("file",          sf.name())
                        .put("staged",        ok.totalRowsWritten())
                        .put("batch_id",      promotion.batchId())
                        .put("live_accepted", promotion.accepted())
                        .put("live_rejected", promotion.rejected())
                        .put("status",        promotion.status()),
                    "loader.promoted:" + sf.name());

            return new IngestRunResult(1, 1, 0, 0, ok.totalRowsWritten());
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    /**
     * Dedupe by file_name appearance in the catalog's stocklevel staging table.
     * Simple, robust enough — the SFTP layer is responsible for not delivering
     * the same name twice in one cycle; this guards against a re-poll after a
     * crash mid-cycle.
     */
    private boolean alreadyIngested(String fileName) {
        String sql = "SELECT EXISTS (SELECT 1 FROM staging.stocklevel_inbox WHERE file_name = ?)";
        try (PreparedStatement ps = db.connection().prepareStatement(sql)) {
            ps.setString(1, fileName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException("dedupe check failed for " + fileName, e);
        }
    }

    /**
     * Calls the DBServices promotion function load_stocklevel(file_name) and
     * returns the summary. Runs in its own auto-commit so a partial promotion
     * is durable even if a later step (archive) fails.
     */
    private PromotionSummary callPromotion(String fileName) {
        String sql = "SELECT batch_id, rows_accepted, rows_rejected, status FROM load_stocklevel(?)";
        try (PreparedStatement ps = db.connection().prepareStatement(sql)) {
            ps.setString(1, fileName);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new DataAccessException("load_stocklevel returned no row for " + fileName);
                }
                return new PromotionSummary(
                        rs.getLong("batch_id"),
                        rs.getInt("rows_accepted"),
                        rs.getInt("rows_rejected"),
                        rs.getString("status"));
            }
        } catch (SQLException e) {
            throw new DataAccessException("load_stocklevel failed for " + fileName, e);
        }
    }

    private record PromotionSummary(long batchId, int accepted, int rejected, String status) {}
}
