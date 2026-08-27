package org.michelin.filemanager.ingest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.michelin.filemanager.catalog.Catalog;
import org.michelin.filemanager.catalog.DetectionResult;
import org.michelin.filemanager.catalog.InterfaceDefinition;
import org.michelin.filemanager.catalog.InterfaceDefinition.DataRecordSpec;
import org.michelin.filemanager.catalog.InterfaceDefinition.EnvelopeSpec;
import org.michelin.filemanager.catalog.VariantDetector;
import org.michelin.filemanager.mapper.EnvelopeContext;
import org.michelin.filemanager.mapper.FieldMapper;
import org.michelin.filemanager.mapper.MappedRow;
import org.michelin.filemanager.parser.ParsedRecord;
import org.michelin.filemanager.parser.PositionalRecordParser;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * End-to-end orchestrator for one file: detect variant → parse → capture
 * envelope metadata → map data rows → validate footer counts → batch-INSERT
 * to staging. Single DB transaction per file (the caller manages commit /
 * rollback).
 *
 * <p>Per the locked bad-row policy, any parser, mapping, or envelope-count
 * failure throws and the caller rolls back the file, moves it to reject/,
 * and writes to the notification outbox.
 */
public final class CatalogIngestPipeline {

    private static final Logger log = LoggerFactory.getLogger(CatalogIngestPipeline.class);

    private final Catalog catalog;
    private final VariantDetector detector;
    private final StagingWriter writer;

    public CatalogIngestPipeline(Catalog catalog) {
        this(catalog, new VariantDetector(catalog), new StagingWriter());
    }

    public CatalogIngestPipeline(Catalog catalog, VariantDetector detector, StagingWriter writer) {
        this.catalog = catalog;
        this.detector = detector;
        this.writer = writer;
    }

    public IngestResult ingest(Path file, String filename, Connection conn) throws IOException, SQLException {
        DetectionResult dr = detector.detect(filename, file);
        if (dr instanceof DetectionResult.None none) {
            return IngestResult.rejected(filename, "variant detection: " + none.reason());
        }
        if (dr instanceof DetectionResult.Ambiguous amb) {
            return IngestResult.rejected(filename, "variant detection ambiguous: " + amb.reason());
        }
        InterfaceDefinition def = ((DetectionResult.Match) dr).definition();
        log.info("ingesting {} as {}/{}", filename, def.interfaceName(), def.variant());

        EnvelopeContext envCtx       = new EnvelopeContext();
        FieldMapper mapper           = new FieldMapper(def);
        EnvelopeValidator envValid   = new EnvelopeValidator(def);
        PositionalRecordParser parser = new PositionalRecordParser(def.parser());

        // Buffer per target table so multi-record-type variants can coexist.
        Map<String, List<MappedRow>> buffers = new LinkedHashMap<>();

        List<ParsedRecord> records = parser.parseAll(file);
        for (ParsedRecord record : records) {
            EnvelopeValidator.RecordKind kind = envValid.observe(record);
            switch (kind) {
                case ENVELOPE -> {
                    EnvelopeSpec env = def.findEnvelope(record.tag()).orElseThrow();
                    if (record.fieldCount() != env.fields()) {
                        throw new EnvelopeValidationException(
                            "line " + record.lineNumber() + ": envelope '" + env.tag() +
                            "' expected " + env.fields() + " fields, got " + record.fieldCount());
                    }
                    envCtx.capture(env, record);
                }
                case DATA -> {
                    DataRecordSpec spec = def.findRecord(record.tag()).orElseThrow();
                    MappedRow row = mapper.map(spec, record, envCtx);
                    buffers.computeIfAbsent(spec.targetTable(), k -> new ArrayList<>()).add(row);
                }
                case UNKNOWN -> throw new EnvelopeValidationException(
                    "line " + record.lineNumber() + ": unknown record tag '" + record.tag() + "'");
            }
        }

        envValid.validate();

        // Per-file natural-key duplicate check. Opt-in per record type via the
        // catalog YAML's `dedupe_keys:` block; records without it skip this stage.
        // Catches exporter bugs that emit the same stock position twice in one file
        // — those would otherwise land as separate rows in staging and then collide
        // on the processed.stock_balance PK during promotion.
        for (DataRecordSpec spec : def.records()) {
            if (!spec.hasDedupeKeys()) continue;
            List<MappedRow> rows = buffers.getOrDefault(spec.targetTable(), List.of());
            String duplicateReason = findDuplicate(spec, rows);
            if (duplicateReason != null) {
                return IngestResult.rejected(filename, duplicateReason);
            }
        }

        Map<String, Object> bookkeeping = Map.of(
            "file_name",  filename,
            "line_number", 0L      // overwritten per row below
        );

        Map<String, Integer> writtenByTable = new LinkedHashMap<>();
        for (Map.Entry<String, List<MappedRow>> e : buffers.entrySet()) {
            String table = e.getKey();
            List<MappedRow> rows = e.getValue();
            int total = 0;
            // Per-row writes so each row's own line_number rides along in the bookkeeping
            // map. StagingWriter buffers internally; the perf cost is negligible at the
            // file sizes we see (~hundreds of rows). If batches ever grow to 100k+ rows,
            // revisit by teaching StagingWriter to accept per-row bookkeeping.
            for (MappedRow row : rows) {
                Map<String, Object> perRow = new LinkedHashMap<>();
                perRow.put("file_name",   filename);
                perRow.put("line_number", row.lineNumber());
                total += writer.writeBatch(conn, table, List.of(row), perRow);
            }
            writtenByTable.put(table, total);
        }

        return IngestResult.success(filename, def.interfaceName(), def.variant(), writtenByTable);
    }

    /**
     * Detects natural-key collisions among rows mapped for one record type.
     * Builds a key list (catalog-declared column names → row values, in YAML
     * order) for each row and tracks the first line that introduced each key.
     * Returns a human-readable rejection reason on the first collision, or
     * {@code null} when every key is distinct.
     */
    private static String findDuplicate(DataRecordSpec spec, List<MappedRow> rows) {
        Map<List<Object>, Long> firstSeenLine = new HashMap<>();
        for (MappedRow row : rows) {
            List<Object> key = new ArrayList<>(spec.dedupeKeys().size());
            for (String col : spec.dedupeKeys()) {
                key.add(row.values().get(col));
            }
            Long previous = firstSeenLine.putIfAbsent(key, row.lineNumber());
            if (previous != null) {
                return "duplicate row in record '" + spec.tag() + "' at lines " +
                       previous + " and " + row.lineNumber() +
                       " (dedupe_keys " + spec.dedupeKeys() + " = " + key + ")";
            }
        }
        return null;
    }
}
