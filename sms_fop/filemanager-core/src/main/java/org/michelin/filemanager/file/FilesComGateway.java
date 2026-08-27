package org.michelin.filemanager.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Narrow seam over the {@code com.files} SDK so {@link FilesComFileSource} can be
 * tested without static mocking. Every method below maps to a single SDK call.
 * The interface speaks paths and bytes — no SDK types leak past this line.
 */
public interface FilesComGateway {

    /** One entry returned by {@link #listFolder(String, int)}. */
    record RemoteEntry(String path, String name, long sizeBytes, boolean isFolder) {}

    /** Lists immediate children of {@code folderPath} on the remote, auto-paginated by {@code pageSize}. */
    List<RemoteEntry> listFolder(String folderPath, int pageSize) throws IOException;

    /** Streams the remote file at {@code remotePath} to the local {@code dest}, replacing it. */
    void download(String remotePath, Path dest) throws IOException;

    /** Moves the remote file at {@code remotePath} to {@code destPath} on the remote. */
    void move(String remotePath, String destPath) throws IOException;
}
