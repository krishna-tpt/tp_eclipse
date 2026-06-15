package com.elevatorSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// ====================== CONTROLLER ======================
public class ElevatorController {
	private static ElevatorController instance;
	private final List<Elevator> elevators = new ArrayList<>();
	private final Queue<Request> requestQueue = new ConcurrentLinkedQueue<>();
	private final ElevatorSelectionStrategy strategy = new NearestElevatorStrategy();
	private final ExecutorService executor = Executors.newFixedThreadPool(5);

	private ElevatorController(int numElevators) {
		for (int i = 1; i <= numElevators; i++) {
			elevators.add(new Elevator(i));
		}
	}

	public static synchronized ElevatorController getInstance(int numElevators) {
		if (instance == null) {
			instance = new ElevatorController(numElevators);
		}
		return instance;
	}

	public void addExternalRequest(int floor, Direction direction) {
		Request req = new Request(floor, direction, false);
		requestQueue.offer(req);
		System.out.println("External Request: Floor " + floor + " " + direction);
		processRequests();
	}

	public void addInternalRequest(int elevatorId, int destinationFloor) {
		Elevator elevator = elevators.stream().filter(e -> e.getId() == elevatorId).findFirst().orElse(null);

		if (elevator != null) {
			elevator.addTargetFloor(destinationFloor);
			System.out.println("Internal Request: Elevator " + elevatorId + " -> Floor " + destinationFloor);
			executor.submit(elevator::move);
		}
	}

	private void processRequests() {
		while (!requestQueue.isEmpty()) {
			Request req = requestQueue.poll();
			Elevator selected = strategy.selectElevator(elevators, req);

			if (selected != null) {
				selected.addTargetFloor(req.getFloor());
				if (selected.getStatus() == ElevatorStatus.STOPPED) {
					executor.submit(selected::move);
				}
				System.out.println("Assigned Elevator " + selected.getId() + " for request");
			} else {
				System.out.println("No available elevator for request at floor " + req.getFloor());
				requestQueue.offer(req); // Re-queue
			}
		}
	}

	public void printStatus() {
		System.out.println("\n=== Elevator Status ===");
		for (Elevator e : elevators) {
			System.out.printf("Elevator %d | Floor: %d | Dir: %s | Status: %s%n", e.getId(), e.getCurrentFloor(),
					e.getDirection(), e.getStatus());
		}
	}
}