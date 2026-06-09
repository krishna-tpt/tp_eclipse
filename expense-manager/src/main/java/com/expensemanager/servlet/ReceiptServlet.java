package com.expensemanager.servlet;

import java.io.IOException;

import com.expensemanager.dao.AuditLogDAO;
import com.expensemanager.dao.ReceiptDAO;
import com.expensemanager.model.Receipt;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

/**
 * GET /receipt?view=ID → serve image/file bytes POST /receipt (upload) → save
 * receipt for transaction POST /receipt (delete) → remove receipt
 */
@WebServlet("/receipt")
@MultipartConfig(maxFileSize = 5_242_880, maxRequestSize = 10_485_760) // 5MB file, 10MB request
public class ReceiptServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String viewId = req.getParameter("view");
		if (viewId == null) {
			resp.sendError(400, "Missing id");
			return;
		}
		try {
			Receipt r = new ReceiptDAO().findById(Integer.parseInt(viewId));
			if (r == null) {
				resp.sendError(404);
				return;
			}
			resp.setContentType(r.getFileType());
			resp.setHeader("Content-Disposition", "inline; filename=\"" + r.getFileName() + "\"");
			resp.setContentLength(r.getFileData().length);
			resp.getOutputStream().write(r.getFileData());
		} catch (Exception e) {
			resp.sendError(500, e.getMessage());
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");
		String action = req.getParameter("action");
		String txnId = req.getParameter("transactionId");

		if ("delete".equals(action)) {
			String receiptId = req.getParameter("receiptId");
			try {
				ReceiptDAO receiptDAO = new ReceiptDAO();
				Receipt existing = receiptDAO.findById(Integer.parseInt(receiptId)); // fetch before delete
				receiptDAO.delete(Integer.parseInt(receiptId));
				if (existing != null) {
					new AuditLogDAO().logReceiptDelete(Integer.parseInt(txnId), "user", existing.getFileName());
				}
			} catch (Exception ignored) {
			}
			resp.sendRedirect(req.getContextPath() + "/transaction?id=" + txnId + "&success=receipt_deleted");
			return;
		}

		// Upload
		Part filePart = req.getPart("receipt");
		if (filePart == null || filePart.getSize() == 0) {
			resp.sendRedirect(req.getContextPath() + "/transaction?id=" + txnId + "&error=no_file");
			return;
		}
		if (filePart.getSize() > 5_242_880) {
			resp.sendRedirect(req.getContextPath() + "/transaction?id=" + txnId + "&error=file_too_large");
			return;
		}

		try {
			Receipt r = new Receipt();
			r.setTransactionId(Integer.parseInt(txnId));
			r.setFileName(getFileName(filePart));
			r.setFileType(filePart.getContentType());
			r.setFileData(filePart.getInputStream().readAllBytes());
			r.setFileSize((int) filePart.getSize());
			new AuditLogDAO().logReceiptUpload(Integer.parseInt(txnId), "user", r.getFileName());
			new ReceiptDAO().insert(r);
			resp.sendRedirect(req.getContextPath() + "/transaction?id=" + txnId + "&success=receipt_uploaded");
		} catch (Exception e) {
			System.out.println("Receipt save failed ->"+e.getMessage());
			resp.sendRedirect(req.getContextPath() + "/transaction?id=" + txnId + "&error=" + e.getMessage());
		}
	}

	private String getFileName(Part part) {
		String cd = part.getHeader("content-disposition");
		if (cd != null) {
			for (String s : cd.split(";")) {
				if (s.trim().startsWith("filename")) {
					return s.substring(s.indexOf('=') + 1).trim().replace("\"", "");
				}
			}
		}
		return "receipt_" + System.currentTimeMillis();
	}
}