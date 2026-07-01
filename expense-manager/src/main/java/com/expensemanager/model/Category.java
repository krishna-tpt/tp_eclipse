package com.expensemanager.model;

import java.time.LocalDateTime;

public class Category {
	private int id;
	private String name;
	private String type; // INCOME | EXPENSE
	private Integer bookId; // null = common (visible in all books), non-null = custom for that book
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public Category() {
	}

	public Category(int id, String name, String type, Integer bookId) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.bookId = bookId;
	}

	public boolean isCommon() {
		return bookId == null;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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
}