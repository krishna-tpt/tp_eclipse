package com.expensemanager.util;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Every request (except /books, /settings, static assets) must have an active
 * book in session, AND that book must be active (is_active=true). If not →
 * redirect to /books with a warning.
 */
@WebFilter("/*")
public class BookFilter implements Filter {
	private static final String[] BYPASS = { "/books", "/css/", "/js/", "/favicon", "/backup", "/log", "/schedulers" };

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		String path = request.getServletPath();

		for (String b : BYPASS) {
			if (path.startsWith(b)) {
				chain.doFilter(req, res);
				return;
			}
		}

		HttpSession session = request.getSession();
		Integer bookId = (Integer) session.getAttribute("activeBookId");
		if (bookId == null || bookId <= 0) {
			response.sendRedirect(request.getContextPath() + "/books");
			return;
		}

		Boolean active = (Boolean) session.getAttribute("activeBookActive");
		if (active == null || !active) {
			session.removeAttribute("activeBookId");
			session.removeAttribute("activeBookName");
			session.removeAttribute("activeBookActive");
			response.sendRedirect(request.getContextPath() + "/books?msg=inactive");
			return;
		}

		chain.doFilter(req, res);
	}
}