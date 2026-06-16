package com.expensemanager.service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expensemanager.model.WorkDriveFile;

public class FilesApiService {

	private static final Logger log = LoggerFactory.getLogger(FilesApiService.class);
	private ZohoTokenService token = new ZohoTokenService();
	private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15))
			.version(HttpClient.Version.HTTP_1_1).build();
	private static final int DAYS_BACKUP = 2;

	public static void main(String[] args) throws Exception {
		System.out.println("Main method");
		FilesApiService fileapi = new FilesApiService();
		fileapi.UploadFile(null);

	}

	public boolean UploadFile(File file) throws Exception {
//		UploadFileService upload = new UploadFileService();
//		boolean isUploaded = upload.uploadToWorkDrive(file);

		ListFilesService listservice = new ListFilesService();
		List<WorkDriveFile> list = listservice.listFiles();
		if (list.size() > ) {
			log.debug("Going to delete");
			Collections.sort(list);
			
			int toBeDeleted = list.size() - 
			for (WorkDriveFile workDriveFile : list) {
				if (currsize > 2) {
					boolean isdeleted = deleteFile(workDriveFile.getId());
					if (isdeleted) {
						log.debug("File deleted ");
					} else
						log.debug("File is not deleted ");
					list.remove(0);
					currsize = list.size();
//				log.debug("Payload : {}", workDriveFile.toString());
				}
			}
		}

		return false;
	}

	public boolean deleteFile(String fileId) throws IOException {
		if (fileId == null || fileId.isBlank()) {
			log.warn("deleteFile: fileId missing");
			return false;
		}

		String url = "https://www.zohoapis.com/workdrive/api/v1/files/" + fileId;
		log.debug("Deleting file: {}", fileId);

		// ✅ PATCH body — status 61 = permanent delete
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
}
