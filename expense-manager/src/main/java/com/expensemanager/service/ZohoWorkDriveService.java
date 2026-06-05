package com.expensemanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Zoho WorkDrive API Service.
 * Handles OAuth token refresh, file download, file search, and file upload.
 * All upload operations run asynchronously via ExecutorService.
 */
public class ZohoWorkDriveService {

    private static final Logger log = LoggerFactory.getLogger(ZohoWorkDriveService.class);
    private static final String ACCOUNTS_URL = "https://accounts.zoho.com/oauth/v2/token";
    private static final String WORKDRIVE_API = "https://workdrive.zoho.com/api/v1";

    private final String clientId;
    private final String clientSecret;
    private final String refreshToken;
    private final String folderId;

    // Cached access token
    private volatile String accessToken;
    private volatile long tokenExpiry = 0;

    // Single-thread executor for async uploads (won't overwhelm API)
    private final ExecutorService uploadExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "workdrive-upload");
        t.setDaemon(true);
        return t;
    });

    private final ObjectMapper mapper = new ObjectMapper();

    public ZohoWorkDriveService(String clientId, String clientSecret,
                                 String refreshToken, String folderId) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.refreshToken = refreshToken;
        this.folderId = folderId;
    }

    // ───────────────────────────────────────────────
    // OAuth
    // ───────────────────────────────────────────────

    /**
     * Returns a valid access token, refreshing if expired (with 60s buffer).
     */
    public synchronized String getAccessToken() throws Exception {
        if (accessToken != null && System.currentTimeMillis() < tokenExpiry - 60_000) {
            return accessToken;
        }
        log.info("Refreshing Zoho access token...");
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(ACCOUNTS_URL);
            String body = "grant_type=refresh_token"
                    + "&client_id=" + clientId
                    + "&client_secret=" + clientSecret
                    + "&refresh_token=" + refreshToken;
            post.setEntity(new StringEntity(body, ContentType.APPLICATION_FORM_URLENCODED));

            String response = client.execute(post, httpResponse ->
                    EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8));
            JsonNode json = mapper.readTree(response);

            if (json.has("error")) {
                throw new RuntimeException("Token refresh failed: " + json.get("error").asText());
            }
            accessToken = json.get("access_token").asText();
            long expiresIn = json.has("expires_in") ? json.get("expires_in").asLong() : 3600;
            tokenExpiry = System.currentTimeMillis() + (expiresIn * 1000);
            log.info("Token refreshed. Expires in {}s", expiresIn);
            return accessToken;
        }
    }

    // ───────────────────────────────────────────────
    // File Operations
    // ───────────────────────────────────────────────

    /**
     * Find the file ID of the Excel file in the configured folder.
     * Returns null if not found.
     */
    public String findFileId(String filename) throws Exception {
        String token = getAccessToken();
        String url = WORKDRIVE_API + "/files/" + folderId + "/files";
        log.info("url {}",url);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(url);
            get.setHeader("Authorization", "Zoho-oauthtoken " + token);
            get.setHeader("Accept", "application/json");

            String response = client.execute(get, httpResponse ->
                    EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8));
            JsonNode json = mapper.readTree(response);

            JsonNode data = json.path("data");
            log.info("Payload {}",data);
            if (data.isArray()) {
                for (JsonNode file : data) {
                    String name = file.path("attributes").path("name").asText();
                    if (filename.equalsIgnoreCase(name)) {
                        return file.path("id").asText();
                    }
                }
            }
        }
        log.warn("File '{}' not found in folder {}", filename, folderId);
        return null;
    }

    /**
     * Download the Excel file as InputStream.
     * Caller must close the stream.
     */
    public byte[] downloadFile(String fileId) throws Exception {
        String token = getAccessToken();
        // WorkDrive download endpoint
        String url = WORKDRIVE_API + "/download/" + fileId;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(url);
            get.setHeader("Authorization", "Zoho-oauthtoken " + token);

            return client.execute(get, httpResponse -> {
                int status = httpResponse.getCode();
                if (status != 200) {
                    String body = EntityUtils.toString(httpResponse.getEntity());
                    throw new RuntimeException("Download failed [" + status + "]: " + body);
                }
                return httpResponse.getEntity().getContent().readAllBytes();
            });
        }
    }

    /**
     * Upload (overwrite) the Excel file to WorkDrive ASYNCHRONOUSLY.
     * Returns a CompletableFuture so caller can track completion if needed.
     */
    public CompletableFuture<Void> uploadFileAsync(byte[] fileBytes, String filename, String existingFileId) {
        return CompletableFuture.runAsync(() -> {
            try {
                uploadFile(fileBytes, filename, existingFileId);
            } catch (Exception e) {
                log.error("Async upload failed for '{}'", filename, e);
                throw new RuntimeException(e);
            }
        }, uploadExecutor);
    }

    /**
     * Synchronous upload — overwrites existing file if fileId provided,
     * else creates a new file in the folder.
     */
    public void uploadFile(byte[] fileBytes, String filename, String existingFileId) throws Exception {
        String token = getAccessToken();
        log.info("Uploading '{}' ({} bytes) to WorkDrive...", filename, fileBytes.length);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post;
            if (existingFileId != null && !existingFileId.isBlank()) {
                // Overwrite existing — upload new version
                post = new HttpPost(WORKDRIVE_API + "/upload?override-name-exist=true&parent-id=" + folderId);
            } else {
                post = new HttpPost(WORKDRIVE_API + "/upload?parent-id=" + folderId);
            }
            post.setHeader("Authorization", "Zoho-oauthtoken " + token);

            var multipart = MultipartEntityBuilder.create()
                    .addBinaryBody("content", fileBytes,
                            ContentType.parse("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
                            filename)
                    .build();
            post.setEntity(multipart);

            String response = client.execute(post, httpResponse -> {
                int status = httpResponse.getCode();
                String body = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
                if (status != 200 && status != 201) {
                    throw new RuntimeException("Upload failed [" + status + "]: " + body);
                }
                return body;
            });
            log.info("Upload successful: {}", response);
        }
    }

    public void shutdown() {
        uploadExecutor.shutdown();
    }
}
