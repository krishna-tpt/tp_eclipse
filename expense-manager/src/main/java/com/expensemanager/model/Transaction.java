package com.expensemanager.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class Transaction {

	public enum Type {
		INCOME, EXPENSE
	}

	private int id;
	private Type type;
	private LocalDateTime dateTime;
	private BigDecimal amount;
	private int categoryId;
	private String categoryName;
	private String note;
	private int subcategoryid;
	private String subCategoryName;
	private int bookId;
	private Map<String, String> customValues = new LinkedHashMap<>();

	public Transaction() {
	}

	public String getFormattedDate() {
		if (dateTime == null)
			return "";
		return dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
	}

	public String getFormattedDateTime() {
		if (dateTime == null)
			return "";
		return dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));
	}

	// Getters / Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public int getSubcategoryid() {
		return subcategoryid;
	}

	public void setSubcategoryid(int subcategoryid) {
		this.subcategoryid = subcategoryid;
	}

	public String getSubCategoryName() {
		return subCategoryName;
	}

	public void setSubCategoryName(String subCategoryName) {
		this.subCategoryName = subCategoryName;
	}

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public Map<String, String> getCustomValues() {
		return customValues;
	}

	public void setCustomValues(Map<String, String> customValues) {
		this.customValues = customValues;
	}

	public void addCustomValue(String key, String value) {
		this.customValues.put(key, value);
	}
}