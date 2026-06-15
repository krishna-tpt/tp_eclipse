package com.elevator.model;

import com.elevator.enums.DoorStatus;

/**
 * Represents the door of an elevator car.
 * Thread-safe via synchronized methods.
 */
public class Door {

	private volatile DoorStatus status;
	private static final long DOOR_OPERATION_DELAY_MS = 500;

	public Door() {
		this.status = DoorStatus.CLOSED;
	}

	public synchronized void open() {
		if (status == DoorStatus.OPEN)
			return;
		simulateDelay();
		this.status = DoorStatus.OPEN;
	}

	public synchronized void close() {
		if (status == DoorStatus.CLOSED)
			return;
		simulateDelay();
		this.status = DoorStatus.CLOSED;
	}

	public boolean isOpen() {
		return status == DoorStatus.OPEN;
	}

	public boolean isClosed() {
		return status == DoorStatus.CLOSED;
	}

	public DoorStatus getStatus() {
		return status;
	}

	private void simulateDelay() {
		try {
			Thread.sleep(DOOR_OPERATION_DELAY_MS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
