package com.taxcalc.ui;

import com.taxcalc.model.TaxSlab;
import com.taxcalc.model.User;
import com.taxcalc.service.AuthService;
import com.taxcalc.service.TaxSlabManager;
import com.taxcalc.util.InputUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminDashboard {

	private final AuthService authService;
	private final TaxSlabManager taxSlabManager;

	public AdminDashboard(AuthService authService, TaxSlabManager taxSlabManager) {
		this.authService = authService;
		this.taxSlabManager = taxSlabManager;
	}

	public void show() {
		boolean running = true;
		while (running) {
			ConsoleUI.printHeader("ADMIN DASHBOARD — " + authService.getLoggedInUser().getUsername().toUpperCase());
			String[] options = { "Register New User / Admin", "View All Users", "Delete a User", "View Tax Slabs",
					"Add New Tax Slab", "Edit Existing Tax Slab", "Delete a Tax Slab", "Reset to Default Tax Slabs",
					"Calculate Tax for Any User", "Logout" };
			ConsoleUI.printMenu(options);
			int choice = InputUtil.readMenuChoice(1, options.length);

			switch (choice) {
			case 1 -> registerUser();
			case 2 -> viewAllUsers();
			case 3 -> deleteUser();
			case 4 -> viewTaxSlabs();
			case 5 -> addTaxSlab();
			case 6 -> editTaxSlab();
			case 7 -> deleteTaxSlab();
			case 8 -> resetDefaultSlabs();
			case 9 -> calculateTaxForUser();
			case 10 -> {
				ConsoleUI.printInfo("Logged out.");
				running = false;
			}
			}
		}
	}

	// ────────────────────────────────────────────
	// 1. Register
	// ────────────────────────────────────────────
	private void registerUser() {
		ConsoleUI.printHeader("REGISTER NEW USER / ADMIN");

		String username = InputUtil.readString("  Username: ").toLowerCase();
		if (authService.findUser(username).isPresent()) {
			ConsoleUI.printError("Username '" + username + "' already exists.");
			InputUtil.pressEnterToContinue();
			return;
		}

		String password = InputUtil.readPassword("  Password: ");
		if (password.length() < 4) {
			ConsoleUI.printError("Password must be at least 4 characters.");
			InputUtil.pressEnterToContinue();
			return;
		}

		System.out.println("\n  Role:");
		System.out.println("    [1] Admin");
		System.out.println("    [2] Tax Payer");
		int roleChoice = InputUtil.readMenuChoice(1, 2);
		User.Role role = (roleChoice == 1) ? User.Role.ADMIN : User.Role.TAXPAYER;

		double income = 0;
		if (role == User.Role.TAXPAYER) {
			income = InputUtil.readPositiveDouble("  Annual Income (₹): ");
		}

		boolean ok = authService.registerUser(username, password, role, income);
		if (ok) {
			ConsoleUI.printSuccess("User '" + username + "' registered successfully as " + role + ".");
		} else {
			ConsoleUI.printError("Registration failed.");
		}
		InputUtil.pressEnterToContinue();
	}

	// ────────────────────────────────────────────
	// 2. View All Users
	// ────────────────────────────────────────────
	private void viewAllUsers() {
		ConsoleUI.printHeader("ALL REGISTERED USERS");
		var users = authService.getAllUsers();
		if (users.isEmpty()) {
			ConsoleUI.printInfo("No users found.");
		} else {
			System.out.println();
			for (User u : users) {
				System.out.println("  " + u);
			}
		}
		InputUtil.pressEnterToContinue();
	}

	// ────────────────────────────────────────────
	// 3. Delete User
	// ────────────────────────────────────────────
	private void deleteUser() {
		ConsoleUI.printHeader("DELETE USER");
		String username = InputUtil.readString("  Username to delete: ").toLowerCase();
		if (username.equals(authService.getLoggedInUser().getUsername())) {
			ConsoleUI.printError("Cannot delete your own account.");
			InputUtil.pressEnterToContinue();
			return;
		}
		if (!authService.findUser(username).isPresent()) {
			ConsoleUI.printError("User '" + username + "' not found.");
			InputUtil.pressEnterToContinue();
			return;
		}
		if (InputUtil.readYesNo("  Confirm delete user '" + username + "'?")) {
			authService.deleteUser(username);
			ConsoleUI.printSuccess("User deleted.");
		} else {
			ConsoleUI.printInfo("Cancelled.");
		}
		InputUtil.pressEnterToContinue();
	}

	// ────────────────────────────────────────────
	// 4. View Tax Slabs
	// ────────────────────────────────────────────
	private void viewTaxSlabs() {
		ConsoleUI.printHeader("CURRENT TAX SLABS");
		List<TaxSlab> slabs = taxSlabManager.getAllSlabs();
		if (slabs.isEmpty()) {
			ConsoleUI.printInfo("No tax slabs defined.");
		} else {
			System.out.println();
			System.out.printf("  %-4s  %-25s  %s%n", "No.", "Income Range", "Tax Rate");
			ConsoleUI.printDivider();
			for (int i = 0; i < slabs.size(); i++) {
				System.out.printf("  %-4d  %s%n", i + 1, slabs.get(i));
			}
		}
		InputUtil.pressEnterToContinue();
	}

	// ────────────────────────────────────────────
	// 5. Add Tax Slab
	// ────────────────────────────────────────────
	private void addTaxSlab() {
		ConsoleUI.printHeader("ADD NEW TAX SLAB");
		double min = InputUtil.readPositiveDouble("  Min Income (₹): ");
		double max = InputUtil.readMaxIncome("  Max Income (₹): ");
		double rate = InputUtil.readTaxRatePercent("  Tax Rate (%): ");

		if (max != Double.MAX_VALUE && max <= min) {
			ConsoleUI.printError("Max income must be greater than min income.");
			InputUtil.pressEnterToContinue();
			return;
		}

		taxSlabManager.addSlab(new TaxSlab(min, max, rate));
		ConsoleUI.printSuccess("Tax slab added.");
		InputUtil.pressEnterToContinue();
	}

	// ────────────────────────────────────────────
	// 6. Edit Tax Slab
	// ────────────────────────────────────────────
	private void editTaxSlab() {
		ConsoleUI.printHeader("EDIT TAX SLAB");
		List<TaxSlab> slabs = new ArrayList<>(taxSlabManager.getAllSlabs());
		if (slabs.isEmpty()) {
			ConsoleUI.printInfo("No slabs to edit.");
			InputUtil.pressEnterToContinue();
			return;
		}

		for (int i = 0; i < slabs.size(); i++) {
			System.out.printf("  [%d] %s%n", i + 1, slabs.get(i));
		}
		System.out.println();
		int idx = InputUtil.readMenuChoice(1, slabs.size()) - 1;
		TaxSlab slab = slabs.get(idx);

		System.out.printf("  Editing: %s%n", slab);
		ConsoleUI.printInfo("Press ENTER to keep current value.");

		// Min
		System.out.printf("  Current Min (₹%.0f). New Min: ", slab.getMinIncome());
		// We can't use readPositiveDouble's loop here since we want skip on blank
		// So we inline a nullable read:
		double newMin = readOptionalDouble(slab.getMinIncome(), false);
		slab.setMinIncome(newMin);

		// Max
		System.out.printf("  Current Max (%s). New Max (0=unlimited): ",
				slab.getMaxIncome() == Double.MAX_VALUE ? "Unlimited" : String.format("₹%.0f", slab.getMaxIncome()));
		double newMax = readOptionalDouble(slab.getMaxIncome(), true);
		slab.setMaxIncome(newMax);

		// Rate
		System.out.printf("  Current Rate (%.1f%%). New Rate (%%): ", slab.getTaxRate() * 100);
		double newRate = readOptionalRate(slab.getTaxRate());
		slab.setTaxRate(newRate);

		taxSlabManager.replaceAllSlabs(slabs);
		ConsoleUI.printSuccess("Tax slab updated.");
		InputUtil.pressEnterToContinue();
	}

	private double readOptionalDouble(double current, boolean allowZeroAsMax) {
		java.util.Scanner sc = new java.util.Scanner(System.in);
		String raw = sc.nextLine().trim();
		if (raw.isEmpty())
			return current;
		try {
			double val = Double.parseDouble(raw);
			if (allowZeroAsMax && val == 0)
				return Double.MAX_VALUE;
			if (val < 0) {
				ConsoleUI.printError("Cannot be negative; keeping old value.");
				return current;
			}
			return val;
		} catch (NumberFormatException e) {
			ConsoleUI.printError("Invalid number '" + raw + "'; keeping old value.");
			return current;
		}
	}

	private double readOptionalRate(double current) {
		java.util.Scanner sc = new java.util.Scanner(System.in);
		String raw = sc.nextLine().trim();
		if (raw.isEmpty())
			return current;
		try {
			double pct = Double.parseDouble(raw);
			if (pct < 0 || pct > 100) {
				ConsoleUI.printError("Rate must be 0–100; keeping old value.");
				return current;
			}
			return pct / 100.0;
		} catch (NumberFormatException e) {
			ConsoleUI.printError("Invalid rate '" + raw + "'; keeping old value.");
			return current;
		}
	}

	// ────────────────────────────────────────────
	// 7. Delete Tax Slab
	// ────────────────────────────────────────────
	private void deleteTaxSlab() {
		ConsoleUI.printHeader("DELETE TAX SLAB");
		List<TaxSlab> slabs = new ArrayList<>(taxSlabManager.getAllSlabs());
		if (slabs.isEmpty()) {
			ConsoleUI.printInfo("No slabs to delete.");
			InputUtil.pressEnterToContinue();
			return;
		}

		for (int i = 0; i < slabs.size(); i++) {
			System.out.printf("  [%d] %s%n", i + 1, slabs.get(i));
		}
		System.out.println();
		int idx = InputUtil.readMenuChoice(1, slabs.size()) - 1;

		if (InputUtil.readYesNo("  Delete slab '" + slabs.get(idx) + "'?")) {
			slabs.remove(idx);
			taxSlabManager.replaceAllSlabs(slabs);
			ConsoleUI.printSuccess("Slab deleted.");
		} else {
			ConsoleUI.printInfo("Cancelled.");
		}
		InputUtil.pressEnterToContinue();
	}

	// ────────────────────────────────────────────
	// 8. Reset Defaults
	// ────────────────────────────────────────────
	private void resetDefaultSlabs() {
		if (InputUtil.readYesNo("  Reset ALL slabs to Indian Income Tax defaults?")) {
			taxSlabManager.clearAllSlabs();
			List<TaxSlab> defaults = new ArrayList<>();
			defaults.add(new TaxSlab(0, 250000, 0.00));
			defaults.add(new TaxSlab(250000, 500000, 0.05));
			defaults.add(new TaxSlab(500000, 1000000, 0.20));
			defaults.add(new TaxSlab(1000000, Double.MAX_VALUE, 0.30));
			taxSlabManager.replaceAllSlabs(defaults);
			ConsoleUI.printSuccess("Default slabs restored.");
		}
		InputUtil.pressEnterToContinue();
	}

	// ────────────────────────────────────────────
	// 9. Calculate Tax for Any User
	// ────────────────────────────────────────────
	private void calculateTaxForUser() {
		ConsoleUI.printHeader("CALCULATE TAX FOR USER");
		String username = InputUtil.readString("  Username: ").toLowerCase();
		Optional<User> opt = authService.findUser(username);
		if (opt.isEmpty()) {
			ConsoleUI.printError("User '" + username + "' not found.");
			InputUtil.pressEnterToContinue();
			return;
		}
		User user = opt.get();
		System.out.printf("  Found: %s%n", user);
		System.out.printf("  Stored income: ₹%.2f%n", user.getAnnualIncome());

		// Option to calculate on stored income or enter custom
		boolean custom = InputUtil.readYesNo("  Enter a different income for this calculation?");
		double income = custom ? InputUtil.readPositiveDouble("  Income (₹): ") : user.getAnnualIncome();

		printTaxBreakdown(income);
		InputUtil.pressEnterToContinue();
	}

	public static void printTaxBreakdown(double income) {
		TaxSlabManager mgr = TaxSlabManager.getInstance();
		TaxSlabManager.TaxResult result = mgr.calculateTax(income);

		System.out.println();
		ConsoleUI.printDivider();
		System.out.printf("  Gross Income      : ₹%,.2f%n", result.getGrossIncome());
		ConsoleUI.printDivider();
		System.out.println("  Tax Breakdown (Slab-wise):");
		System.out.println();

		for (TaxSlabManager.SlabBreakdown bd : result.getBreakdowns()) {
			System.out.printf("  %-30s  Taxable: ₹%,10.2f   Tax: ₹%,10.2f%n", bd.slab.toString(), bd.taxableAmount,
					bd.tax);
		}

		ConsoleUI.printDivider();
		System.out.printf("  Total Tax         : ₹%,.2f%n", result.getTotalTax());
		System.out.printf("  Net Income        : ₹%,.2f%n", result.getNetIncome());
		System.out.printf("  Effective Rate    : %.2f%%%n", result.getEffectiveRate());
		ConsoleUI.printDivider();
	}
}
