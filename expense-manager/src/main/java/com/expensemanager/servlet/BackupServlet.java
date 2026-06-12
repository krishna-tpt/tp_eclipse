package com.expensemanager.servlet;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expensemanager.dao.BackupDAO;
import com.expensemanager.model.BackupMetadata;
import com.expensemanager.model.BackupMetadata.BackupType;
import com.expensemanager.service.BackupService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/backup/*")
@MultipartConfig(maxFileSize = 50 * 1024 * 1024)
public class BackupServlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(BackupServlet.class);
	private final BackupService svc = new BackupService();
	private final BackupDAO dao = new BackupDAO();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String path = req.getPathInfo();
		if ("/download".equals(path)) {
			handleDownload(req, res);
			return;
		}
		try {
			List<BackupMetadata> backups = dao.getAll();
			req.setAttribute("backups", backups);
			req.setAttribute("activePage", "backup");
			req.setAttribute("pageTitle", "Backup & Restore");
			req.getRequestDispatcher("/WEB-INF/views/backup.jsp").forward(req, res);
		} catch (Exception e) {
			req.getSession().setAttribute("errorMsg", "Error: " + e.getMessage());
			res.sendRedirect(req.getContextPath() + "/backup/list");
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String path = req.getPathInfo();
		if (path == null)
			path = "";
		switch (path) {
		case "/create":
			handleCreate(req, res);
			break;
		case "/restore":
			handleRestore(req, res);
			break;
		case "/upload":
			handleUpload(req, res);
			break;
		case "/delete":
			handleDelete(req, res);
			break;
		default:
			res.sendRedirect(req.getContextPath() + "/backup/list");
		}
	}

	private void handleCreate(HttpServletRequest req, HttpServletResponse res) throws IOException {
		try {
			BackupMetadata m = svc.createBackup(req.getParameter("description"), BackupType.MANUAL);
			req.getSession().setAttribute("successMsg",
					"✅ Backup created! File: " + m.getFileName() + " | Size: " + m.getFileSizeFormatted() + " | "
							+ m.getIncomeCount() + " income, " + m.getExpenseCount() + " expense records");
		} catch (Exception e) {
			req.getSession().setAttribute("errorMsg", "❌ Backup failed: " + e.getMessage());
		}
		res.sendRedirect(req.getContextPath() + "/backup/list");
	}

	private void handleRestore(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String idStr = req.getParameter("backupId");
		if (idStr == null || idStr.isEmpty()) {
			req.getSession().setAttribute("errorMsg", "No backup ID.");
			res.sendRedirect(req.getContextPath() + "/backup/list");
			return;
		}
		try {
			svc.restoreBackup(Integer.parseInt(idStr));
			req.getSession().setAttribute("successMsg",
					"✅ Data restored from backup #" + idStr + ". Safety backup was auto-created before restore.");
		} catch (Exception e) {
			req.getSession().setAttribute("errorMsg", "❌ Restore failed: " + e.getMessage());
		}
		res.sendRedirect(req.getContextPath() + "/backup/list");
	}

	private void handleDownload(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String idStr = req.getParameter("id");
		if (idStr == null) {
			res.sendError(400, "Missing id");
			return;
		}
		try {
			int id = Integer.parseInt(idStr);
			byte[] bytes = svc.getBackupBytes(id);
			BackupMetadata meta = dao.getById(id);
			res.setContentType("application/zip");
			res.setHeader("Content-Disposition", "attachment; filename=\"" + meta.getFileName() + "\"");
			res.setContentLength(bytes.length);
			res.getOutputStream().write(bytes);
		} catch (Exception e) {
			res.sendError(500, "Download failed: " + e.getMessage());
		}
	}

	private void handleUpload(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		Part filePart = req.getPart("backupFile");
		if (filePart == null || filePart.getSize() == 0) {
			req.getSession().setAttribute("errorMsg", "Please select a file.");
			res.sendRedirect(req.getContextPath() + "/backup/list");
			return;
		}
		String name = extractFileName(filePart);
		if (!name.toLowerCase().endsWith(".zip")) {
			req.getSession().setAttribute("errorMsg", "Only .zip files supported.");
			res.sendRedirect(req.getContextPath() + "/backup/list");
			return;
		}
		try {
			byte[] bytes = filePart.getInputStream().readAllBytes();
			BackupMetadata m = svc.registerUploadedBackup(bytes, name, req.getParameter("description"));
			req.getSession().setAttribute("successMsg",
					"✅ Uploaded: " + m.getFileName() + " (" + m.getFileSizeFormatted() + ")");
		} catch (Exception e) {
			req.getSession().setAttribute("errorMsg", "❌ Upload failed: " + e.getMessage());
		}
		res.sendRedirect(req.getContextPath() + "/backup/list");
	}

	private void handleDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
		try {
			svc.deleteBackup(Integer.parseInt(req.getParameter("backupId")));
			req.getSession().setAttribute("successMsg", "Backup deleted.");
		} catch (Exception e) {
			req.getSession().setAttribute("errorMsg", "Delete failed: " + e.getMessage());
		}
		res.sendRedirect(req.getContextPath() + "/backup/list");
	}

	private String extractFileName(Part part) {
		String cd = part.getHeader("content-disposition");
		for (String t : cd.split(";")) {
			t = t.trim();
			if (t.startsWith("filename"))
				return t.substring(t.indexOf('=') + 1).trim().replace("\"", "");
		}
		return "backup.zip";
	}
}
