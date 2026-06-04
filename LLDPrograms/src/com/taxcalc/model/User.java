package com.taxcalc.model;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	private String username;
	private String passwordHash;
	private Role role;
	private double annualIncome;

	public enum Role {
		ADMIN, TAXPAYER
	}

	public User(String username, String passwordHash, Role role, double annualIncome) {
		this.username = username;
		this.passwordHash = passwordHash;
		this.role = role;
		this.annualIncome = annualIncome;
	}

	public String getUsername() {
		return username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public Role getRole() {
		return role;
	}

	public double getAnnualIncome() {
		return annualIncome;
	}

	public void setAnnualIncome(double annualIncome) {
		this.annualIncome = annualIncome;
	}

	public void setPasswordHash(String hash) {
		this.passwordHash = hash;
	}

	@Override
	public String toString() {
		return String.format("Username: %-15s | Role: %-10s | Income: ₹%.2f", username, role, annualIncome);
	}
}
