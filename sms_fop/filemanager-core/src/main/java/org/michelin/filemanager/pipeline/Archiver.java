package org.michelin.filemanager.pipeline;

import org.michelin.filemanager.exception.FileSourceException;
import org.michelin.filemanager.file.FileSource;
import org.michelin.filemanager.file.SourceFile;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Disposes a file by moving it to archive or reject with a timestamped name.
 * Clock is injected so tests are deterministic.
 */
public class Archiver {

    private final FileSource source;
    private final Clock clock;
    private final DateTimeFormatter stampFormatter;

    public Archiver(FileSource source, Clock clock, String timestampPattern) {
        this.source = source;
        this.clock = clock;
        this.stampFormatter = DateTimeFormatter.ofPattern(timestampPattern);
    }

    public void archive(SourceFile file) {
        try {
            source.moveToArchive(file, stamp() + "_" + file.name());
        } catch (IOException e) {
            throw new FileSourceException("archive failed for " + file.name(), e);
        }
    }

    public void reject(SourceFile file) {
        try {
            source.moveToReject(file, stamp() + "_" + file.name());
        } catch (IOException e) {
            throw new FileSourceException("reject failed for " + file.name(), e);
        }
    }

    private String stamp() {
        return LocalDateTime.now(clock).format(stampFormatter);
    }
}
