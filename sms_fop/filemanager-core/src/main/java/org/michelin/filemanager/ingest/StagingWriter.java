package org.michelin.filemanager.ingest;

import org.michelin.filemanager.mapper.MappedRow;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Writes a batch of {@link MappedRow} into a staging table using
 * PreparedStatement.addBatch(). The pipeline injects bookkeeping columns
 * (file_name, line_number, ingested_at uses DB default) so the SQL is
 * built from (catalog-mapped columns ∪ bookkeeping columns) in declared order.
 *
 * <p>Why not COPY? COPY is faster for huge files but harder to stream from
 * typed Java objects; for Pipeline 1 scale (≤ a few thousand rows per file)
 * batch INSERT is easier to reason about and gives us per-row error positions.
 * Can be promoted to COPY later if profiling justifies it.
 */
public final class StagingWriter {

    public int writeBatch(Connection conn,
                          String targetTable,
                          List<MappedRow> rows,
                          Map<String, Object> bookkeeping) throws SQLException {
        if (rows.isEmpty()) return 0;

        // Take the column shape from the first row; all rows in the batch share
        // the same DataRecordSpec → same column set in the same order.
        List<String> dataCols = new ArrayList<>(rows.get(0).values().keySet());
        List<String> bkCols   = new ArrayList<>(bookkeeping.keySet());

        List<String> allCols  = new ArrayList<>(dataCols);
        allCols.addAll(bkCols);

        String columnList = String.join(", ", allCols);
        String placeholders = String.join(", ", java.util.Collections.nCopies(allCols.size(), "?"));
        String sql = "INSERT INTO " + targetTable + " (" + columnList + ") VALUES (" + placeholders + ")";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (MappedRow row : rows) {
                int idx = 1;
                for (String col : dataCols) bindOne(ps, idx++, row.values().get(col));
                for (String col : bkCols)   bindOne(ps, idx++, bookkeeping.get(col));
                ps.addBatch();
            }
            int[] outcomes = ps.executeBatch();
            int written = 0;
            for (int n : outcomes) if (n >= 0 || n == PreparedStatement.SUCCESS_NO_INFO) written++;
            return written;
        }
    }

    private void bindOne(PreparedStatement ps, int idx, Object value) throws SQLException {
        if (value == null) {
            ps.setNull(idx, Types.NULL);
            return;
        }
        switch (value) {
            case String s            -> ps.setString(idx, s);
            case Long l              -> ps.setLong(idx, l);
            case Integer i           -> ps.setInt(idx, i);
            case BigDecimal d        -> ps.setBigDecimal(idx, d);
            case Boolean b           -> ps.setBoolean(idx, b);
            case LocalDate ld        -> ps.setObject(idx, ld);
            case LocalDateTime ldt   -> ps.setObject(idx, ldt);
            case OffsetDateTime odt  -> ps.setObject(idx, odt);
            default                  -> ps.setObject(idx, value);
        }
    }
}
