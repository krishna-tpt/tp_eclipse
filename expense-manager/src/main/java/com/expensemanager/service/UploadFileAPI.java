package com.expensemanager.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expensemanager.util.AppContextListener;

public class UploadFileAPI {

	private static final Logger log = LoggerFactory.getLogger(UploadFileAPI.class);
	private static final String WORDRIVE_API_URL = "https://workdrive.zoho.com/api/v1/upload";
	private static String WORKDRIVE_FOLDER_ID = null;
	private ZohoTokenService token = new ZohoTokenService();

	public boolean uploadToWorkDrive(File file) throws Exception {
		WORKDRIVE_FOLDER_ID = AppContextListener.getContext().getInitParameter("workdrive.folder.id");
		String boundary = "Boundary-" + UUID.randomUUID().toString().replace("-", "");

		// Multipart body manually build (no external library)
		ByteArrayOutputStream bodyStream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(bodyStream));

		// -- parent_id field
		writer.append("--").append(boundary).append("\r\n");
		writer.append("Content-Disposition: form-data; name=\"parent_id\"").append("\r\n\r\n");
		writer.append(WORKDRIVE_FOLDER_ID).append("\r\n");

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

		log.debug("HTTP Status : {}" , response.statusCode());
		log.debug("Response    : {} ", response.body());

		if (response.statusCode() == 200 || response.statusCode() == 201) {
			JSONObject json = new JSONObject(response.body());
			System.out.println("✅ Upload Success!");

			JSONObject fileData = json.getJSONArray("data").getJSONObject(0);
			JSONObject attributes = fileData.getJSONObject("attributes");
//			System.out.println(attributes);

			log.debug("File Name   : {}", attributes.optString("FileName", "N/A"));
			log.debug("Resource ID : {}", attributes.optString("resource_id", "N/A"));
			log.debug("Parent ID   : {}", attributes.optString("parent_id", "N/A"));
			return true;
		}
		return false;
	}
}