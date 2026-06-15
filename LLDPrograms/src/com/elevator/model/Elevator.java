package com.elevator.model;

import com.elevator.enums.Direction;
import com.elevator.enums.DoorStatus;
import com.elevator.enums.ElevatorState;
import com.elevator.observer.ElevatorEventListener;
import com.elevator.request.InternalRequest;
import com.elevator.request.Request;
import com.elevator.request.RequestQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a single elevator car.
 *
 * Design patterns used:
 *  - State Pattern    : ElevatorState drives behavior (IDLE / MOVING / STOPPED / MAINTENANCE)
 *  - Observer Pattern : Notifies ElevatorEventListener on each event
 *  - Command Pattern  : Processes Request objects from the queue
 *
 * Thread safety:
 *  - currentFloor volatile (read by multiple threads)
 *  - state volatile
 *  - RequestQueue uses ReentrantLock internally
 *  - Door methods are synchronized
 *  - Runs its own service thread
 */
public class Elevator implements Runnable {

	private final int id;
	private volatile int currentFloor;
	private volatile ElevatorState state;
	private volatile Direction direction;
	private final int maxCapacity;
	private volatile int currentLoad;
	private final Door door;
	private final RequestQueue requestQueue;
	private final List<ElevatorEventListener> listeners = new ArrayList<>();
	private final AtomicBoolean running = new AtomicBoolean(true);

	private static final long FLOOR_TRAVEL_TIME_MS = 400;
	private static final long DOOR_WAIT_TIME_MS = 600;

	public Elevator(int id, int startFloor, int maxCapacity) {
		this.id = id;
		this.currentFloor = startFloor;
		this.maxCapacity = maxCapacity;
		this.currentLoad = 0;
		this.state = ElevatorState.IDLE;
		this.direction = Direction.IDLE;
		this.door = new Door();
		this.requestQueue = new RequestQueue();
	}

	// ── Observer support ─────────────────────────────────────────────────────
	public void addListener(ElevatorEventListener listener) {
		listeners.add(listener);
	}

	private void notifyFloorReached(int floor) {
		listeners.forEach(l -> l.onFloorReached(id, floor));
	}

	private void notifyStateChanged(ElevatorState s) {
		listeners.forEach(l -> l.onStateChanged(id, s));
	}

	private void notifyDoorOpened(int floor) {
		listeners.forEach(l -> l.onDoorOpened(id, floor));
	}

	private void notifyDoorClosed(int floor) {
		listeners.forEach(l -> l.onDoorClosed(id, floor));
	}

	// ── Internal request from cabin panel ────────────────────────────────────
	public void pressFloorButton(int destinationFloor) {
		if (state == ElevatorState.MAINTENANCE) {
			System.out.println("[Elevator " + id + "] In maintenance, ignoring request.");
			return;
		}
		if (!requestQueue.containsFloor(destinationFloor)) {
			requestQueue.addRequest(new InternalRequest(id, destinationFloor));
		}
	}

	// ── External request assignment from Dispatcher ──────────────────────────
	public void assignRequest(Request request) {
		if (state == ElevatorState.MAINTENANCE || state == ElevatorState.OVERLOADED) {
			System.out.println("[Elevator " + id + "] Cannot accept request: " + state);
			return;
		}
		if (!requestQueue.containsFloor(request.getTargetFloor())) {
			requestQueue.addRequest(request);
		}
	}

	// ── Main service loop (runs in own thread) ────────────────────────────────
	@Override
	public void run() {
		System.out.println("[Elevator " + id + "] Service thread started on floor " + currentFloor);
		while (running.get()) {
			try {
				requestQueue.ageRequests(); // anti-starvation

				if (requestQueue.isEmpty()) {
					setState(ElevatorState.IDLE);
					setDirection(Direction.IDLE);
					Thread.sleep(200);
					continue;
				}

				Request next = requestQueue.nextRequest();
				if (next == null)
					continue;

				System.out.println("[Elevator " + id + "] Processing: " + next);
				moveToFloor(next.getTargetFloor());
				serveFloor(next.getTargetFloor());

			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
		System.out.println("[Elevator " + id + "] Service thread stopped.");
	}

	// ── Movement ──────────────────────────────────────────────────────────────
	private void moveToFloor(int targetFloor) throws InterruptedException {
		if (currentFloor == targetFloor)
			return;

		Direction moveDir = targetFloor > currentFloor ? Direction.UP : Direction.DOWN;
		setDirection(moveDir);
		setState(ElevatorState.MOVING);

		System.out.println("[E" + id + "] Moving " + moveDir + "  floor " + currentFloor + " → " + targetFloor);

		while (currentFloor != targetFloor) {
			Thread.sleep(FLOOR_TRAVEL_TIME_MS);
			currentFloor += (moveDir == Direction.UP) ? 1 : -1;
			System.out.println("[E" + id + "] floor " + currentFloor);
			notifyFloorReached(currentFloor);
		}
	}

	// ── Door service at floor ─────────────────────────────────────────────────
	private void serveFloor(int floor) throws InterruptedException {
		setState(ElevatorState.STOPPED);
		door.open();
		notifyDoorOpened(floor);

		System.out.println("[E" + id + "] ✅ Serving floor " + floor);
		Thread.sleep(DOOR_WAIT_TIME_MS);

		door.close();
		notifyDoorClosed(floor);
	}

	// ── Maintenance & Overload ────────────────────────────────────────────────
	public void setMaintenanceMode(boolean enabled) {
		if (enabled) {
			setState(ElevatorState.MAINTENANCE);
			System.out.println("[Elevator " + id + "] Entered MAINTENANCE mode.");
		} else {
			setState(ElevatorState.IDLE);
			System.out.println("[Elevator " + id + "] Exited maintenance mode.");
		}
	}

	public synchronized void updateLoad(int delta) {
		currentLoad += delta;
		if (currentLoad >= maxCapacity) {
			setState(ElevatorState.OVERLOADED);
			System.out.println("[Elevator " + id + "] OVERLOADED! Load=" + currentLoad);
		} else if (state == ElevatorState.OVERLOADED) {
			setState(ElevatorState.IDLE);
		}
	}

	public void shutdown() {
		running.set(false);
	}

	// ── Getters ───────────────────────────────────────────────────────────────
	private void setState(ElevatorState s) {
		this.state = s;
		notifyStateChanged(s);
	}

	private void setDirection(Direction d) {
		this.direction = d;
	}

	public int getId() {
		return id;
	}

	public int getCurrentFloor() {
		return currentFloor;
	}

	public ElevatorState getState() {
		return state;
	}

	public Direction getDirection() {
		return direction;
	}

	public int getCurrentLoad() {
		return currentLoad;
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public boolean isAvailable() {
		return state != ElevatorState.MAINTENANCE && state != ElevatorState.OVERLOADED;
	}

	public int getQueueSize() {
		return requestQueue.size();
	}

	public Door getDoor() {
		return door;
	}

	@Override
	public String toString() {
		return "Elevator[id=" + id + ", floor=" + currentFloor + ", state=" + state + ", dir=" + direction
				+ ", queueSize=" + getQueueSize() + "]";
	}
}
