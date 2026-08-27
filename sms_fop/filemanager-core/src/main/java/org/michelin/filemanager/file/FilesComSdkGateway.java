package org.michelin.filemanager.file;

import com.files.FilesClient;
import com.files.models.File;
import com.files.models.Folder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.michelin.filemanager.config.Config;
import org.michelin.filemanager.config.ConfigValidationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Production gateway against the {@code com.files} SDK. The SDK uses static
 * fields for credentials and base URL — we set them once in the constructor and
 * trust that this MicroService runs as one tenant of files.com. If that ever
 * changes (multi-account in-process), this is the seam to revisit.
 */
public class FilesComSdkGateway implements FilesComGateway {

    private static final Logger log = LoggerFactory.getLogger(FilesComSdkGateway.class);

    public FilesComSdkGateway(Config.FileConfig.FilesComConfig cfg) {
        if (cfg == null) {
            throw new ConfigValidationException(
                "file.filescom block is missing — required when file.source=filescom");
        }
        if (cfg.apiKey() == null || cfg.apiKey().isBlank()) {
            throw new ConfigValidationException(
                "file.filescom.api_key is required (env FILES_COM_API_KEY) — refusing to connect anonymously");
        }
        FilesClient.apiKey = cfg.apiKey();
        if (cfg.baseUrl() != null && !cfg.baseUrl().isBlank()) {
            FilesClient.setProperty("apiRoot", cfg.baseUrl());
        }
        FilesClient.setProperty("connectTimeout", String.valueOf(cfg.connectTimeoutMs()));
        FilesClient.setProperty("readTimeout",    String.valueOf(cfg.readTimeoutMs()));
        log.info("FilesComSdkGateway ready (baseUrl={})",
                 cfg.baseUrl() == null || cfg.baseUrl().isBlank() ? "<sdk-default>" : cfg.baseUrl());
    }

    @Override
    public List<RemoteEntry> listFolder(String folderPath, int pageSize) throws IOException {
        HashMap<String, Object> params = new HashMap<>();
        params.put("per_page", pageSize);
        try {
            List<RemoteEntry> out = new ArrayList<>();
            for (File f : Folder.listFor(folderPath, params, null).listAutoPaging()) {
                boolean isFolder = "directory".equalsIgnoreCase(f.type);
                long size = f.size == null ? 0L : f.size;
                out.add(new RemoteEntry(f.path, basename(f.path), size, isFolder));
            }
            return out;
        } catch (RuntimeException e) {
            throw new IOException("files.com list failed for " + folderPath + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void download(String remotePath, Path dest) throws IOException {
        try {
            Files.createDirectories(dest.getParent() != null ? dest.getParent() : dest);
            File metadata = File.download(remotePath, null, null);
            metadata.saveAsLocalFile(dest.toString());
        } catch (RuntimeException e) {
            throw new IOException("files.com download failed for " + remotePath + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void move(String remotePath, String destPath) throws IOException {
        HashMap<String, Object> params = new HashMap<>();
        params.put("destination", destPath);
        params.put("overwrite", true);
        try {
            File.move(remotePath, params, null);
        } catch (RuntimeException e) {
            throw new IOException(
                "files.com move " + remotePath + " -> " + destPath + " failed: " + e.getMessage(), e);
        }
    }

    private static String basename(String path) {
        if (path == null) return "";
        int slash = path.lastIndexOf('/');
        return slash < 0 ? path : path.substring(slash + 1);
    }
}
