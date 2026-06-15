package com.expensemanager.service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expensemanager.util.AppConfig;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * GET /download?fileId=xxx&fileName=xxx.xlsx
 * Downloads a file from Zoho WorkDrive and streams to browser.
 */
public class DownloadFileService {

    private static final Logger log = LoggerFactory.getLogger(DownloadFileService.class);
	private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15))
			.version(HttpClient.Version.HTTP_1_1).build();
	private ZohoTokenService token = new ZohoTokenService();
    
    public byte[] downloadFile(String fileId) throws IOException {
	    //  download-accl.zoho.com use 
	    String url = "https://download-accl.zoho.com/v1/workdrive/download/" + fileId;
	    log.debug("Downloading file: {}", fileId);

	    try {
	        HttpRequest req = HttpRequest.newBuilder()
	                .uri(URI.create(url))
	                .timeout(Duration.ofSeconds(60))
	                .header("Authorization", "Zoho-oauthtoken " + token.getAccessToken())
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