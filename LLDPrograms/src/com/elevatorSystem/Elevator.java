package com.elevatorSystem;

import java.util.Set;
import java.util.TreeSet;

public class Elevator {
	// ====================== ELEVATOR ======================
	private int id;
	private int currentFloor = 0;
	private Direction direction = Direction.IDLE;
	private ElevatorStatus status = ElevatorStatus.STOPPED;
	private DoorState doorState = DoorState.CLOSED;
	private Set<Integer> targetFloors = new TreeSet<>();
	private final Object lock = new Object();

	public Elevator(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public int getCurrentFloor() {
		return currentFloor;
	}

	public Direction getDirection() {
		return direction;
	}

	public ElevatorStatus getStatus() {
		return status;
	}

	public void addTargetFloor(int floor) {
		synchronized (lock) {
			targetFloors.add(floor);
		}
	}

	public void move() {
		synchronized (lock) {
			if (targetFloors.isEmpty()) {
				direction = Direction.IDLE;
				status = ElevatorStatus.STOPPED;
				return;
			}

			int nextFloor = direction == Direction.UP ? ((TreeSet<Integer>) targetFloors).first()
					: ((TreeSet<Integer>) targetFloors).last();

			System.out
					.println("Elevator " + id + " moving " + direction + " from " + currentFloor + " to " + nextFloor);

			// Simulate movement
			while (currentFloor != nextFloor) {
				currentFloor += (direction == Direction.UP ? 1 : -1);
				try {
					Thread.sleep(800);
				} catch (InterruptedException e) {
				}
				System.out.print(".");
			}
			System.out.println("\nElevator " + id + " arrived at floor " + currentFloor);

			openDoor();
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
			}
			closeDoor();

			targetFloors.remove(currentFloor);

			if (targetFloors.isEmpty()) {
				direction = Direction.IDLE;
				status = ElevatorStatus.STOPPED;
			}
		}
	}

	private void openDoor() {
		doorState = DoorState.OPEN;
		System.out.println("Elevator " + id + " doors OPEN at floor " + currentFloor);
	}

	private void closeDoor() {
		doorState = DoorState.CLOSED;
		System.out.println("Elevator " + id + " doors CLOSED");
	}

	public boolean canServe(Request request) {
		if (status == ElevatorStatus.MAINTENANCE)
			return false;
		if (direction == Direction.IDLE)
			return true;
		return (direction == request.getDirection())
				&& ((direction == Direction.UP && request.getFloor() >= currentFloor)
						|| (direction == Direction.DOWN && request.getFloor() <= currentFloor));
	}
}