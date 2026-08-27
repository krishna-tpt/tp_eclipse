package org.michelin.filemanager.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FileSource {
    List<SourceFile> list() throws IOException;
    Path download(SourceFile file, Path dest) throws IOException;
    void moveToArchive(SourceFile file, String newName) throws IOException;
    void moveToReject(SourceFile file, String newName) throws IOException;
}
