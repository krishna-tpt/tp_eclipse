package com.expensemanager.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expensemanager.dao.CashBookDAO;
import com.expensemanager.model.CashBook;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * /books GET → list all books /books POST → create new book /books?edit=id GET
 * → edit form /books?delete=id GET → delete /books/select?id=X GET → set active
 * book in session
 */
@WebServlet("/books")
public class CashBookServlet extends HttpServlet {
	private static final Logger log = LoggerFactory.getLogger(CashBookServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String deleteId = req.getParameter("delete");
		String selectId = req.getParameter("select");

		if (deleteId != null) {
			try {
				new CashBookDAO().delete(Integer.parseInt(deleteId));
			} catch (Exception e) {
				log.info("doGet method Exception delete operation : {}", e.getMessage());
			}
			resp.sendRedirect(req.getContextPath() + "/books?msg=deleted");
			return;
		}
		if (selectId != null) {
			try {
				CashBook book = new CashBookDAO().findById(Integer.parseInt(selectId));
				if (book == null) {
					resp.sendRedirect(req.getContextPath() + "/books?msg=notfound");
					return;
				}
				if (!book.isActive()) {
					resp.sendRedirect(req.getContextPath() + "/books?msg=inactive");
					return;
				}
				req.getSession().setAttribute("activeBookId", book.getId());
				req.getSession().setAttribute("activeBookName", book.getName());
				req.getSession().setAttribute("activeBookActive", true);
			} catch (NumberFormatException | SQLException e) {
				log.info("doGet method Exception Get operation : {}", e.getMessage());
			}
			resp.sendRedirect(req.getContextPath() + "/home");
			return;
		}

		// Fresh navigation to /books (e.g. clicking the "Books" nav link, or the
		// book switcher's "Manage Books"), with no other query params — force the
		// user to explicitly reselect a book. Flows that redirect back here with a
		// param (msg=saved/deleted, search=, sort=) are NOT touched, so create/update
		// results and filters keep working normally.
		if (req.getParameterMap().isEmpty()) {
			HttpSession session = req.getSession();
			session.removeAttribute("activeBookId");
			session.removeAttribute("activeBookName");
			session.removeAttribute("activeBookActive");
		}

		String search = req.getParameter("search");
		String sort = req.getParameter("sort");

		try {
			CashBookDAO dao = new CashBookDAO();
			List<CashBook> books = dao.findAll(search, sort);
			req.setAttribute("books", books);
			req.setAttribute("search", search);
			req.setAttribute("sort", sort);

			Map<Integer, Map<String, java.math.BigDecimal>> summaries = new LinkedHashMap<>();
			for (CashBook b : books)
				summaries.put(b.getId(), dao.getSummary(b.getId()));
			req.setAttribute("summaries", summaries);
		} catch (Exception e) {
			log.info("doGet method Exception summaries operation : {}", e.getMessage());
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
		boolean active = "true".equals(req.getParameter("active"));

		try {
			CashBookDAO dao = new CashBookDAO();
			if ("update".equals(action) && idStr != null) {
				int id = Integer.parseInt(idStr);
				dao.update(id, name, desc, active);

				// If the book being edited is the currently active one, sync session flag
				HttpSession session = req.getSession();
				Integer activeBookId = (Integer) session.getAttribute("activeBookId");
				if (activeBookId != null && activeBookId == id) {
					session.setAttribute("activeBookActive", active);
					if (!active) {
						// currently-active book was just turned inactive → force reselect
						session.removeAttribute("activeBookId");
						session.removeAttribute("activeBookName");
					} else {
						session.setAttribute("activeBookName", name.trim());
					}
				}
			} else {
				int newId = dao.insert(name, desc);
				req.getSession().setAttribute("activeBookId", newId);
				req.getSession().setAttribute("activeBookName", name.trim());
				req.getSession().setAttribute("activeBookActive", true);
			}
		} catch (Exception e) {
			log.info("doPost method Exception operation : {}", e.getMessage());
		}
		resp.sendRedirect(req.getContextPath() + "/books?msg=saved");
	}
}
