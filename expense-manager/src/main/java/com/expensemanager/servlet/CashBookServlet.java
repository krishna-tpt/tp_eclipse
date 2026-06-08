package com.expensemanager.servlet;

import com.expensemanager.dao.CashBookDAO;
import com.expensemanager.model.CashBook;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * /books GET → list all books /books POST → create new book /books?edit=id GET
 * → edit form /books?delete=id GET → delete /books/select?id=X GET → set active
 * book in session
 */
@WebServlet("/books")
public class CashBookServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String deleteId = req.getParameter("delete");
		String selectId = req.getParameter("select");

//		if (deleteId != null) {
//			try {
//				new CashBookDAO().delete(Integer.parseInt(deleteId));
//			} catch (Exception e) {
//				/* ignore */ }
//			resp.sendRedirect(req.getContextPath() + "/books?msg=deleted");
//			return;
//		}

		if (selectId != null) {
			// Store selected book in session
			req.getSession().setAttribute("activeBookId", Integer.parseInt(selectId));
			try {
				req.getSession().setAttribute("activeBookName",
						new CashBookDAO().findById(Integer.parseInt(selectId)).getName());
			} catch (NumberFormatException | SQLException e) {
				System.out.println(e.getMessage() );
			}
			resp.sendRedirect(req.getContextPath() + "/home");
			return;
		}

		try {
			CashBookDAO dao = new CashBookDAO();
			List<CashBook> books = dao.findAll();
			req.setAttribute("books", books);

			// Attach summary to each book
			java.util.Map<Integer, java.util.Map<String, java.math.BigDecimal>> summaries = new java.util.LinkedHashMap<>();
			for (CashBook b : books)
				summaries.put(b.getId(), dao.getSummary(b.getId()));
			req.setAttribute("summaries", summaries);

		} catch (Exception e) {
			req.setAttribute("dbError", e.getMessage());
		}
		req.getRequestDispatcher("/WEB-INF/views/books.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String action = req.getParameter("action"); // create | update
		String name = req.getParameter("name");
		String desc = req.getParameter("description");
		String idStr = req.getParameter("id");

		try {
			CashBookDAO dao = new CashBookDAO();
			if ("update".equals(action) && idStr != null) {
				dao.update(Integer.parseInt(idStr), name, desc);
			} else {
				int newId = dao.insert(name, desc);
				// Auto-select newly created book
				req.getSession().setAttribute("activeBookId", newId);
				req.getSession().setAttribute("activeBookName", name.trim());
			}
		} catch (Exception e) {
			/* ignore */ }

		resp.sendRedirect(req.getContextPath() + "/books?msg=saved");
	}
}