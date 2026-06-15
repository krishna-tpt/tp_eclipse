package com.elevator;

import com.elevator.controller.ElevatorController;
import com.elevator.model.Building;
import com.elevator.model.Elevator;
import com.elevator.strategy.NearestCarStrategy;
import com.elevator.strategy.RoundRobinStrategy;

import java.util.List;
import java.util.Scanner;

/**
 * Interactive Elevator LLD Simulation ------------------------------------
 * Scanner-based menu. User controls every action manually. Real-time elevator
 * status printed after each command.
 *
 * How to run: javac -d out -sourcepath src
 * src/com/elevator/ElevatorSimulation.java java -cp out
 * com.elevator.ElevatorSimulation
 */
public class ElevatorSimulation {

	// ── Config (change these to test different building sizes) ─────────────
	private static final int TOTAL_FLOORS = 10;
	private static final int TOTAL_ELEVATORS = 3;

	// ── State ──────────────────────────────────────────────────────────────
	private static Building building;
	private static Scanner scanner;

	// ─────────────────────────────────────────────────────────────────────
	public static void main(String[] args) throws InterruptedException {

		scanner = new Scanner(System.in);

		printBanner();
		printScenarioGuide();

		pressEnterToContinue("Press ENTER to start the building...");

		// Boot the building
		building = new Building("TechPark Tower", TOTAL_FLOORS, TOTAL_ELEVATORS);
		Thread.sleep(400);

		printStatus();

		// Main menu loop
		boolean running = true;
		while (running) {
			printMenu();
			String choice = scanner.nextLine().trim();

			switch (choice) {
			case "1" -> handleFloorUp();
			case "2" -> handleFloorDown();
			case "3" -> handleCabinSelect();
			case "4" -> handleStatus();
			case "5" -> handleMaintenance();
			case "6" -> handleOverload();
			case "7" -> handleStrategySwitch();
			case "8" -> handleMultiRequest();
			case "9" -> printScenarioGuide();
			case "0" -> running = false;
			default -> print("  ❌  Invalid choice. Try again.");
			}

			if (running) {
				Thread.sleep(300);
			}
		}

		print("\n  Shutting down building...");
		building.shutdown();
		ElevatorController.reset();
		print("  ✅  Goodbye!\n");
		scanner.close();
	}

	// ══════════════════════════════════════════════════════════════════════
	// MENU HANDLERS
	// ══════════════════════════════════════════════════════════════════════

	/** Option 1 — Press UP on a floor */
	private static void handleFloorUp() {
		int floor = readInt("  Enter floor number to press UP (1–" + TOTAL_FLOORS + "): ", 1, TOTAL_FLOORS);
		print("");
		print("  >> Floor " + floor + " UP button pressed");
		building.callElevatorUp(floor);
		sleep(500);
		printStatus();
		print("  ℹ️   An elevator is now assigned and heading to floor " + floor);
	}

	/** Option 2 — Press DOWN on a floor */
	private static void handleFloorDown() {
		int floor = readInt("  Enter floor number to press DOWN (1–" + TOTAL_FLOORS + "): ", 1, TOTAL_FLOORS);
		print("");
		print("  >> Floor " + floor + " DOWN button pressed");
		building.callElevatorDown(floor);
		sleep(500);
		printStatus();
		print("  ℹ️   An elevator is now assigned and heading to floor " + floor);
	}

	/** Option 3 — Press destination inside cabin */
	private static void handleCabinSelect() {
		printStatus();
		int elevId = readInt("  Enter Elevator ID (1–" + TOTAL_ELEVATORS + "): ", 1, TOTAL_ELEVATORS);
		int dest = readInt("  Enter destination floor (1–" + TOTAL_FLOORS + "): ", 1, TOTAL_FLOORS);
		print("");
		print("  >> Inside Elevator " + elevId + ": pressed floor " + dest);
		building.selectFloorInCabin(elevId, dest);
		sleep(500);
		printStatus();
		print("  ℹ️   Elevator " + elevId + " added floor " + dest + " to its queue");
	}

	/** Option 4 — Print status */
	private static void handleStatus() {
		printStatus();
		printElevatorDetails();
	}

	/** Option 5 — Toggle maintenance mode */
	private static void handleMaintenance() {
		printStatus();
		int elevId = readInt("  Enter Elevator ID to toggle maintenance (1–" + TOTAL_ELEVATORS + "): ", 1,
				TOTAL_ELEVATORS);

		print("  Toggle: (1) Enable Maintenance   (2) Disable Maintenance");
		print("  Choice: ");
		String sub = scanner.nextLine().trim();

		boolean enable = sub.equals("1");
		building.setMaintenance(elevId, enable);
		sleep(300);
		printStatus();

		if (enable) {
			print("  ℹ️   Elevator " + elevId + " is in MAINTENANCE — dispatcher will skip it");
			print("  ℹ️   Try option 1 or 2 now — watch which elevator gets assigned");
		} else {
			print("  ℹ️   Elevator " + elevId + " is back ONLINE");
		}
	}

	/** Option 6 — Simulate overload */
	private static void handleOverload() {
		printStatus();
		int elevId = readInt("  Enter Elevator ID to update load (1–" + TOTAL_ELEVATORS + "): ", 1, TOTAL_ELEVATORS);

		print("  Current max capacity: 10 persons");
		print("  Enter load delta (+N to add, -N to remove persons): ");
		String input = scanner.nextLine().trim();

		try {
			int delta = Integer.parseInt(input);
			building.updateLoad(elevId, delta);
			sleep(300);
			printStatus();

			Elevator e = getElevator(elevId);
			if (e != null) {
				print("  ℹ️   Elevator " + elevId + " load = " + e.getCurrentLoad() + "/" + e.getMaxCapacity());
				if (e.getCurrentLoad() >= e.getMaxCapacity()) {
					print("  ⚠️   OVERLOADED — try assigning a request now, it will be skipped");
				}
			}
		} catch (NumberFormatException ex) {
			print("  ❌  Invalid number. Enter like +5 or -3.");
		}
	}

	/** Option 7 — Switch dispatch strategy */
	private static void handleStrategySwitch() {
		print("");
		print("  Current strategy options:");
		print("  (1) Nearest Car   — assigns closest elevator in right direction");
		print("  (2) Round Robin   — distributes load evenly (good for peak hours)");
		print("  Choice: ");
		String sub = scanner.nextLine().trim();

		switch (sub) {
		case "1" -> {
			building.setDispatchStrategy(new NearestCarStrategy());
			print("  ✅  Switched to Nearest Car strategy");
		}
		case "2" -> {
			building.setDispatchStrategy(new RoundRobinStrategy());
			print("  ✅  Switched to Round Robin strategy");
			print("  ℹ️   Now press UP/DOWN from multiple floors and watch the distribution");
		}
		default -> print("  ❌  Invalid choice");
		}
	}

	/** Option 8 — Fire multiple requests at once (stress test) */
	private static void handleMultiRequest() {
		print("");
		print("  Multi-Request Stress Test");
		print("  How many floor requests to fire simultaneously? (2–6): ");
		int count = readInt("", 2, 6);

		print("  Firing " + count + " simultaneous UP requests...\n");

		int[] floors = pickSpreadFloors(count);
		Thread[] threads = new Thread[count];

		for (int i = 0; i < count; i++) {
			final int floor = floors[i];
			threads[i] = new Thread(() -> {
				print("  [Thread] Floor " + floor + " UP pressed");
				building.callElevatorUp(floor);
			});
		}

		for (Thread t : threads)
			t.start();
		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		sleep(600);
		printStatus();
		print("  ℹ️   Observe how " + TOTAL_ELEVATORS + " elevators share " + count + " requests");
		print("  ℹ️   Switch to Round Robin (option 7) and repeat to compare!");
	}

	// ══════════════════════════════════════════════════════════════════════
	// DISPLAY HELPERS
	// ══════════════════════════════════════════════════════════════════════

	private static void printMenu() {
		print("\n╔══════════════════════════════════════════════╗");
		print("║              ELEVATOR CONTROL PANEL         ║");
		print("╠══════════════════════════════════════════════╣");
		print("║  1  →  Press UP button on a floor           ║");
		print("║  2  →  Press DOWN button on a floor         ║");
		print("║  3  →  Press destination inside cabin       ║");
		print("║  4  →  Show elevator status                 ║");
		print("║  5  →  Toggle maintenance mode              ║");
		print("║  6  →  Simulate overload                    ║");
		print("║  7  →  Switch dispatch strategy             ║");
		print("║  8  →  Multi-request stress test            ║");
		print("║  9  →  Show test scenario guide             ║");
		print("║  0  →  Exit                                 ║");
		print("╚══════════════════════════════════════════════╝");
		System.out.print("  Your choice: ");
	}

	private static void printStatus() {
		List<Elevator> elevators = building.getController().getElevators();
		print("\n  ┌─────────────────────────────────────────────┐");
		print("  │               LIVE STATUS                  │");
		print("  ├────────┬───────┬───────────┬──────┬───────┤");
		print("  │ Elev   │ Floor │ State     │ Dir  │ Queue │");
		print("  ├────────┼───────┼───────────┼──────┼───────┤");
		for (Elevator e : elevators) {
			print(String.format("  │ E%-5d │  %-4d │ %-9s │ %-4s │  %-4d │", e.getId(), e.getCurrentFloor(),
					e.getState(), e.getDirection(), e.getQueueSize()));
		}
		print("  └────────┴───────┴───────────┴──────┴───────┘");
	}

	private static void printElevatorDetails() {
		List<Elevator> elevators = building.getController().getElevators();
		print("\n  Detailed view:");
		for (Elevator e : elevators) {
			print("  Elevator " + e.getId() + ":" + "  Floor=" + e.getCurrentFloor() + "  Load=" + e.getCurrentLoad()
					+ "/" + e.getMaxCapacity() + "  Door=" + e.getDoor().getStatus() + "  Available="
					+ e.isAvailable());
		}
	}

	private static void printBanner() {
		print("\n");
		print("  ╔═══════════════════════════════════════════════════╗");
		print("  ║                                                   ║");
		print("  ║       ELEVATOR SYSTEM  —  LLD INTERVIEW           ║");
		print("  ║       Interactive Simulation  (Java)              ║");
		print("  ║                                                   ║");
		print("  ║   Building  : TechPark Tower                      ║");
		print("  ║   Floors    : " + TOTAL_FLOORS + "                                    ║");
		print("  ║   Elevators : " + TOTAL_ELEVATORS + "                                    ║");
		print("  ║                                                   ║");
		print("  ╚═══════════════════════════════════════════════════╝");
		print("\n");
	}

	private static void printScenarioGuide() {
		print("\n");
		print("  ╔═══════════════════════════════════════════════════════════╗");
		print("  ║              TEST SCENARIO GUIDE                         ║");
		print("  ╠═══════════════════════════════════════════════════════════╣");
		print("  ║                                                           ║");
		print("  ║  SCENARIO 1 — Basic External Request                      ║");
		print("  ║    Step 1: Choose option 1 (UP button)                    ║");
		print("  ║    Step 2: Enter floor 5                                  ║");
		print("  ║    Watch : Nearest elevator assigned and moves to floor 5 ║");
		print("  ║                                                           ║");
		print("  ║  SCENARIO 2 — Cabin Destination                           ║");
		print("  ║    Step 1: Option 1 → floor 3 (call elevator to 3)        ║");
		print("  ║    Step 2: Option 3 → Elevator 1 → floor 8 (inside press) ║");
		print("  ║    Watch : Elevator goes to 3 first, then 8               ║");
		print("  ║                                                           ║");
		print("  ║  SCENARIO 3 — Maintenance Mode                            ║");
		print("  ║    Step 1: Option 5 → Elevator 2 → Enable maintenance     ║");
		print("  ║    Step 2: Option 1 → any floor (call elevator)           ║");
		print("  ║    Watch : Elevator 2 never gets assigned                 ║");
		print("  ║    Step 3: Option 5 → Elevator 2 → Disable maintenance    ║");
		print("  ║                                                           ║");
		print("  ║  SCENARIO 4 — Overload Detection                          ║");
		print("  ║    Step 1: Option 6 → Elevator 3 → load delta +10         ║");
		print("  ║    Step 2: Option 1 → any floor                           ║");
		print("  ║    Watch : Elevator 3 is OVERLOADED, skipped              ║");
		print("  ║    Step 3: Option 6 → Elevator 3 → load delta -5          ║");
		print("  ║                                                           ║");
		print("  ║  SCENARIO 5 — Strategy Switch (Peak Hours)                ║");
		print("  ║    Step 1: Option 7 → Switch to Round Robin               ║");
		print("  ║    Step 2: Option 1 several times (floors 1,3,5,7,9)      ║");
		print("  ║    Watch : Requests distributed evenly across elevators   ║");
		print("  ║    Step 3: Option 7 → Switch back to Nearest Car          ║");
		print("  ║    Step 4: Same requests → watch different assignment      ║");
		print("  ║                                                           ║");
		print("  ║  SCENARIO 6 — Concurrent Stress Test                      ║");
		print("  ║    Step 1: Option 8 → fire 5 simultaneous requests        ║");
		print("  ║    Watch : 3 elevators share 5 requests in parallel       ║");
		print("  ║    Note  : Thread safety — no duplicate assignment        ║");
		print("  ║                                                           ║");
		print("  ║  SCENARIO 7 — Full Flow (Interview Demo)                  ║");
		print("  ║    Step 1: Option 1 → floor 1 (lobby, going UP)           ║");
		print("  ║    Step 2: Option 3 → that elevator → floor 10            ║");
		print("  ║    Step 3: Option 2 → floor 10 (going DOWN)               ║");
		print("  ║    Step 4: Option 3 → that elevator → floor 1             ║");
		print("  ║    Watch : Full round trip, door open/close at each stop  ║");
		print("  ║                                                           ║");
		print("  ╚═══════════════════════════════════════════════════════════╝");
		print("\n");
	}

	// ══════════════════════════════════════════════════════════════════════
	// UTILITY HELPERS
	// ══════════════════════════════════════════════════════════════════════

	private static int readInt(String prompt, int min, int max) {
		while (true) {
			if (!prompt.isEmpty())
				System.out.print(prompt);
			String line = scanner.nextLine().trim();
			try {
				int val = Integer.parseInt(line);
				if (val >= min && val <= max)
					return val;
				print("  ❌  Enter a number between " + min + " and " + max);
			} catch (NumberFormatException e) {
				print("  ❌  That's not a number. Try again.");
			}
		}
	}

	private static void pressEnterToContinue(String msg) {
		System.out.print("\n  " + msg);
		scanner.nextLine();
	}

	private static int[] pickSpreadFloors(int count) {
		// Spread floors across the building for interesting distribution
		int[] floors = new int[count];
		int step = TOTAL_FLOORS / (count + 1);
		for (int i = 0; i < count; i++) {
			floors[i] = Math.min((i + 1) * step, TOTAL_FLOORS);
		}
		return floors;
	}

	private static Elevator getElevator(int id) {
		return building.getController().getElevators().stream().filter(e -> e.getId() == id).findFirst().orElse(null);
	}

	private static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private static void print(String msg) {
		System.out.println(msg);
	}
}