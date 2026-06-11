package com.expensemanager.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Holds all filter criteria for transaction search. Built from HTTP request
 * params, passed to TransactionDAO.
 */
public class TransactionFilter {

	// Basic
	private String type; // INCOME | EXPENSE | null = all
	private Integer bookId;

	// Date range
	private LocalDate dateFrom;
	private LocalDate dateTo;

	// Category / SubCategory
	private Integer categoryId;
	private Integer subCategoryId;

	// Amount conditions
	private String amountOp1; // =, >, >=, <, <=
	private BigDecimal amount1;
	private String amountOp2; // >=, <= (second condition for range)
	private BigDecimal amount2;

	// Text search (note + custom fields)
	private String noteSearch;

	// Pagination
	private int page = 1;
	private int pageSize = 15;

	// ── Getters / Setters ──────────────────────────────────
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getBookId() {
		return bookId;
	}

	public void setBookId(Integer bookId) {
		this.bookId = bookId;
	}

	public LocalDate getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(LocalDate dateFrom) {
		this.dateFrom = dateFrom;
	}

	public LocalDate getDateTo() {
		return dateTo;
	}

	public void setDateTo(LocalDate dateTo) {
		this.dateTo = dateTo;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getSubCategoryId() {
		return subCategoryId;
	}

	public void setSubCategoryId(Integer subCategoryId) {
		this.subCategoryId = subCategoryId;
	}

	public String getAmountOp1() {
		return amountOp1;
	}

	public void setAmountOp1(String amountOp1) {
		this.amountOp1 = amountOp1;
	}

	public BigDecimal getAmount1() {
		return amount1;
	}

	public void setAmount1(BigDecimal amount1) {
		this.amount1 = amount1;
	}

	public String getAmountOp2() {
		return amountOp2;
	}

	public void setAmountOp2(String amountOp2) {
		this.amountOp2 = amountOp2;
	}

	public BigDecimal getAmount2() {
		return amount2;
	}

	public void setAmount2(BigDecimal amount2) {
		this.amount2 = amount2;
	}

	public String getNoteSearch() {
		return noteSearch;
	}

	public void setNoteSearch(String noteSearch) {
		this.noteSearch = noteSearch;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/** True if any filter is active (besides type and book) */
	public boolean isFiltered() {
		return dateFrom != null || dateTo != null || categoryId != null || subCategoryId != null || amount1 != null
				|| (noteSearch != null && !noteSearch.isBlank());
	}

	/** Build SQL safe amount operator */
	public static String safeOp(String op) {
		return switch (op != null ? op.trim() : "") {
		case ">=" -> ">=";
		case "<=" -> "<=";
		case ">" -> ">";
		case "<" -> "<";
		default -> "=";
		};
	}
}