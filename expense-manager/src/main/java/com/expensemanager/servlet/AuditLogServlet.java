package com.expensemanager.servlet;

import com.expensemanager.dao.AuditLogDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/audit")
public class AuditLogServlet extends HttpServlet {
	
	private static final Logger log = LoggerFactory.getLogger(AuditLogServlet.class);
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		int bookId = (Integer) req.getSession().getAttribute("activeBookId");

		String pageStr = req.getParameter("page");
		int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
		int pageSize = 25;

		try {
			AuditLogDAO dao = new AuditLogDAO();
			var logs = dao.findRecentByBook(bookId, page, pageSize);
			int total = dao.countByBook(bookId);
			int totalPages = (int) Math.ceil((double) total / pageSize);

			req.setAttribute("auditLogs", logs);
			req.setAttribute("page", page);
			req.setAttribute("totalPages", totalPages);
			req.setAttribute("total", total);
		} catch (Exception e) {
			req.setAttribute("dbError", e.getMessage());
		}
		req.getRequestDispatcher("/WEB-INF/views/audit_log.jsp").forward(req, resp);
	}
}