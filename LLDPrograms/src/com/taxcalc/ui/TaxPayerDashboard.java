package com.taxcalc.ui;

import com.taxcalc.model.TaxSlab;
import com.taxcalc.model.User;
import com.taxcalc.service.AuthService;
import com.taxcalc.service.TaxSlabManager;
import com.taxcalc.util.InputUtil;
import com.taxcalc.util.PasswordUtil;

import java.util.List;

public class TaxPayerDashboard {

	private final AuthService authService;
	private final TaxSlabManager taxSlabManager;

	public TaxPayerDashboard(AuthService authService, TaxSlabManager taxSlabManager) {
		this.authService = authService;
		this.taxSlabManager = taxSlabManager;
	}

	public void show() {
		boolean running = true;
		while (running) {
			User user = authService.getLoggedInUser();
			ConsoleUI.printHeader("TAXPAYER DASHBOARD — " + user.getUsername().toUpperCase());
			String[] options = { "View My Tax Calculation", "Calculate Tax on Custom Income", "Update My Annual Income",
					"Change My Password", "View Current Tax Slabs", "Logout" };
			ConsoleUI.printMenu(options);
			int choice = InputUtil.readMenuChoice(1, options.length);

			switch (choice) {
			case 1 -> viewMyTax(user);
			case 2 -> customTaxCalc();
			case 3 -> updateIncome(user);
			case 4 -> changePassword(user);
			case 5 -> viewSlabs();
			case 6 -> {
				ConsoleUI.printInfo("Logged out.");
				running = false;
			}
			}
		}
	}

	private void viewMyTax(User user) {
		ConsoleUI.printHeader("MY TAX CALCULATION");
		if (user.getAnnualIncome() <= 0) {
			ConsoleUI.printInfo("Your income is not set. Please update it first.");
			InputUtil.pressEnterToContinue();
			return;
		}
		System.out.printf("  Your Annual Income: ₹%,.2f%n", user.getAnnualIncome());
		AdminDashboard.printTaxBreakdown(user.getAnnualIncome());
		InputUtil.pressEnterToContinue();
	}

	private void customTaxCalc() {
		ConsoleUI.printHeader("CALCULATE TAX ON CUSTOM INCOME");
		double income = InputUtil.readPositiveDouble("  Enter Annual Income (₹): ");
		AdminDashboard.printTaxBreakdown(income);
		InputUtil.pressEnterToContinue();
	}

	private void updateIncome(User user) {
		ConsoleUI.printHeader("UPDATE MY ANNUAL INCOME");
		System.out.printf("  Current Income: ₹%,.2f%n", user.getAnnualIncome());
		double newIncome = InputUtil.readPositiveDouble("  New Annual Income (₹): ");
		user.setAnnualIncome(newIncome);
		authService.updateUser(user);
		ConsoleUI.printSuccess(String.format("Income updated to ₹%,.2f", newIncome));
		InputUtil.pressEnterToContinue();
	}

	private void changePassword(User user) {
		ConsoleUI.printHeader("CHANGE PASSWORD");
		String current = InputUtil.readPassword("  Current Password: ");
		if (!PasswordUtil.verify(user.getUsername(), current, user.getPasswordHash())) {
			ConsoleUI.printError("Incorrect current password.");
			InputUtil.pressEnterToContinue();
			return;
		}
		String newPwd = InputUtil.readPassword("  New Password: ");
		if (newPwd.length() < 4) {
			ConsoleUI.printError("Password must be at least 4 characters.");
			InputUtil.pressEnterToContinue();
			return;
		}
		String confirm = InputUtil.readPassword("  Confirm New Password: ");
		if (!newPwd.equals(confirm)) {
			ConsoleUI.printError("Passwords do not match.");
			InputUtil.pressEnterToContinue();
			return;
		}
		user.setPasswordHash(PasswordUtil.hash(user.getUsername(), newPwd));
		authService.updateUser(user);
		ConsoleUI.printSuccess("Password changed successfully.");
		InputUtil.pressEnterToContinue();
	}

	private void viewSlabs() {
		ConsoleUI.printHeader("CURRENT TAX SLABS");
		List<TaxSlab> slabs = taxSlabManager.getAllSlabs();
		System.out.println();
		System.out.printf("  %-4s  %-25s  %s%n", "No.", "Income Range", "Tax Rate");
		ConsoleUI.printDivider();
		for (int i = 0; i < slabs.size(); i++) {
			System.out.printf("  %-4d  %s%n", i + 1, slabs.get(i));
		}
		InputUtil.pressEnterToContinue();
	}
}
