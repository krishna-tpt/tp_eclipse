package com.expensemanager.util;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Every request (except /books, /settings, static assets) must have an active
 * book in session. If not → redirect to /books.
 */
@WebFilter("/*")
public class BookFilter implements Filter {

	private static final String[] BYPASS = { "/books", "/settings", "/css/", "/js/", "/favicon", "/receipt" };

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		String path = request.getServletPath();

		// Always allow bypassed paths
		for (String b : BYPASS) {
			if (path.startsWith(b)) {
				chain.doFilter(req, res);
				return;
			}
		}

		// Check session for active book
		Integer bookId = (Integer) request.getSession().getAttribute("activeBookId");
		if (bookId == null || bookId <= 0) {
			response.sendRedirect(request.getContextPath() + "/books");
			return;
		}

		chain.doFilter(req, res);
	}
}
