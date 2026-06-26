package com.expensemanager.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expensemanager.dao.SchedulerDAO;
import com.expensemanager.model.SchedulerConfig;
import com.expensemanager.model.SchedulerLog;
import com.expensemanager.service.SchedulerEngine;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/schedulers")
public class SchedulerServlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(SchedulerServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		SchedulerDAO dao = new SchedulerDAO();
		int page = 1;
		try {
			String p = req.getParameter("logPage");
			if (p != null)
				page = Integer.parseInt(p);
		} catch (Exception ignored) {
		}

		int pageSize = 15;
		int offset = (page - 1) * pageSize;

		List<SchedulerLog> logs = null;
		int totalLogs = 0;
		try {
//			dao.insertscheduler();
			logs = dao.allRecentLogs(pageSize, offset);
			totalLogs = dao.countAllLogs(); // total count
		} catch (SQLException e) {
			log.debug("doGet method SQLException : {}", e.getMessage());
		}

		int totalPages = (int) Math.ceil((double) totalLogs / pageSize);

		try {
			req.setAttribute("schedulers", dao.findAll());
			req.setAttribute("recentLogs", logs);
			req.setAttribute("logPage", page);
			req.setAttribute("logTotalPages", totalPages);
			req.setAttribute("logTotal", totalLogs);
//			req.setAttribute("recentLogs", dao.allRecentLogs(50));
		} catch (Exception e) {
			req.setAttribute("dbError", e.getMessage());
		}
		req.setAttribute("pageTitle", "Schedulers");
		req.setAttribute("activePage", "schedulers");
		req.getRequestDispatcher("/WEB-INF/views/schedulers.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");
		String action = req.getParameter("action");

		// ── Run Now ───────────────────────────────────────────────
		if ("runNow".equals(action)) {
			String name = req.getParameter("name");
			try {
				SchedulerEngine.getInstance().runNow(name);
				resp.sendRedirect(req.getContextPath() + "/schedulers?msg=started&name=" + name);
			} catch (Exception e) {
				resp.sendRedirect(req.getContextPath() + "/schedulers?error="
						+ java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
			}
			return;
		}

		// ── Save config ───────────────────────────────────────────
		if ("save".equals(action)) {
			SchedulerDAO dao = new SchedulerDAO();
			try {
				int id = Integer.parseInt(req.getParameter("id"));
				boolean enabled = "on".equals(req.getParameter("enabled"))
						|| "true".equals(req.getParameter("enabled"));
				String repeatType = req.getParameter("repeatType");
				int runHour = Integer.parseInt(req.getParameter("runHour"));
				int runMinute = Integer.parseInt(req.getParameter("runMinute"));

				// Build repeatDays
				String repeatDays = null;
				if ("WEEKLY".equals(repeatType)) {
					String[] days = req.getParameterValues("weekDays");
					if (days != null)
						repeatDays = String.join(",", days);
				} else if ("MONTHLY".equals(repeatType)) {
					repeatDays = req.getParameter("monthDay");
				}

				SchedulerConfig s = dao.findAll().stream().filter(x -> x.getId() == id).findFirst().orElse(null);
				if (s != null) {
					s.setEnabled(enabled);
					s.setRepeatType(repeatType);
					s.setRepeatDays(repeatDays);
					s.setRunHour(runHour);
					s.setRunMinute(runMinute);
					// Recalculate next run
					s.setNextRunAt(calcNextRun(s));
					dao.update(s);
				}
				resp.sendRedirect(req.getContextPath() + "/schedulers?success=saved");
			} catch (Exception e) {
				log.error("SchedulerServlet save error: {}", e.getMessage(), e);
				resp.sendRedirect(req.getContextPath() + "/schedulers?error="
						+ java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
			}
			return;
		}

		resp.sendRedirect(req.getContextPath() + "/schedulers");
	}

	private LocalDateTime calcNextRun(SchedulerConfig s) {
		java.time.LocalDate today = java.time.LocalDate.now();
		LocalTime runTime = LocalTime.of(s.getRunHour(), s.getRunMinute());
		return switch (s.getRepeatType()) {
		case "WEEKLY" -> {
			java.time.LocalDate d = today.plusDays(1);
			for (int i = 0; i < 7; i++, d = d.plusDays(1)) {
				String day = d.getDayOfWeek().name().substring(0, 3);
				if (s.getRepeatDays() != null && s.getRepeatDays().toUpperCase().contains(day))
					yield LocalDateTime.of(d, runTime);
			}
			yield LocalDateTime.of(today.plusDays(7), runTime);
		}
		case "MONTHLY" -> {
			int dom = 1;
			try {
				dom = Integer.parseInt(s.getRepeatDays().trim());
			} catch (Exception ignored) {
			}
			yield LocalDateTime.of(today.plusMonths(1).withDayOfMonth(dom), runTime);
		}
		default -> LocalDateTime.of(today.plusDays(1), runTime);
		};
	}
}
//