package com.elevator.controller;

import com.elevator.enums.ElevatorState;
import com.elevator.model.Elevator;
import com.elevator.model.Floor;
import com.elevator.observer.ElevatorEventListener;
import com.elevator.observer.FloorRequestListener;
import com.elevator.request.ExternalRequest;
import com.elevator.request.InternalRequest;
import com.elevator.strategy.DispatchStrategy;
import com.elevator.strategy.NearestCarStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ElevatorController — Singleton.
 *
 * Responsibilities: - Register floors and elevators - Accept external (floor)
 * and internal (cabin) requests - Delegate dispatch to pluggable
 * DispatchStrategy - Manage elevator threads - Periodically run anti-starvation
 * aging
 *
 * Thread safety: - elevators list read-only after init, so no lock needed there
 * - handleExternalRequest synchronized to prevent duplicate dispatch -
 * ExecutorService manages elevator threads
 */
public class ElevatorController implements FloorRequestListener {

	// ── Singleton ─────────────────────────────────────────────────────────────
	private static volatile ElevatorController instance;

	public static ElevatorController getInstance(int numFloors, int numElevators) {
		if (instance == null) {
			synchronized (ElevatorController.class) {
				if (instance == null) {
					instance = new ElevatorController(numFloors, numElevators);
				}
			}
		}
		return instance;
	}

	/** Reset singleton — for testing only */
	public static void reset() {
		instance = null;
	}

	// ── Fields ────────────────────────────────────────────────────────────────
	private final List<Elevator> elevators;
	private final List<Floor> floors;
	private final ExecutorService elevatorThreadPool;
	private final ScheduledExecutorService agingScheduler;
	private DispatchStrategy dispatchStrategy;

	private ElevatorController(int numFloors, int numElevators) {
		this.elevators = new ArrayList<>();
		this.floors = new ArrayList<>();
		this.dispatchStrategy = new NearestCarStrategy(); // default strategy
		this.elevatorThreadPool = Executors.newFixedThreadPool(numElevators);
		this.agingScheduler = Executors.newSingleThreadScheduledExecutor();

		initFloors(numFloors);
		initElevators(numElevators);
		startAgingScheduler();

		System.out.println("[Controller] Initialized: " + numFloors + " floors, " + numElevators + " elevators.");
	}

	// ── Initialization ────────────────────────────────────────────────────────
	private void initFloors(int count) {
		for (int i = 1; i <= count; i++) {
			Floor floor = new Floor(i);
			floor.addListener(this); // Observer: controller listens to floor buttons
			floors.add(floor);
		}
	}

	private void initElevators(int count) {
		for (int i = 1; i <= count; i++) {
			Elevator elevator = new Elevator(i, 1, 10);
			elevator.addListener(buildEventListener()); // Observer: log events
			elevators.add(elevator);
			elevatorThreadPool.submit(elevator); // each elevator runs its own thread
		}
	}

	/** Aging: every 5 seconds, boost priority of long-waiting requests */
	private void startAgingScheduler() {
		agingScheduler.scheduleAtFixedRate(() -> {
			elevators.forEach(e -> {
				// RequestQueue.ageRequests() is already called inside Elevator.run()
				// This external hook can be used for cross-elevator balancing
			});
		}, 5, 5, TimeUnit.SECONDS);
	}

	// ── Strategy: hot-swappable at runtime ───────────────────────────────────
	public synchronized void setDispatchStrategy(DispatchStrategy strategy) {
		this.dispatchStrategy = strategy;
		System.out.println("[Controller] Dispatch strategy changed to: " + strategy.getClass().getSimpleName());
	}

	// ── External request (from floor button) ─────────────────────────────────
	@Override
	public synchronized void onExternalRequest(ExternalRequest request) {
		System.out.println("[Controller] External: " + request);
		Elevator chosen = dispatchStrategy.dispatch(request, elevators);
		if (chosen == null) {
			System.out.println("[Controller] ❌ No elevator available for: " + request);
			return;
		}
		System.out.println("[Controller] → Assigned to E" + chosen.getId());
		chosen.assignRequest(request);
	}

	// ── Internal request (from cabin button) ─────────────────────────────────
	public void onInternalRequest(int elevatorId, int destinationFloor) {
		Elevator elevator = findById(elevatorId);
		if (elevator == null) {
			System.out.println("[Controller] Unknown elevator id: " + elevatorId);
			return;
		}
		System.out.println("[Controller] Cabin: E" + elevatorId + " → floor " + destinationFloor);
		elevator.pressFloorButton(destinationFloor);
	}

	// ── Maintenance / Overload API ────────────────────────────────────────────
	public void setMaintenanceMode(int elevatorId, boolean enabled) {
		Elevator e = findById(elevatorId);
		if (e != null)
			e.setMaintenanceMode(enabled);
	}

	public void updateLoad(int elevatorId, int delta) {
		Elevator e = findById(elevatorId);
		if (e != null)
			e.updateLoad(delta);
	}

	// ── Floor button API (used by simulation / tests) ─────────────────────────
	public void pressFloorUp(int floorNumber) {
		Floor floor = findFloor(floorNumber);
		if (floor != null)
			floor.pressUp();
	}

	public void pressFloorDown(int floorNumber) {
		Floor floor = findFloor(floorNumber);
		if (floor != null)
			floor.pressDown();
	}

	// ── Status ────────────────────────────────────────────────────────────────
	public void printStatus() {
		System.out.println("\n=== Elevator System Status ===");
		elevators.forEach(System.out::println);
		System.out.println("==============================\n");
	}

	// ── Shutdown ──────────────────────────────────────────────────────────────
	public void shutdown() throws InterruptedException {
		elevators.forEach(Elevator::shutdown);
		elevatorThreadPool.shutdown();
		agingScheduler.shutdown();
		elevatorThreadPool.awaitTermination(5, TimeUnit.SECONDS);
		System.out.println("[Controller] Shutdown complete.");
	}

	// ── Helpers ───────────────────────────────────────────────────────────────
	private Elevator findById(int id) {
		return elevators.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
	}

	private Floor findFloor(int num) {
		return floors.stream().filter(f -> f.getFloorNumber() == num).findFirst().orElse(null);
	}

	public List<Elevator> getElevators() {
		return List.copyOf(elevators);
	}

	public List<Floor> getFloors() {
		return List.copyOf(floors);
	}

	// ── Default event listener (console logging) ──────────────────────────────
	private ElevatorEventListener buildEventListener() {
		return new ElevatorEventListener() {
			@Override
			public void onFloorReached(int elevatorId, int floor) {
			}

			@Override
			public void onStateChanged(int elevatorId, com.elevator.enums.ElevatorState s) {
				if (s == ElevatorState.OVERLOADED || s == ElevatorState.MAINTENANCE) {
					System.out.println("[E" + elevatorId + "] ⚠️  State → " + s);
				}
			}

			@Override
			public void onDoorOpened(int elevatorId, int floor) {
				System.out.println("[E" + elevatorId + "] 🚪 Door OPEN  at floor " + floor);
			}

			@Override
			public void onDoorClosed(int elevatorId, int floor) {
				System.out.println("[E" + elevatorId + "] 🚪 Door CLOSE at floor " + floor);
			}
		};
	}
}
