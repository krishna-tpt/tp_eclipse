package com.elevator.model;

import com.elevator.controller.ElevatorController;
import com.elevator.strategy.DispatchStrategy;

/**
 * Building — top-level Facade.
 *
 * Provides a clean API hiding the controller/dispatcher complexity.
 * External clients (REST API, simulation) interact only with Building.
 */
public class Building {

	private final String name;
	private final int totalFloors;
	private final int totalElevators;
	private final ElevatorController controller;

	public Building(String name, int totalFloors, int totalElevators) {
		this.name = name;
		this.totalFloors = totalFloors;
		this.totalElevators = totalElevators;
		this.controller = ElevatorController.getInstance(totalFloors, totalElevators);
		System.out.println(
				"[Building] '" + name + "' ready: " + totalFloors + " floors, " + totalElevators + " elevators.");
	}

	// ── Public API ────────────────────────────────────────────────────────────

	/** Floor panel UP button */
	public void callElevatorUp(int floor) {
		validateFloor(floor);
		controller.pressFloorUp(floor);
	}

	/** Floor panel DOWN button */
	public void callElevatorDown(int floor) {
		validateFloor(floor);
		controller.pressFloorDown(floor);
	}

	/** Cabin destination button */
	public void selectFloorInCabin(int elevatorId, int destinationFloor) {
		validateFloor(destinationFloor);
		controller.onInternalRequest(elevatorId, destinationFloor);
	}

	/** Switch dispatch strategy at runtime (e.g., peak-hour mode) */
	public void setDispatchStrategy(DispatchStrategy strategy) {
		controller.setDispatchStrategy(strategy);
	}

	/** Put elevator into maintenance mode */
	public void setMaintenance(int elevatorId, boolean enabled) {
		controller.setMaintenanceMode(elevatorId, enabled);
	}

	/** Update elevator load (kg or person count) */
	public void updateLoad(int elevatorId, int delta) {
		controller.updateLoad(elevatorId, delta);
	}

	public void printStatus() {
		controller.printStatus();
	}

	public void shutdown() throws InterruptedException {
		controller.shutdown();
	}

	// ── Validation ────────────────────────────────────────────────────────────
	private void validateFloor(int floor) {
		if (floor < 1 || floor > totalFloors) {
			throw new IllegalArgumentException("Invalid floor: " + floor + " (building has 1–" + totalFloors + ")");
		}
	}

	public String getName() {
		return name;
	}

	public int getTotalFloors() {
		return totalFloors;
	}

	public int getTotalElevators() {
		return totalElevators;
	}

	public ElevatorController getController() {
		return controller;
	}
}
