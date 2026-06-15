package com.expensemanager.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.slf4j.LoggerFactory;

/**
 * GET /log → HTML log viewer page GET /log/stream → SSE stream of log lines
 * (real-time) POST /log/clear → clear in-memory buffer
 */
@WebServlet(urlPatterns = { "/log", "/log/stream", "/log/clear" })
public class LogServlet extends HttpServlet {

	// ── In-memory ring buffer (last 500 lines) ─────────────────────
	private static final int MAX_LINES = 500;
	private static final CopyOnWriteArrayList<String> LOG_BUFFER = new CopyOnWriteArrayList<>();
	private static volatile long lastSeq = 0;

	// ── Active SSE clients ─────────────────────────────────────────
	private static final List<PrintWriter> SSE_CLIENTS = Collections.synchronizedList(new ArrayList<>());

	// ── Logback in-memory appender ─────────────────────────────────
	private static InMemoryAppender appender;

	@Override
	public void init() throws ServletException {
		LoggerContext ctx = (LoggerContext) LoggerFactory.getILoggerFactory();
		appender = new InMemoryAppender();
		appender.setContext(ctx);
		appender.setName("IN_MEMORY");
		appender.start();

		// Attach to root logger
		Logger root = ctx.getLogger(Logger.ROOT_LOGGER_NAME);
		root.addAppender(appender);
	}

	@Override
	public void destroy() {
		if (appender != null)
			appender.stop();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String uri = req.getRequestURI();

		if (uri.endsWith("/stream")) {
			handleStream(req, resp);
		} else {
			handlePage(req, resp);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOG_BUFFER.clear();
		lastSeq = 0;
		resp.sendRedirect(req.getContextPath() + "/log");
	}

	// ── SSE stream ─────────────────────────────────────────────────
	private void handleStream(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		resp.setContentType("text/event-stream");
		resp.setCharacterEncoding("UTF-8");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setHeader("Connection", "keep-alive");
		resp.setHeader("X-Accel-Buffering", "no"); // nginx: disable buffering

		PrintWriter out = resp.getWriter();
		SSE_CLIENTS.add(out);

		// Send existing buffer first
		List<String> snapshot = new ArrayList<>(LOG_BUFFER);
		for (String line : snapshot) {
			out.write("data: " + escapeSSE(line) + "\n\n");
		}
		out.flush();

		// Keep connection alive until client disconnects
		try {
			while (!out.checkError()) {
				Thread.sleep(500);
				out.write(": ping\n\n"); // SSE comment = keepalive
				out.flush();
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			SSE_CLIENTS.remove(out);
		}
	}

	// ── HTML page ──────────────────────────────────────────────────
	private void handlePage(HttpServletRequest req, HttpServletResponse resp) 
	        throws ServletException, IOException {
	    req.setAttribute("pageTitle",   "Application Log");
	    req.setAttribute("activePage",  "log");
	    req.setAttribute("currentYear", java.time.Year.now().getValue()); // ←add this
	    req.getRequestDispatcher("/WEB-INF/views/log.jsp").forward(req, resp);
	}

	// ── Called by appender to push new log line ────────────────────
	static void pushLine(String line) {
		LOG_BUFFER.add(line);
		if (LOG_BUFFER.size() > MAX_LINES) {
			LOG_BUFFER.remove(0);
		}
		lastSeq++;

		// Broadcast to SSE clients
		String sseMsg = "data: " + escapeSSE(line) + "\n\n";
		synchronized (SSE_CLIENTS) {
			SSE_CLIENTS.removeIf(w -> {
				w.write(sseMsg);
				w.flush();
				return w.checkError(); // remove if disconnected
			});
		}
	}

	private static String escapeSSE(String s) {
		// SSE data lines can't contain raw newlines
		return s.replace("\r\n", " ").replace("\n", " ").replace("\r", " ");
	}

	// ── Logback Appender ──────────────────────────────────────────
	public static class InMemoryAppender extends AppenderBase<ILoggingEvent> {

		private static final String[] LEVEL_COLORS = {
				// TRACE, DEBUG, INFO, WARN, ERROR
		};

		@Override
		protected void append(ILoggingEvent event) {
			String level = event.getLevel().toString();
			String logger = event.getLoggerName();
			// shorten logger: com.expensemanager.servlet.HomeServlet → HomeServlet
			int dot = logger.lastIndexOf('.');
			String shortLog = dot >= 0 ? logger.substring(dot + 1) : logger;
			String thread = event.getThreadName();
			String msg = event.getFormattedMessage();

			// Format: HH:mm:ss LEVEL [thread] Logger — message
			String time = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(event.getTimeStamp()));

			String line = String.format("%s %-5s [%s] %s — %s", time, level, thread, shortLog, msg);

			// Append throwable if present
			if (event.getThrowableProxy() != null) {
				line += " | " + event.getThrowableProxy().getClassName() + ": "
						+ event.getThrowableProxy().getMessage();
			}

			LogServlet.pushLine(line);
		}
	}
}
