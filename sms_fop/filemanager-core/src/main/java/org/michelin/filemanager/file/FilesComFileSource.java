package org.michelin.filemanager.file;

import org.michelin.filemanager.config.Config;
import org.michelin.filemanager.config.ConfigValidationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * {@link FileSource} backed by files.com (HTTPS / native Java SDK).
 *
 * <p>The remote layout mirrors the local/sftp backends:
 * <pre>
 *   pickupPath/   ← drop zone (operator polls here)
 *   archivePath/  ← success destination
 *   rejectPath/   ← failure destination
 * </pre>
 *
 * <p>The {@link FilesComGateway} seam keeps the SDK's static surface out of this
 * class so unit tests can pin behavior without static-mocking.
 */
public class FilesComFileSource implements FileSource {

    private final FilesComGateway gateway;
    private final String pickupPath;
    private final String archivePath;
    private final String rejectPath;
    private final Pattern namePattern;
    private final int pageSize;

    public FilesComFileSource(FilesComGateway gateway,
                              Config.FileConfig.FilesComConfig cfg,
                              Pattern namePattern) {
        if (gateway == null) {
            throw new ConfigValidationException("FilesComFileSource requires a gateway");
        }
        if (cfg == null) {
            throw new ConfigValidationException("file.filescom block is missing");
        }
        if (cfg.pickupPath() == null || cfg.pickupPath().isBlank()) {
            throw new ConfigValidationException("file.filescom.pickup_path is required");
        }
        if (cfg.archivePath() == null || cfg.archivePath().isBlank()) {
            throw new ConfigValidationException("file.filescom.archive_path is required");
        }
        if (cfg.rejectPath() == null || cfg.rejectPath().isBlank()) {
            throw new ConfigValidationException("file.filescom.reject_path is required");
        }
        this.gateway = gateway;
        this.pickupPath  = trimTrailingSlash(cfg.pickupPath());
        this.archivePath = trimTrailingSlash(cfg.archivePath());
        this.rejectPath  = trimTrailingSlash(cfg.rejectPath());
        this.namePattern = namePattern;
        this.pageSize    = cfg.pageSize();
    }

    @Override
    public List<SourceFile> list() throws IOException {
        List<FilesComGateway.RemoteEntry> entries = gateway.listFolder(pickupPath, pageSize);
        List<SourceFile> out = new ArrayList<>();
        for (FilesComGateway.RemoteEntry e : entries) {
            if (e.isFolder()) continue;
            if (!namePattern.matcher(e.name()).matches()) continue;
            out.add(new SourceFile(e.path(), e.name(), e.sizeBytes()));
        }
        out.sort(Comparator.comparing(SourceFile::name));
        return out;
    }

    @Override
    public Path download(SourceFile file, Path dest) throws IOException {
        Files.createDirectories(dest.getParent() != null ? dest.getParent() : dest);
        gateway.download(file.id(), dest);
        return dest;
    }

    @Override
    public void moveToArchive(SourceFile file, String newName) throws IOException {
        move(file, archivePath, newName);
    }

    @Override
    public void moveToReject(SourceFile file, String newName) throws IOException {
        move(file, rejectPath, newName);
    }

    private void move(SourceFile file, String dir, String newName) throws IOException {
        String target = dir + "/" + (newName != null ? newName : file.name());
        gateway.move(file.id(), target);
    }

    private static String trimTrailingSlash(String s) {
        if (s == null) return null;
        return s.endsWith("/") && s.length() > 1 ? s.substring(0, s.length() - 1) : s;
    }
}
