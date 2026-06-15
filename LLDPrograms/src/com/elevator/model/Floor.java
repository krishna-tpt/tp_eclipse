package com.elevator.model;

import com.elevator.enums.Direction;
import com.elevator.observer.FloorRequestListener;
import com.elevator.request.ExternalRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a building floor with UP/DOWN call buttons.
 * Implements Observer (Subject) pattern — notifies controller on button press.
 */
public class Floor {

	private final int floorNumber;
	private volatile boolean upButtonActive;
	private volatile boolean downButtonActive;
	private final List<FloorRequestListener> listeners = new ArrayList<>();

	public Floor(int floorNumber) {
		this.floorNumber = floorNumber;
	}

	public void addListener(FloorRequestListener listener) {
		listeners.add(listener);
	}

	/**
	 * User presses UP button on this floor. Notifies all registered listeners
	 * (ElevatorController).
	 */
	public synchronized void pressUp() {
		if (upButtonActive) {
			System.out.println("[Floor " + floorNumber + "] UP already requested, skipping.");
			return;
		}
		upButtonActive = true;
		System.out.println("[Floor " + floorNumber + "] UP button pressed");
		ExternalRequest req = new ExternalRequest(floorNumber, Direction.UP);
		listeners.forEach(l -> l.onExternalRequest(req));
	}

	/**
	 * User presses DOWN button on this floor.
	 */
	public synchronized void pressDown() {
		if (downButtonActive) {
			System.out.println("[Floor " + floorNumber + "] DOWN already requested, skipping.");
			return;
		}
		downButtonActive = true;
		System.out.println("[Floor " + floorNumber + "] DOWN button pressed");
		ExternalRequest req = new ExternalRequest(floorNumber, Direction.DOWN);
		listeners.forEach(l -> l.onExternalRequest(req));
	}

	/** Called when elevator arrives at this floor — resets button lights. */
	public synchronized void resetButtons(Direction direction) {
		if (direction == Direction.UP)
			upButtonActive = false;
		if (direction == Direction.DOWN)
			downButtonActive = false;
	}

	public int getFloorNumber() {
		return floorNumber;
	}

	public boolean isUpButtonActive() {
		return upButtonActive;
	}

	public boolean isDownButtonActive() {
		return downButtonActive;
	}
}
