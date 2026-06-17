package com.expensemanager.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expensemanager.model.WorkDriveFile;
import com.expensemanager.util.AppContextListener;

public class FilesApiService {

	private static final Logger log = LoggerFactory.getLogger(FilesApiService.class);
	private ZohoTokenService token = new ZohoTokenService();
	private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15))
			.version(HttpClient.Version.HTTP_1_1).build();
	private static final int DAYS_BACKUP = 30;
	private static String FOLDERID = null;

	FilesApiService() {
		FOLDERID = AppContextListener.getContext().getInitParameter("workdrive.folder.id");
//		FOLDERID = "40p8hd98b5756f2c84539a3f51d502ce71a51";
	}

	public String UploadFile(File file) {
//		UploadFileService upload = new UploadFileService();
		String resourceID = null;
		try {
			resourceID = uploadToWorkDrive(file);
		} catch (Exception e) {
			log.debug("UploadFile Method - File uploading: {} ", e.getMessage());
			e.printStackTrace();
		}

		List<WorkDriveFile> list = null;
		try {
			list = listFiles();
		} catch (IOException e) {
			log.debug("UploadFile Method - listservice : {} ", e.getMessage());
			e.printStackTrace();
		}
		if (list.size() > DAYS_BACKUP) {
			log.debug("Going to delete");
			Collections.sort(list);

			int toBeDeleted = list.size() - DAYS_BACKUP;
			int deletedFiles = 0;
			for (int i = 0; i < toBeDeleted; i++) {
				WorkDriveFile wdf = list.get(i);
				boolean isdeleted = false;
				try {
					isdeleted = deleteFile(wdf.getId());
				} catch (IOException e) {
					log.debug("UploadFile Method - deleteFile : {} ", e.getMessage());
					e.printStackTrace();
				}
				if (isdeleted) {
					deletedFiles++;
				}
			}
			int failed = toBeDeleted - deletedFiles;
			log.debug("Total File Need to delete : {}, Deleted : {}, Failed : {}", toBeDeleted, deletedFiles, failed);
		}

		return resourceID;
	}

	public List<WorkDriveFile> listFiles() throws IOException {
//		folderId = AppContextListener.getContext().getInitParameter("workdrive.folder.id");
		String BASE_URL = "https://workdrive.zoho.com/api/v1";
		List<WorkDriveFile> allFiles = new ArrayList<>();
		int offset = 0;
		int limit = 50;

		while (true) {
			String url = BASE_URL + "/files/" + FOLDERID + "/files" + "?" + limit + "&page%5Boffset%5D=" + offset;

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

//				System.out.println(attrs);

				WorkDriveFile f = new WorkDriveFile();
				f.setId(item.optString("id"));
				f.setName(attrs.optString("name"));
				f.setExtension(attrs.optString("extn"));
				f.setType(attrs.optString("type"));
				f.setFolder(false);
				f.setModifiedTime(attrs.optLong("modified_time_in_millisecond"));
				f.setParentId(attrs.optString("parent_id"));
				f.setPermalink(attrs.optString("permalink"));

				JSONObject storage = attrs.optJSONObject("storage_info");
				if (storage != null) {
					f.setSizeInBytes(storage.optLong("size_in_bytes", 0));
				}

				log.debug("Found: {} ({}) {}", f.getName(), f.getExtension(), f.getId());
				allFiles.add(f);
			}

			if (data.length() < limit) {
				log.debug("Last page reached at offset: {}", offset);
				break;
			}

			offset += limit;
			log.debug("Moving to next page — offset: {}", offset);
		}

		log.info("Total files listed: {} in folder {}", allFiles.size(), FOLDERID);
		return allFiles;
	}

	private String get(String url) throws IOException {
		try {
			HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url))
					.header("Authorization", "Zoho-oauthtoken " + token.getAccessToken())
					.header("Accept", "application/vnd.api+json").GET().build();

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

	public boolean deleteFile(String fileId) throws IOException {
		if (fileId == null || fileId.isBlank()) {
			log.warn("deleteFile: fileId missing");
			return false;
		}

		String url = "https://www.zohoapis.com/workdrive/api/v1/files/" + fileId;
		log.debug("Deleting file: {}", fileId);

		// PATCH body — status 51 = delete file to trash
		String body = "{\"data\":{\"attributes\":{\"status\":\"51\"},\"type\":\"files\"}}";

		try {
			HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(30))
					.header("Authorization", "Zoho-oauthtoken " + token.getAccessToken())
					.header("Content-Type", "application/vnd.api+json").header("Accept", "application/vnd.api+json")
					.method("PATCH", HttpRequest.BodyPublishers.ofString(body)) // ✅ PATCH
					.build();

			HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
			log.debug("Delete PATCH {} → HTTP {}", url, resp.statusCode());
			log.debug("Delete response: {}", resp.body());

			if (resp.statusCode() == 200 || resp.statusCode() == 204) {
				log.info("File deleted successfully: {}", fileId);
				return true;
			}

			log.error("Delete failed [HTTP {}]: {}", resp.statusCode(), resp.body());
			return false;

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException("Delete interrupted", e);
		}
	}
	
	public static class DownloadResult {
	    public final String fileName;
	    public final byte[] fileBytes;
	    public DownloadResult(String fileName, byte[] fileBytes) {
	        this.fileName = fileName;
	        this.fileBytes = fileBytes;
	    }
	}

	public byte[] downloadFile(String fileId) throws IOException {
		// download-accl.zoho.com use
		String url = "https://download-accl.zoho.com/v1/workdrive/download/" + fileId;
		log.debug("Downloading file: {}", fileId);

		try {
			HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(60))
					.header("Authorization", "Zoho-oauthtoken " + token.getAccessToken()).GET().build();

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

	public String uploadToWorkDrive(File file) throws Exception {
		String WORDRIVE_API_URL = "https://workdrive.zoho.com/api/v1/upload";

		FOLDERID = AppContextListener.getContext().getInitParameter("workdrive.folder.id");
		String boundary = "Boundary-" + UUID.randomUUID().toString().replace("-", "");

		// Multipart body manually build (no external library)
		ByteArrayOutputStream bodyStream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(bodyStream));

		// -- parent_id field
		writer.append("--").append(boundary).append("\r\n");
		writer.append("Content-Disposition: form-data; name=\"parent_id\"").append("\r\n\r\n");
		writer.append(FOLDERID).append("\r\n");

		// -- override-name-exist field
		writer.append("--").append(boundary).append("\r\n");
		writer.append("Content-Disposition: form-data; name=\"override-name-exist\"").append("\r\n\r\n");
		writer.append("true").append("\r\n");

		// -- file content field header
		writer.append("--").append(boundary).append("\r\n");
		writer.append("Content-Disposition: form-data; name=\"content\"; filename=\"").append(file.getName())
				.append("\"").append("\r\n");
		writer.append("Content-Type: application/zip").append("\r\n\r\n");
		writer.flush();

		// File bytes
		bodyStream.write(Files.readAllBytes(file.toPath()));

		// Closing boundary
		writer = new PrintWriter(new OutputStreamWriter(bodyStream));
		writer.append("\r\n--").append(boundary).append("--\r\n");
		writer.flush();

		byte[] body = bodyStream.toByteArray();

		// HTTP Request
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(WORDRIVE_API_URL))
				.header("Authorization", "Zoho-oauthtoken " + token.getAccessToken())
				.header("Content-Type", "multipart/form-data; boundary=" + boundary)
				.POST(HttpRequest.BodyPublishers.ofByteArray(body)).build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		log.debug("HTTP Status : {}", response.statusCode());
		log.debug("Response    : {} ", response.body());

		if (response.statusCode() == 200 || response.statusCode() == 201) {
			JSONObject json = new JSONObject(response.body());
			System.out.println("✅ Upload Success!");

			JSONObject fileData = json.getJSONArray("data").getJSONObject(0);
			JSONObject attributes = fileData.getJSONObject("attributes");
//			System.out.println(attributes);

			log.debug("File Name   : {}", attributes.optString("FileName", "N/A"));
			log.debug("Resource ID : {}", attributes.optString("resource_id"));
			log.debug("Parent ID   : {}", attributes.optString("parent_id", "N/A"));
			return attributes.optString("resource_id");
		}
		return null;
	}
}
