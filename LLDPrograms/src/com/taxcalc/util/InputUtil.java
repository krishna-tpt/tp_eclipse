package com.taxcalc.util;

import java.util.Scanner;

/**
 * Centralised safe input reader.
 * Handles ALL invalid input cases the Zoho invigilators throw:
 *   - letters/symbols where number is expected
 *   - negative income
 *   - empty strings
 *   - whitespace-only strings
 *   - out-of-range menu choices
 *   - tax rate > 100% or negative
 */
public class InputUtil {

	private static final Scanner scanner = new Scanner(System.in);

	private InputUtil() {
	}

	/** Read a non-empty, non-blank string */
	public static String readString(String prompt) {
		while (true) {
			System.out.print(prompt);
			String input = scanner.nextLine().trim();
			if (!input.isEmpty())
				return input;
			System.out.println("  [!] Input cannot be empty. Try again.");
		}
	}

	/** Read a password (no echo possible in plain console, just reads quietly) */
	public static String readPassword(String prompt) {
		// Console.readPassword() works in real terminals; falls back gracefully in IDE
		java.io.Console console = System.console();
		if (console != null) {
			char[] pwd = console.readPassword(prompt);
			if (pwd != null && pwd.length > 0)
				return new String(pwd);
		}
		return readString(prompt);
	}

	/** Read a positive double (income amounts) */
	public static double readPositiveDouble(String prompt) {
		while (true) {
			System.out.print(prompt);
			String raw = scanner.nextLine().trim();
			if (raw.isEmpty()) {
				System.out.println("  [!] Please enter a numeric value.");
				continue;
			}
			try {
				double val = Double.parseDouble(raw);
				if (val < 0) {
					System.out.println("  [!] Value cannot be negative.");
					continue;
				}
				return val;
			} catch (NumberFormatException e) {
				System.out.printf("  [!] '%s' is not a valid number. Enter digits only (e.g. 750000).%n", raw);
			}
		}
	}

	/**
	 * Read a double that may be Double.MAX_VALUE sentinel (for "no upper limit").
	 * Enter 0 to mean "no upper limit / infinity".
	 */
	public static double readMaxIncome(String prompt) {
		System.out.println("  (Enter 0 to indicate 'no upper limit / topmost slab')");
		double val = readPositiveDouble(prompt);
		return (val == 0) ? Double.MAX_VALUE : val;
	}

	/** Read a tax rate as a percentage 0–100, returns as fraction 0–1 */
	public static double readTaxRatePercent(String prompt) {
		while (true) {
			System.out.print(prompt);
			String raw = scanner.nextLine().trim();
			try {
				double pct = Double.parseDouble(raw);
				if (pct < 0 || pct > 100) {
					System.out.println("  [!] Tax rate must be between 0 and 100 (percent).");
					continue;
				}
				return pct / 100.0;
			} catch (NumberFormatException e) {
				System.out.printf("  [!] '%s' is not a valid percentage. Enter a number like 5 or 20.%n", raw);
			}
		}
	}

	/** Read an integer menu choice within [min, max] inclusive */
	public static int readMenuChoice(int min, int max) {
		while (true) {
			System.out.print("  Enter choice [" + min + "-" + max + "]: ");
			String raw = scanner.nextLine().trim();
			try {
				int choice = Integer.parseInt(raw);
				if (choice < min || choice > max) {
					System.out.printf("  [!] Please enter a number between %d and %d.%n", min, max);
					continue;
				}
				return choice;
			} catch (NumberFormatException e) {
				System.out.printf("  [!] '%s' is not a valid option. Enter a number.%n", raw);
			}
		}
	}

	/** Pause and wait for Enter */
	public static void pressEnterToContinue() {
		System.out.print("\n  Press ENTER to continue...");
		scanner.nextLine();
	}

	/** Yes/No confirmation */
	public static boolean readYesNo(String prompt) {
		while (true) {
			System.out.print(prompt + " (y/n): ");
			String input = scanner.nextLine().trim().toLowerCase();
			if (input.equals("y") || input.equals("yes"))
				return true;
			if (input.equals("n") || input.equals("no"))
				return false;
			System.out.println("  [!] Enter 'y' or 'n'.");
		}
	}
}
