package org.michelin.filemanager.ingest;

/**
 * Aggregate outcome of one ingest cycle across all files picked up from the
 * source. Replaces the legacy {@code pipeline.LoadResult} (which sat next to
 * the retired OpeningBalanceLoader chain).
 *
 * <ul>
 *   <li>{@code filesProcessed} — total files inspected this cycle
 *   <li>{@code filesArchived}  — successfully ingested and moved to archive/
 *   <li>{@code filesRejected}  — failed ingest, moved to reject/
 *   <li>{@code filesSkippedDup}— recognized as already-ingested (dedupe hit)
 *   <li>{@code totalRowsLoaded}— sum of rows written to staging across all
 *                                successful files
 * </ul>
 */
public record IngestRunResult(
        int filesProcessed,
        int filesArchived,
        int filesRejected,
        int filesSkippedDup,
        int totalRowsLoaded) {

    public static IngestRunResult empty() {
        return new IngestRunResult(0, 0, 0, 0, 0);
    }

    public IngestRunResult plus(IngestRunResult other) {
        return new IngestRunResult(
                filesProcessed   + other.filesProcessed,
                filesArchived    + other.filesArchived,
                filesRejected    + other.filesRejected,
                filesSkippedDup  + other.filesSkippedDup,
                totalRowsLoaded  + other.totalRowsLoaded);
    }
}
