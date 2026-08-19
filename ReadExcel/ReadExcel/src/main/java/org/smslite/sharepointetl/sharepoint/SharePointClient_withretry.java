package org.smslite.sharepointetl.sharepoint;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.smslite.sharepointetl.model.SharePointFile;
import org.smslite.sharepointetl.model.FolderConfig;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SharePointClient {

    private static final Logger logger = LoggerFactory.getLogger(SharePointClient.class);

    private final String tenantId;
    private final String clientId;
    private final String clientSecret;
    private final String driveId;
    private final FolderConfig folderConfig;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    // Added: cache the access token until it's close to expiring, instead of fetching a new
    // one on every call. static + shared lock because every folder gets its own SharePointClient
    // instance (same credentials each time), so this cache is shared across all of them.
    private static volatile String cachedAccessToken = null;
    private static volatile Instant tokenExpiryTime = Instant.MIN;
    private static final Object TOKEN_LOCK = new Object();
    private static final long TOKEN_EXPIRY_BUFFER_SECONDS = 120; // refresh a bit before actual expiry

    public SharePointClient(String tenantId, String clientId, String clientSecret,
                            String driveId, FolderConfig folderConfig) {
        this.tenantId = tenantId;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.driveId = driveId;
        this.folderConfig = folderConfig;
    }

    // Added: retry handling for transient Graph errors (503/504/429) so one bad response
    // doesn't abort the whole polling cycle. Existing logic/flow elsewhere is unchanged.
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long DEFAULT_RETRY_SECONDS = 5L;
    private static final long MAX_RETRY_SECONDS = 120L;

    private HttpResponse<String> sendWithRetry(HttpRequest request, String context) throws IOException, InterruptedException {
        int attempt = 0;
        while (true) {
            attempt++;
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();

            if (status != 503 && status != 504 && status != 429) {
                return response; // same as before: caller does its own status check
            }
            if (attempt >= MAX_RETRY_ATTEMPTS) {
                logger.error("Giving up after {} attempts for {}. Last HTTP status: {} body: {}",
                        attempt, context, status, response.body());
                return response; // caller's existing status-check logic throws as before
            }

            long waitSeconds = resolveRetryAfterSeconds(response, attempt);
            logger.warn("Transient error ({}) on {} (attempt {}/{}). Retrying in {}s. Body: {}",
                    status, context, attempt, MAX_RETRY_ATTEMPTS, waitSeconds, response.body());
            Thread.sleep(waitSeconds * 1000L);
        }
    }

    private long resolveRetryAfterSeconds(HttpResponse<String> response, int attempt) {
        try {
            JSONObject json = new JSONObject(response.body());
            if (json.has("error")) {
                JSONObject error = json.getJSONObject("error");
                //added log for get the retryAfterSeconds actual value from json response on aug 4th 2026
                logger.debug("Retry after seconds from json response : {}",error.optLong("retryAfterSeconds"));
                if (error.has("retryAfterSeconds")) {
                    return Math.min(error.optLong("retryAfterSeconds", DEFAULT_RETRY_SECONDS), MAX_RETRY_SECONDS);
                }
            }
        } catch (Exception ignored) { }

        java.util.Optional<String> header = response.headers().firstValue("Retry-After");
        if (header.isPresent()) {
            try {
                return Math.min(Long.parseLong(header.get()), MAX_RETRY_SECONDS);
            } catch (NumberFormatException ignored) { }
        }

        long backoff = DEFAULT_RETRY_SECONDS * (1L << (attempt - 1)); // 5, 10, 20...
        return Math.min(backoff, MAX_RETRY_SECONDS);
    }
    // End of added retry handling

    private String getAccessToken() {
        // Added: return cached token if it's still valid, skipping the network call entirely.
        if (cachedAccessToken != null && Instant.now().isBefore(tokenExpiryTime)) {
            logger.debug("Using cached access token (valid until {})", tokenExpiryTime);
            return cachedAccessToken;
        }

        synchronized (TOKEN_LOCK) {
            // Re-check inside the lock in case another folder's client just refreshed it
            if (cachedAccessToken != null && Instant.now().isBefore(tokenExpiryTime)) {
                logger.debug("Using cached access token fetched by another thread (valid until {})", tokenExpiryTime);
                return cachedAccessToken;
            }

        try {
            String body = "client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
                    + "&scope=" + URLEncoder.encode("https://graph.microsoft.com/.default", StandardCharsets.UTF_8)
                    + "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8)
                    + "&grant_type=client_credentials";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/token"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to get access token: " + response.body());
            }

            JSONObject json = new JSONObject(response.body());
            String token = json.getString("access_token");

            // Added: cache the token using the expires_in value Azure AD returns, minus a buffer.
            long expiresIn = json.optLong("expires_in", 3600L);
            cachedAccessToken = token;
            tokenExpiryTime = Instant.now().plusSeconds(Math.max(expiresIn - TOKEN_EXPIRY_BUFFER_SECONDS, 60));
            logger.info("Fetched new access token, expires in {}s (cached until {})", expiresIn, tokenExpiryTime);

            return token;

        } catch (Exception e) {
            logger.error("Error fetching access token", e);
            throw new RuntimeException("Error fetching access token", e);
        }
        } // end synchronized(TOKEN_LOCK)
    }

    public List<SharePointFile> listFilesInFolder() {
        List<SharePointFile> files = new ArrayList<>();
        String token = getAccessToken();

        String url = "https://graph.microsoft.com/v1.0/drives/" + driveId +
                "/items/" + folderConfig.getReceivedFolderId() + "/children";
        logger.info("Processing SharePoint folder: {}", folderConfig.getName());
        logger.debug("URL to fetch files: {}", url);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            logger.info("URL Processing is " + url);
            HttpResponse<String> response = sendWithRetry(request, "listFilesInFolder:" + folderConfig.getName());

            if (response.statusCode() != 200) {
                logger.error("Failed to fetch files for folder {}. HTTP error code: {}", folderConfig.getName(), response.statusCode());
                throw new RuntimeException("Failed to fetch files. HTTP error code: " + response.statusCode());
            }

            JSONObject json = new JSONObject(response.body());
            JSONArray value = json.getJSONArray("value");

            logger.debug("Files JSON response for folder {}: {}", folderConfig.getName(), value);

            for (int i = 0; i < value.length(); i++) {
                JSONObject fileObj = value.getJSONObject(i);

                if (!fileObj.has("file")) continue; // skip folders

                String name = fileObj.getString("name");
                String id = fileObj.getString("id");
                String urlDownload = fileObj.optString("@microsoft.graph.downloadUrl", null);
                if (urlDownload == null) {
                    logger.warn("No download URL for file: {} in folder: {}", name, folderConfig.getName());
                    continue;
                }

                long length = fileObj.optLong("size", 0L);
                String createdDateTime = fileObj.optString("createdDateTime", null);
                LocalDateTime createdTime = null;
                if (createdDateTime != null) {
                    createdTime = LocalDateTime.parse(createdDateTime.replace("Z", ""));
                } else {
                    createdTime = LocalDateTime.now(); // fallback to current time if not available
                }
                
                SharePointFile file = new SharePointFile(name, urlDownload, length, id, createdTime);
                files.add(file);
                logger.info("Found file: {} ({} bytes) in folder: {}", name, length, folderConfig.getName());
                logger.debug("Download URL for file {}: {}", name, urlDownload);
            }

        } catch (IOException | InterruptedException e) {
            logger.error("Error listing files from SharePoint folder {}", folderConfig.getName(), e);
            throw new RuntimeException("Error listing files from SharePoint", e);
        }

        return files;
    }

    public void moveFile(String fileId) {
        moveFile(fileId, null);
    }

    public void moveFile(String fileId, String newFileName) {
        try {
            String url = String.format("https://graph.microsoft.com/v1.0/drives/%s/items/%s", driveId, fileId);
            JSONObject body = new JSONObject();
            JSONObject parentReference = new JSONObject();
            parentReference.put("id", folderConfig.getProcessedFolderId());
            body.put("parentReference", parentReference);
            
            if (newFileName != null && !newFileName.isEmpty()) {
                body.put("name", newFileName);
            }

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + getAccessToken())
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

            HttpResponse<String> response = sendWithRetry(request, "moveFile:" + fileId);
            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to move file: " + response.body());
            }
            logger.debug("Moved file {} to processed folder as {}", fileId, newFileName != null ? newFileName : "original name");
        } catch (Exception e) {
            logger.error("Error moving file: {}", e.getMessage(), e);
            throw new RuntimeException("Error moving file", e);
        }
    }

    public void moveFileToFolder(String fileId, String targetFolderId) {
        moveFileToFolder(fileId, targetFolderId, null);
    }

    public void moveFileToFolder(String fileId, String targetFolderId, String newFileName) {
        try {
            String url = String.format("https://graph.microsoft.com/v1.0/drives/%s/items/%s", driveId, fileId);
            JSONObject body = new JSONObject();
            JSONObject parentReference = new JSONObject();
            parentReference.put("id", targetFolderId);
            body.put("parentReference", parentReference);
            
            if (newFileName != null && !newFileName.isEmpty()) {
                body.put("name", newFileName);
            }

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + getAccessToken())
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

            HttpResponse<String> response = sendWithRetry(request, "moveFileToFolder:" + fileId);
            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to move file: " + response.body());
            }
            logger.debug("Moved file {} to folder {} as {}", fileId, targetFolderId, newFileName != null ? newFileName : "original name");
        } catch (Exception e) {
            logger.error("Error moving file: {}", e.getMessage(), e);
            throw new RuntimeException("Error moving file", e);
        }
    }

    public String getTargetTable() {
        return folderConfig.getTargetTable();
    }
}