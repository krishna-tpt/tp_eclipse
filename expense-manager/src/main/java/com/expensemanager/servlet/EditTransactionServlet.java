package com.expensemanager.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expensemanager.dao.AuditLogDAO;
import com.expensemanager.dao.CategoryDAO;
import com.expensemanager.dao.ReceiptDAO;
import com.expensemanager.dao.SubCategoryDAO;
import com.expensemanager.dao.TransactionDAO;
import com.expensemanager.model.Transaction;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/transaction")
public class EditTransactionServlet extends HttpServlet {
	private static final Logger log = LoggerFactory.getLogger(EditTransactionServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String idStr = req.getParameter("id");
		if (idStr == null) {
			resp.sendRedirect(req.getContextPath() + "/transactions");
			return;
		}

		try {
			int id = Integer.parseInt(idStr);
			TransactionDAO txnDAO = new TransactionDAO();
			AuditLogDAO audDAO = new AuditLogDAO();
			CategoryDAO catDAO = new CategoryDAO();
			SubCategoryDAO scDAO = new SubCategoryDAO();
			ReceiptDAO recDAO = new ReceiptDAO();

			Transaction t = txnDAO.findById(id);
			if (t == null) {
				resp.sendRedirect(req.getContextPath() + "/transactions?error=notfound");
				return;
			}

			req.setAttribute("txn", t);
			req.setAttribute("auditLogs", audDAO.findByTransactionId(id));
			req.setAttribute("receipts", recDAO.findMetaByTransactionId(id));
			req.setAttribute("incomeCategories", catDAO.findByType("INCOME"));
			req.setAttribute("expenseCategories", catDAO.findByType("EXPENSE"));
			req.setAttribute("subCategories", scDAO.findAll());

		} catch (Exception e) {
			req.setAttribute("dbError", e.getMessage());
		}
		req.getRequestDispatcher("/WEB-INF/views/transaction_detail.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");
		String action = req.getParameter("action");
		String idStr = req.getParameter("id");
		if (idStr == null) {
			resp.sendRedirect(req.getContextPath() + "/transactions");
			return;
		}

		Enumeration<String> paramNames = req.getParameterNames();

		while (paramNames.hasMoreElements()) {
			String key = paramNames.nextElement();
			String value = req.getParameter(key);

			log.debug("Key: {} --> Value: {}", key, value);
		}
		int id = Integer.parseInt(idStr);
//		int bookId = (Integer) req.getSession().getAttribute("activeBookId");

		// Detect panel AJAX call
		boolean isAjax = "XMLHttpRequest".equals(req.getHeader("X-Requested-With"));

		try {
			TransactionDAO txnDAO = new TransactionDAO();

			if ("delete".equalsIgnoreCase(action)) {
				txnDAO.delete(id);
				if (isAjax) {
					resp.setContentType("application/json");
					resp.getWriter().write("{\"status\":\"deleted\"}");
				} else {
					resp.sendRedirect(req.getContextPath() + "/transactions?success=deleted");
				}
				return;
			}

			// UPDATE
			Transaction old = txnDAO.findById(id);
			if (old == null) {
				resp.sendRedirect(req.getContextPath() + "/transactions?error=notfound");
				return;
			}

			// Build updated transaction
			Transaction updated = new Transaction();
			updated.setId(id);
			updated.setType(old.getType()); // type cannot change
			updated.setBookId(old.getBookId());

			String amountStr = req.getParameter("amount");
			String dateStr = req.getParameter("dateTime");
			String catIdStr = req.getParameter("categoryId");
			String subcatStr = req.getParameter("subcategoryId");
			String note = req.getParameter("note");

			updated.setAmount(new BigDecimal(amountStr));
			updated.setDateTime(LocalDateTime.parse(dateStr));
			updated.setCategoryId(Integer.parseInt(catIdStr));
			updated.setNote(note);

			// Need category name for audit log
			CategoryDAO catDAO = new CategoryDAO();
			String catType = old.getType().name();
			catDAO.findByType(catType).stream().filter(c -> c.getId() == updated.getCategoryId()).findFirst()
					.ifPresent(c -> updated.setCategoryName(c.getName()));

			if (subcatStr != null && !subcatStr.isBlank()) {
				int subcatId = Integer.parseInt(subcatStr.trim());
				updated.setSubcategoryid(subcatId);
				new SubCategoryDAO().findAll().stream().filter(sc -> sc.getId() == subcatId).findFirst()
						.ifPresent(sc -> updated.setSubCategoryName(sc.getName()));
			}

			txnDAO.update(old, updated);

			if (isAjax) {
				resp.setContentType("application/json");
				resp.getWriter().write("{\"status\":\"ok\"}");
			} else {
				resp.sendRedirect(req.getContextPath() + "/transaction?id=" + id + "&success=updated");
			}

		} catch (Exception e) {
			if (isAjax) {
				resp.setContentType("application/json");
				resp.getWriter().write("{\"status\":\"error\",\"msg\":\"" + e.getMessage().replace("\"", "'") + "\"}");
			} else {
				resp.sendRedirect(req.getContextPath() + "/transaction?id=" + id + "&error="
						+ java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
			}
		}
	}
}