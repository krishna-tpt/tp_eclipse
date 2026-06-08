package com.expensemanager.servlet;

import com.expensemanager.dao.TransactionDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@WebServlet("/calendar")
public class CalendarServlet extends HttpServlet {

	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		int bookId = (Integer) req.getSession().getAttribute("activeBookId");

		// Default to current month
		LocalDate today = LocalDate.now();
		String yrStr = req.getParameter("year");
		String moStr = req.getParameter("month");
		int year = yrStr != null ? Integer.parseInt(yrStr) : today.getYear();
		int month = moStr != null ? Integer.parseInt(moStr) : today.getMonthValue();

		try {
			TransactionDAO dao = new TransactionDAO();
			List<Map<String, Object>> daily = dao.dailyTotals(year, month, bookId);

			req.setAttribute("dailyJson", mapper.writeValueAsString(daily));
			req.setAttribute("year", year);
			req.setAttribute("month", month);
			req.setAttribute("today", today.toString());
		} catch (Exception e) {
			req.setAttribute("dbError", e.getMessage());
		}
		req.getRequestDispatcher("/WEB-INF/views/calendar.jsp").forward(req, resp);
	}
}