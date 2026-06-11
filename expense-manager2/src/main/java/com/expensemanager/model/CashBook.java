package com.expensemanager.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CashBook {
	private int id;
	private String name;
	private String description;
	private LocalDateTime createdAt;
	private boolean active;

	public CashBook() {
	}

	public CashBook(int id, String name, String description, LocalDateTime createdAt, boolean active) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.createdAt = createdAt;
		this.active = active;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getFormattedDate() {
		if (createdAt == null)
			return "";
		return createdAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
	}
}
