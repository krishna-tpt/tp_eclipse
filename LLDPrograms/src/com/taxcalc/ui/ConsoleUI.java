package com.taxcalc.ui;

public class ConsoleUI {

	public static final String RESET = "\u001B[0m";
	public static final String BOLD = "\u001B[1m";
	public static final String GREEN = "\u001B[32m";
	public static final String CYAN = "\u001B[36m";
	public static final String YELLOW = "\u001B[33m";
	public static final String RED = "\u001B[31m";
	public static final String BLUE = "\u001B[34m";
	public static final String WHITE = "\u001B[37m";

	private ConsoleUI() {
	}

	public static void clearScreen() {
		System.out.print("\033[H\033[2J");
		System.out.flush();
	}

	public static void printBanner() {
		System.out.println(CYAN + BOLD);
		System.out.println("  ╔══════════════════════════════════════════════════╗");
		System.out.println("  ║          INDIA TAX CALCULATOR SYSTEM            ║");
		System.out.println("  ║          Progressive Marginal Tax Engine        ║");
		System.out.println("  ╚══════════════════════════════════════════════════╝");
		System.out.println(RESET);
	}

	public static void printHeader(String title) {
		System.out.println();
		System.out.println(BOLD + BLUE + "  ┌─────────────────────────────────────────────────┐");
		System.out.printf("  │  %-46s  │%n", title);
		System.out.println("  └─────────────────────────────────────────────────┘" + RESET);
	}

	public static void printSuccess(String msg) {
		System.out.println(GREEN + "  [✔] " + msg + RESET);
	}

	public static void printError(String msg) {
		System.out.println(RED + "  [✘] " + msg + RESET);
	}

	public static void printInfo(String msg) {
		System.out.println(YELLOW + "  [i] " + msg + RESET);
	}

	public static void printDivider() {
		System.out.println("  ─────────────────────────────────────────────────");
	}

	public static void printMenu(String[] options) {
		for (int i = 0; i < options.length; i++) {
			System.out.printf("    %s[%d]%s %s%n", CYAN, i + 1, RESET, options[i]);
		}
		System.out.println();
	}

	public static void printMenuWithZero(String[] options, String zeroLabel) {
		printMenu(options);
		System.out.printf("    %s[0]%s %s%n%n", CYAN, RESET, zeroLabel);
	}
}
