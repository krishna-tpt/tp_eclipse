package com.expensemanager.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Groups a list of transactions by calendar day, computing the day's income /
 * expense subtotals along the way — used by the dashboard to render a
 * day-by-day view (like the mobile app's Daily tab) instead of a flat table.
 *
 * NOTE: pass in a list that is already sorted by date (desc or asc) — if the
 * list jumps around (e.g. sorted by amount or category), each non-contiguous
 * appearance of the same date will form its own separate group.
 */
public class DayGroup {

	private static final DateTimeFormatter DOW_FMT = DateTimeFormatter.ofPattern("EEE");
	private static final DateTimeFormatter MY_FMT = DateTimeFormatter.ofPattern("MM.yyyy");

	private final LocalDate date;
	private final List<Transaction> transactions;
	private final BigDecimal income;
	private final BigDecimal expense;

	public DayGroup(LocalDate date, List<Transaction> transactions, BigDecimal income, BigDecimal expense) {
		this.date = date;
		this.transactions = transactions;
		this.income = income;
		this.expense = expense;
	}

	/** Groups transactions by LocalDate, preserving the input's order. */
	public static List<DayGroup> groupByDay(List<Transaction> txns) {
		Map<LocalDate, List<Transaction>> byDate = new LinkedHashMap<>();
		for (Transaction t : txns) {
			LocalDate d = t.getDateTime().toLocalDate();
			byDate.computeIfAbsent(d, k -> new ArrayList<>()).add(t);
		}

		List<DayGroup> groups = new ArrayList<>();
		for (Map.Entry<LocalDate, List<Transaction>> e : byDate.entrySet()) {
			BigDecimal inc = BigDecimal.ZERO;
			BigDecimal exp = BigDecimal.ZERO;
			for (Transaction t : e.getValue()) {
				if (t.getType() == Transaction.Type.INCOME) {
					inc = inc.add(t.getAmount());
				} else {
					exp = exp.add(t.getAmount());
				}
			}
			groups.add(new DayGroup(e.getKey(), e.getValue(), inc, exp));
		}
		return groups;
	}

	public LocalDate getDate() {
		return date;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public BigDecimal getIncome() {
		return income;
	}

	public BigDecimal getExpense() {
		return expense;
	}

	public BigDecimal getNet() {
		return income.subtract(expense);
	}

	public int getDayOfMonth() {
		return date.getDayOfMonth();
	}

	public String getDayOfWeek() {
		return date.format(DOW_FMT);
	}

	public String getMonthYear() {
		return date.format(MY_FMT);
	}
}
