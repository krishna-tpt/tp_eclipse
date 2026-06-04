package com.taxcalc;

import com.taxcalc.model.User;
import com.taxcalc.repository.UserRepository;
import com.taxcalc.service.AuthService;
import com.taxcalc.service.TaxSlabManager;
import com.taxcalc.ui.AdminDashboard;
import com.taxcalc.ui.ConsoleUI;
import com.taxcalc.ui.TaxPayerDashboard;
import com.taxcalc.util.InputUtil;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║           TAX CALCULATOR — L3 MACHINE CODING           ║
 * ║                                                              ║
 * ║  Key Design Decisions:                                       ║
 * ║  • Singleton pattern for TaxSlabManager                      ║
 * ║  • File-based non-volatile persistence (Java Serialization)  ║
 * ║  • CORRECT progressive/marginal tax calculation              ║
 * ║  • Bulletproof input validation (handles all bad inputs)     ║
 * ║  • Password hashed via hashCode of username+password combo   ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
public class TaxCalculatorApp {

	private final AuthService authService;
	private final TaxSlabManager taxSlabManager;
	private final AdminDashboard adminDashboard;
	private final TaxPayerDashboard taxpayerDashboard;

	public TaxCalculatorApp() {
		UserRepository userRepo = new UserRepository();
		this.authService = new AuthService(userRepo);
		this.taxSlabManager = TaxSlabManager.getInstance(); // Singleton
		this.adminDashboard = new AdminDashboard(authService, taxSlabManager);
		this.taxpayerDashboard = new TaxPayerDashboard(authService, taxSlabManager);
	}

	public void run() {
		ConsoleUI.printBanner();

		boolean running = true;
		while (running) {
			ConsoleUI.printHeader("MAIN MENU");
			String[] options = { "Login as Admin", "Login as Tax Payer", "Register as New Tax Payer", "Exit" };
			ConsoleUI.printMenu(options);
			int choice = InputUtil.readMenuChoice(1, options.length);

			switch (choice) {
			case 1 -> loginAndRoute(User.Role.ADMIN);
			case 2 -> loginAndRoute(User.Role.TAXPAYER);
			case 3 -> selfRegisterTaxPayer();
			case 4 -> {
				ConsoleUI.printInfo("Goodbye!");
				running = false;
			}
			}
		}
	}

	private void loginAndRoute(User.Role expectedRole) {
		ConsoleUI.printHeader("LOGIN — " + expectedRole);

		int attempts = 0;
		final int MAX_ATTEMPTS = 3;

		while (attempts < MAX_ATTEMPTS) {
			String username = InputUtil.readString("  Username: ");
			String password = InputUtil.readPassword("  Password: ");

			if (authService.login(username, password)) {
				User user = authService.getLoggedInUser();

				// Role check
				if (user.getRole() != expectedRole) {
					ConsoleUI.printError("This account is not a " + expectedRole + " account.");
					ConsoleUI.printInfo("Please use the correct login option from the main menu.");
					authService.logout();
					InputUtil.pressEnterToContinue();
					return;
				}

				ConsoleUI.printSuccess("Welcome, " + user.getUsername() + "!");

				if (user.getRole() == User.Role.ADMIN) {
					adminDashboard.show();
				} else {
					taxpayerDashboard.show();
				}
				authService.logout();
				return;

			} else {
				attempts++;
				int remaining = MAX_ATTEMPTS - attempts;
				ConsoleUI.printError("Invalid credentials. "
						+ (remaining > 0 ? remaining + " attempt(s) remaining." : "Account locked for this session."));
			}
		}
		InputUtil.pressEnterToContinue();
	}

	private void selfRegisterTaxPayer() {
		ConsoleUI.printHeader("REGISTER AS NEW TAX PAYER");

		String username = InputUtil.readString("  Choose Username: ").toLowerCase();

		if (authService.findUser(username).isPresent()) {
			ConsoleUI.printError("Username '" + username + "' is already taken. Try another.");
			InputUtil.pressEnterToContinue();
			return;
		}

		if (username.length() < 3) {
			ConsoleUI.printError("Username must be at least 3 characters.");
			InputUtil.pressEnterToContinue();
			return;
		}

		String password = InputUtil.readPassword("  Choose Password (min 4 chars): ");
		if (password.length() < 4) {
			ConsoleUI.printError("Password must be at least 4 characters.");
			InputUtil.pressEnterToContinue();
			return;
		}

		String confirm = InputUtil.readPassword("  Confirm Password: ");
		if (!password.equals(confirm)) {
			ConsoleUI.printError("Passwords do not match.");
			InputUtil.pressEnterToContinue();
			return;
		}

		double income = InputUtil.readPositiveDouble("  Annual Income (₹): ");

		boolean ok = authService.registerUser(username, password, User.Role.TAXPAYER, income);
		if (ok) {
			ConsoleUI.printSuccess("Registration successful! You can now login.");
		} else {
			ConsoleUI.printError("Registration failed. Username may already be taken.");
		}
		InputUtil.pressEnterToContinue();
	}

	// ────────────────────────────────────────────
	public static void main(String[] args) {
		try {
			new TaxCalculatorApp().run();
		} catch (Exception e) {
			System.err.println("[FATAL] Unexpected error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
