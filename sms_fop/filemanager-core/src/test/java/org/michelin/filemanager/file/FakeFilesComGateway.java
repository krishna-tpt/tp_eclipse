package org.michelin.filemanager.file;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory {@link FilesComGateway} used by {@link FilesComFileSourceTest}.
 *
 * <p>Holds a flat path → bytes map and a folder index. Mirrors files.com's path
 * model: paths are absolute strings rooted at {@code /}, immediate children are
 * everything one slash deeper. No global state; one per test.
 */
class FakeFilesComGateway implements FilesComGateway {

    private final Map<String, byte[]> files = new HashMap<>();
    private final List<String> folders = new ArrayList<>();
    int listCalls, downloadCalls, moveCalls;

    void seedFile(String path, String body) {
        files.put(path, body.getBytes(StandardCharsets.UTF_8));
        ensureParentFolder(path);
    }

    void seedFolder(String path) { folders.add(normalize(path)); }

    boolean exists(String path) { return files.containsKey(path); }

    @Override
    public List<RemoteEntry> listFolder(String folderPath, int pageSize) {
        listCalls++;
        String prefix = normalize(folderPath) + "/";
        List<RemoteEntry> out = new ArrayList<>();
        for (Map.Entry<String, byte[]> e : files.entrySet()) {
            String p = e.getKey();
            if (!p.startsWith(prefix)) continue;
            String rest = p.substring(prefix.length());
            if (rest.contains("/")) continue;            // not an immediate child
            out.add(new RemoteEntry(p, rest, e.getValue().length, false));
        }
        for (String f : folders) {
            if (!f.startsWith(prefix)) continue;
            String rest = f.substring(prefix.length());
            if (rest.isEmpty() || rest.contains("/")) continue;
            out.add(new RemoteEntry(f, rest, 0L, true));
        }
        return out;
    }

    @Override
    public void download(String remotePath, Path dest) throws IOException {
        downloadCalls++;
        byte[] body = files.get(remotePath);
        if (body == null) throw new IOException("not found: " + remotePath);
        Files.createDirectories(dest.getParent() != null ? dest.getParent() : dest);
        Files.write(dest, body);
    }

    @Override
    public void move(String remotePath, String destPath) throws IOException {
        moveCalls++;
        byte[] body = files.remove(remotePath);
        if (body == null) throw new IOException("not found: " + remotePath);
        files.put(destPath, body);
    }

    private static String normalize(String p) {
        if (p == null || p.isEmpty()) return "";
        return p.endsWith("/") && p.length() > 1 ? p.substring(0, p.length() - 1) : p;
    }

    private void ensureParentFolder(String path) {
        int slash = path.lastIndexOf('/');
        if (slash > 0) folders.add(path.substring(0, slash));
    }
}
