package com.expensemanager.servlet;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expensemanager.service.WorkDriveApiService;
import com.expensemanager.util.AppConfig;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * GET /download?fileId=xxx&fileName=xxx.xlsx
 * Downloads a file from Zoho WorkDrive and streams to browser.
 */
public class DownloadServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(DownloadServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        log.trace("ENTER DownloadServlet.doGet()");

        String fileId   = req.getParameter("fileId");
        String fileName = req.getParameter("fileName");

        log.info(">>> Download request — fileId: {}, fileName: {}", fileId, fileName);

        // Validation
        if (fileId == null || fileId.isBlank()) {
            log.warn("fileId missing in request");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"fileId is required\"}");
            return;
        }

        if (fileName == null || fileName.isBlank()) {
        	fileName = "download_" + fileId + ".zip";
        }

        try {
            WorkDriveApiService apiSvc = AppConfig.getApiService(getServletContext());

            log.debug("Starting download for fileId: {}", fileId);
            byte[] fileBytes = apiSvc.downloadFile(fileId);
            log.info("Download complete — {} bytes for file: {}", fileBytes.length, fileName);

            resp.reset();
            resp.setContentType("application/zip");                          // ← ZIP content type
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            resp.setContentLengthLong(fileBytes.length);
            resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            resp.setHeader("Pragma", "no-cache");

            resp.getOutputStream().write(fileBytes);
            resp.getOutputStream().flush();

            log.trace("EXIT DownloadServlet.doGet() - success");

        } catch (Exception e) {
            log.error("Download failed for fileId: {}", fileId, e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Download failed: " + e.getMessage());
        }
    }
}