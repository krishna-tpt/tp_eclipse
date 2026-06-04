package com.workdrive.organizer.servlet;

import com.workdrive.organizer.model.WorkDriveFile;
import com.workdrive.organizer.service.WorkDriveApiService;
import com.workdrive.organizer.util.AppConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * GET /api/files Returns JSON list of all files in the main WorkDrive folder.
 */
public class FilesApiServlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(FilesApiServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

//		log.trace("ENTER FilesApiServlet.doGet()");
//		log.info(">>> GET /api/files called");

		resp.setContentType("application/json; charset=UTF-8");
		resp.setHeader("Cache-Control", "no-cache");

		try {
//			log.trace("Getting ApiService from AppConfig");
			WorkDriveApiService apiSvc = AppConfig.getApiService(getServletContext());
			String mainFolderId = getServletContext().getInitParameter("workdrive.main.folder.id");
			log.debug("Main folder ID: {}", mainFolderId);
			  
//			log.trace("Calling listFiles()");
	        List<WorkDriveFile> files = apiSvc.listFiles(mainFolderId);
	        log.info("Total files fetched: {}", files.size());


			StringBuilder json = new StringBuilder("[");
			for (int i = 0; i < files.size(); i++) {
				WorkDriveFile f = files.get(i);
				String targetKey = f.resolveTargetFolderKey();
				json.append("{").append("\"id\":\"").append(f.getId()).append("\",").append("\"name\":\"")
						.append(escapeJson(f.getName())).append("\",").append("\"extension\":\"")
						.append(f.getExtension()).append("\",").append("\"type\":\"").append(f.getType()).append("\",")
						.append("\"sizeInBytes\":").append(f.getSizeInBytes()).append(",").append("\"modifiedTime\":\"")
						.append(f.getModifiedTime()).append("\",").append("\"permalink\":\"").append(f.getPermalink())
						.append("\",").append("\"targetFolder\":\"").append(targetKey != null ? targetKey : "none")
						.append("\"").append("}");
				if (i < files.size() - 1)
					json.append(",");
			}
			json.append("]");

			resp.getWriter().write(json.toString());
			log.debug(json.toString());
//			log.trace("EXIT FilesApiServlet.doGet() - success");
		} catch (IllegalStateException e) {
			log.error("Config error: {}", e.getMessage());
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.getWriter().write("{\"error\":\"Configuration error: " + escapeJson(e.getMessage()) + "\"}");
		} catch (Exception e) {
			log.error("Error listing files", e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.getWriter().write("{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
//			 log.error("FilesApiServlet error", e);
//		        log.trace("EXIT FilesApiServlet.doGet() - error");
		}
	}

	private String escapeJson(String s) {
		return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
	}
}
