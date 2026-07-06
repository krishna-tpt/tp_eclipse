package com.expensemanager.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expensemanager.dao.BudgetDAO;
import com.expensemanager.dao.SchedulerDAO;
import com.expensemanager.model.BackupMetadata.BackupMode;
import com.expensemanager.model.BackupMetadata.BackupType;
import com.expensemanager.model.Budget;
import com.expensemanager.model.SchedulerConfig;
import com.expensemanager.util.DBConnection;

/**
 * Central scheduler engine — checks every minute if any scheduler is due, then
 * executes the appropriate job.
 */
public class SchedulerEngine {

	private static final Logger log = LoggerFactory.getLogger(SchedulerEngine.class);
	private static SchedulerEngine INSTANCE;
	private ScheduledExecutorService executor;
	private final SchedulerDAO dao = new SchedulerDAO();

	public static synchronized SchedulerEngine getInstance() {
		if (INSTANCE == null)
			INSTANCE = new SchedulerEngine();
		return INSTANCE;
	}

	// ── Start polling every 60 seconds ─────────────────────────────
	public void start() {
		executor = Executors.newSingleThreadScheduledExecutor(r -> {
			Thread t = new Thread(r, "SchedulerEngine");
			t.setDaemon(true);
			return t;
		});
		executor.scheduleAtFixedRate(this::tick, 10, 60, TimeUnit.SECONDS);
		log.info("[SchedulerEngine] Started — polling every 60s");
	}

	public void stop() {
		if (executor != null)
			executor.shutdownNow();
		log.info("[SchedulerEngine] Stopped");
	}

	// ── Tick — called every minute ─────────────────────────────────
	private void tick() {
		try {
			List<SchedulerConfig> all = dao.findAll();
			LocalDateTime now = LocalDateTime.now();
			for (SchedulerConfig s : all) {
				if (!s.isEnabled())
					continue;
				if (isDue(s, now)) {
					log.info("[SchedulerEngine] Running: {}", s.getName());
					runAsync(s);
				}
			}
		} catch (Exception e) {
			log.error("[SchedulerEngine] Tick error: {}", e.getMessage(), e);
		}
	}

	// ── Check if scheduler is due ──────────────────────────────────
	private boolean isDue(SchedulerConfig s, LocalDateTime now) {
		// Use next_run_at if available
		if (s.getNextRunAt() != null) {
			return !now.isBefore(s.getNextRunAt());
		}
		// Fallback: time-of-day match
		LocalTime runTime = LocalTime.of(s.getRunHour(), s.getRunMinute());
		LocalTime nowTime = now.toLocalTime();
		if (Math.abs(nowTime.toSecondOfDay() - runTime.toSecondOfDay()) > 90)
			return false;

		return switch (s.getRepeatType()) {
		case "DAILY" -> true;
		case "WEEKLY" -> isWeekDay(s.getRepeatDays(), now);
		case "MONTHLY" -> isMonthDay(s.getRepeatDays(), now);
		default -> false;
		};
	}

	private boolean isWeekDay(String days, LocalDateTime now) {
		if (days == null)
			return false;
		String today = now.getDayOfWeek().name().substring(0, 3); // MON,TUE...
		return days.toUpperCase().contains(today);
	}

	private boolean isMonthDay(String days, LocalDateTime now) {
		if (days == null)
			return false;
		try {
			return Integer.parseInt(days.trim()) == now.getDayOfMonth();
		} catch (Exception e) {
			return false;
		}
	}

	// ── Run in background thread ───────────────────────────────────
	private void runAsync(SchedulerConfig s) {
		executor.submit(() -> execute(s));
	}

	// ── Manual "Run Now" trigger ───────────────────────────────────
	public void runNow(String schedulerName) throws Exception {
		SchedulerConfig s = dao.findByName(schedulerName);
		if (s == null)
			throw new Exception("Scheduler not found: " + schedulerName);
		executor.submit(() -> execute(s));
	}

	// ── Execute a specific scheduler ───────────────────────────────
	private void execute(SchedulerConfig s) {
		int logId = -1;
		LocalDateTime oneWeekAgo = null;
		LocalDateTime lastRun = null;
//		log.debug("oneWeekAgo : {} - lastRun : {}", oneWeekAgo, lastRun);
		LocalDateTime fromDate = null;
		log.debug("Execute Name : {}", s.getName());

		try {
			logId = dao.logStart(s.getId());

			String result;
			int rows = 0;

			switch (s.getName()) {
			case "BACKUP" -> result = runBackup();
			case "CASHBOOK" -> {
				var r = runCashBook();
				result = r[0];
				rows = Integer.parseInt(r[1]);
			}
			case "BUDGET" -> {
				var r = runBudget();
				result = r[0];
				rows = Integer.parseInt(r[1]);
			}
			case "NEON_SYNC_PUSH" -> {
				oneWeekAgo = LocalDateTime.now().minusDays(7);
				lastRun = s.getLastRunAt();
				// First-ever run for this scheduler: lastRunAt is null.
				// Without this guard, lastRun.isBefore(...) below throws
				// an NPE and the sync silently fails every time.
				if (lastRun == null)
					lastRun = oneWeekAgo;
				fromDate = lastRun.isBefore(oneWeekAgo) ? lastRun : oneWeekAgo;

				var sr = runNeonSync(fromDate, true);
				// sync() swallows its own exceptions and just returns
				// success=false — it never throws. Without this check,
				// execute() falls through to logFinish(..., "SUCCESS", ...)
				// below even when the push actually failed, hiding the
				// real failure and letting last_run_at advance anyway.
				if (!sr.success)
					throw new Exception(sr.error);
				result = sr.getSummary();
				rows = sr.totalRows;
			}
			case "NEON_SYNC_PULL" -> {
				oneWeekAgo = LocalDateTime.now().minusDays(7);
				lastRun = s.getLastRunAt();
				if (lastRun == null)
					lastRun = oneWeekAgo;
				fromDate = lastRun.isBefore(oneWeekAgo) ? lastRun : oneWeekAgo;

				var sr = runNeonSync(fromDate, false);
				if (!sr.success)
					throw new Exception(sr.error);
				result = sr.getSummary();
				rows = sr.totalRows;
			}
			default -> result = "Unknown scheduler: " + s.getName();
			}

			LocalDateTime nextRun = calcNextRun(s);
			dao.logFinish(logId, s.getId(), "SUCCESS", result, rows, nextRun);
			log.info("[SchedulerEngine] {} completed: {}", s.getName(), result);

		} catch (Exception ex) {
			log.error("[SchedulerEngine] {} failed: {}", s.getName(), ex.getMessage(), ex);
			try {
				LocalDateTime nextRun = calcNextRun(s);
				dao.logFinish(logId, s.getId(), "FAILED", ex.getMessage(), 0, nextRun);
				log.info("[SchedulerEngine] - Exception: {}", ex.getMessage());
			} catch (Exception ignored) {
				log.info("[SchedulerEngine] - ex Exception: {}", ignored.getMessage());
			}
		}
	}

	// ── CASHBOOK: create cash book for next month if not exists ────
	private String[] runCashBook() throws Exception {
		LocalDate nextMonth = LocalDate.now().withDayOfMonth(1);
		String name = nextMonth.getMonth().name() + " " + nextMonth.getYear();
		Connection conn = DBConnection.getInstance().getConnection();
		log.info("[CashBook] name: {}", name);
		try {
			// Check if exists
			try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM cash_books WHERE name=?")) {
				ps.setString(1, name);
				ResultSet rs = ps.executeQuery();
				if (rs.next())
					return new String[] { "Cash book already exists: " + name, "0" };
			}
			// Create
			try (PreparedStatement ps = conn.prepareStatement("INSERT INTO cash_books (name) VALUES (?)")) {
				ps.setString(1, name);
				ps.executeUpdate();
			}
			log.info("[CashBook] Created: {}", name);
			return new String[] { "Created cash book: " + name, "1" };
		} finally {
			DBConnection.getInstance().releaseConnection(conn);
		}
	}

	// ── BUDGET: copy this month's budget to next month ─────────────
	private String[] runBudget() throws Exception {
		LocalDate now = LocalDate.now();
		int thisYear = now.getYear();
		int thisMonth = now.getMonthValue();
		int nextYear = now.plusMonths(1).getYear();
		int nextMonth = now.plusMonths(1).getMonthValue();

		BudgetDAO budgetDAO = new BudgetDAO();

		// Get all book IDs
		Connection conn = DBConnection.getInstance().getConnection();
		int created = 0;
		try {
			try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM cash_books");
					ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					int bookId = rs.getInt(1);
					Budget cur = budgetDAO.findByMonth(bookId, thisYear, thisMonth);
					if (cur == null)
						continue;

					// Check if next month budget already exists
					Budget existing = budgetDAO.findByMonth(bookId, nextYear, nextMonth);
					if (existing != null)
						continue;

					// Copy this month → next month
					Budget next = new Budget();
					next.setBookId(bookId);
					next.setYear(nextYear);
					next.setMonth(nextMonth);
					next.setOverallLimit(cur.getOverallLimit());
					int newBudgetId = budgetDAO.upsert(next);

					// Copy category limits
					if (cur.getCategories() != null) {
						for (var bc : cur.getCategories()) {
							var newBc = new com.expensemanager.model.BudgetCategory();
							newBc.setBudgetId(newBudgetId);
							newBc.setCategoryId(bc.getCategoryId());
							newBc.setCatLimit(bc.getCatLimit());
							newBc.setAlertPct(bc.getAlertPct());
							budgetDAO.upsertCategory(newBc);
						}
					}
					created++;
				}
			}
		} finally {
			DBConnection.getInstance().releaseConnection(conn);
		}
		return new String[] { "Budget assigned for " + nextYear + "/" + nextMonth + " (" + created + " books)",
				String.valueOf(created) };
	}

	// ── BACKUP: delegate to existing BackupService ─────────────────
	private String runBackup() throws Exception {
		// Reuse existing BackupService
		com.expensemanager.service.BackupService svc = new com.expensemanager.service.BackupService();
		var meta = svc.createBackup("Scheduled daily backup", BackupType.SCHEDULED, BackupMode.ONLINE);
		return "Backup created: " + meta.getFileName() + " (" + meta.getFileSizeBytes() + " bytes)";
	}

	// ── NEON_SYNC ──────────────────────────────────────────────────
	private NeonSyncService.SyncResult runNeonSync(LocalDateTime lastRunAt, Boolean isPush) {
		return new NeonSyncService().sync(lastRunAt, isPush);
	}

	// ── Calculate next run time ────────────────────────────────────
	private LocalDateTime calcNextRun(SchedulerConfig s) {
		LocalDate today = LocalDate.now();
		LocalTime runTime = LocalTime.of(s.getRunHour(), s.getRunMinute());

		return switch (s.getRepeatType()) {
		case "DAILY" -> LocalDateTime.of(today.plusDays(1), runTime);
		case "WEEKLY" -> {
			// Find next matching weekday
			LocalDate d = today.plusDays(1);
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
			LocalDate next = today.plusMonths(1).withDayOfMonth(dom);
			yield LocalDateTime.of(next, runTime);
		}
		default -> LocalDateTime.of(today.plusDays(1), runTime);
		};
	}
}
