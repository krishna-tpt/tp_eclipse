package com.workdrive.organizer.service;

import com.workdrive.organizer.model.WorkDriveFile;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class WorkDriveApiService {

	private static final Logger log = LoggerFactory.getLogger(WorkDriveApiService.class);
	private static final String BASE_URL = "https://workdrive.zoho.com/api/v1";

	private final ZohoTokenService tokenService;
	private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15))
			.version(HttpClient.Version.HTTP_1_1).build();

	public WorkDriveApiService(ZohoTokenService tokenService) {
		this.tokenService = tokenService;
	}

	public List<WorkDriveFile> listFiles(String folderId) throws IOException {
		List<WorkDriveFile> allFiles = new ArrayList<>();
		int offset = 0;
		int limit = 50;

		while (true) {
			String url = BASE_URL + "/files/" + folderId + "/files" + "?" + limit + "&page%5Boffset%5D="+ offset;

			log.debug("Fetching files — offset: {}, limit: {}", offset, limit);
			String responseBody = get(url);

			JSONObject json = new JSONObject(responseBody);
			JSONArray data = json.optJSONArray("data");

			if (data == null || data.length() == 0) {
				log.debug("No more files — stopping");
				break;
			}
//			System.out.println(data);

			for (int i = 0; i < data.length(); i++) {
				JSONObject item = data.getJSONObject(i);
				JSONObject attrs = item.optJSONObject("attributes");
				if (attrs == null)
					continue;

				boolean isFolder = attrs.optBoolean("is_folder", false);
				if (isFolder) {
					log.debug("Skipping folder: {}", attrs.optString("name"));
					continue;
				}
				
				System.out.println(attrs);
				
				WorkDriveFile f = new WorkDriveFile();
				f.setId(item.optString("id"));
				f.setName(attrs.optString("name"));
				f.setExtension(attrs.optString("extn"));
				f.setType(attrs.optString("type"));
				f.setFolder(false);
				f.setModifiedTime(attrs.optString("modified_time"));
				f.setParentId(attrs.optString("parent_id"));
				f.setPermalink(attrs.optString("permalink"));

				JSONObject storage = attrs.optJSONObject("storage_info");
				if (storage != null) {
					f.setSizeInBytes(storage.optLong("size_in_bytes", 0));
				}

				log.debug("Found: {} ({})", f.getName(), f.getExtension());
				allFiles.add(f);
			}

			if (data.length() < limit) {
				log.debug("Last page reached at offset: {}", offset);
				break;
			}

			offset += limit;
			log.debug("Moving to next page — offset: {}", offset);
		}

		log.info("Total files listed: {} in folder {}", allFiles.size(), folderId);
		return allFiles;
	}

	private String get(String url) throws IOException {
		try {
			HttpRequest req = HttpRequest.newBuilder()
					.uri(URI.create(url))
					.header("Authorization", "Zoho-oauthtoken " + tokenService.getAccessToken())
					.header("Accept", "application/vnd.api+json")
					.GET()
					.build();

			HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
			log.debug("GET {} → HTTP {}", url, resp.statusCode());
			if (resp.statusCode() != 200)
				throw new IOException("GET failed [HTTP " + resp.statusCode() + "]: " + resp.body());
			return resp.body();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException("Interrupted", e);
		}
	}

	/**
	 * Downloads a file from Zoho WorkDrive and returns raw bytes.
	 * URL: https://download-accl.zoho.com/v1/workdrive/download/{resource_id}
	 */
	public byte[] downloadFile(String fileId) throws IOException {
	    // ✅ download-accl.zoho.com use 
	    String url = "https://download-accl.zoho.com/v1/workdrive/download/" + fileId;
	    log.debug("Downloading file: {}", fileId);

	    try {
	        HttpRequest req = HttpRequest.newBuilder()
	                .uri(URI.create(url))
	                .timeout(Duration.ofSeconds(60))
	                .header("Authorization", "Zoho-oauthtoken " + tokenService.getAccessToken())
	                .GET()
	                .build();

	        HttpResponse<byte[]> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofByteArray());
	        log.debug("Download {} → HTTP {}", url, resp.statusCode());

	        if (resp.statusCode() != 200) {
	            log.error("Download failed [HTTP {}]: {}", resp.statusCode(), new String(resp.body()));
	            throw new IOException("Download failed [HTTP " + resp.statusCode() + "]");
	        }

	        log.info("Downloaded {} bytes for fileId: {}", resp.body().length, fileId);
	        return resp.body();

	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	        throw new IOException("Download interrupted", e);
	    }
	}

}