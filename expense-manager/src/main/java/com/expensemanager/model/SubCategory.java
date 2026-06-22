package com.expensemanager.model;

import java.time.LocalDateTime;

public class SubCategory {

	private int id;
	private String name;
	private int category_id;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public SubCategory() {
	}

	public SubCategory(int id, String name, int category_id) {
		this.id = id;
		this.name = name;
		this.category_id = category_id;
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

	public int getCategory_id() {
		return category_id;
	}

	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}
	

}