package org.michelin.filemanager.file;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LocalFolderFileSource implements FileSource {

    private final Path pickupPath;
    private final Path archivePath;
    private final Path rejectPath;
    private final Pattern namePattern;

    public LocalFolderFileSource(Path pickupPath, Path archivePath, Path rejectPath, Pattern namePattern) {
        this.pickupPath = pickupPath;
        this.archivePath = archivePath;
        this.rejectPath = rejectPath;
        this.namePattern = namePattern;
    }

    @Override
    public List<SourceFile> list() throws IOException {
        List<SourceFile> out = new ArrayList<>();
        if (!Files.isDirectory(pickupPath)) return out;
        try (Stream<Path> entries = Files.list(pickupPath)) {
            // Sort by name for deterministic ordering across runs.
            entries.filter(Files::isRegularFile)
                   .filter(p -> namePattern.matcher(p.getFileName().toString()).matches())
                   .sorted(Comparator.comparing(p -> p.getFileName().toString()))
                   .forEach(p -> {
                       try {
                           out.add(new SourceFile(
                                   p.toAbsolutePath().toString(),
                                   p.getFileName().toString(),
                                   Files.size(p)));
                       } catch (IOException e) {
                           throw new UncheckedIOException(e);
                       }
                   });
        }
        return out;
    }

    @Override
    public Path download(SourceFile file, Path dest) throws IOException {
        Path source = Path.of(file.id());
        Files.createDirectories(dest.getParent() != null ? dest.getParent() : dest);
        Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
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

    private void move(SourceFile file, Path dir, String newName) throws IOException {
        Files.createDirectories(dir);
        Path target = dir.resolve(newName != null ? newName : file.name());
        Files.move(Path.of(file.id()), target, StandardCopyOption.REPLACE_EXISTING);
    }
}
