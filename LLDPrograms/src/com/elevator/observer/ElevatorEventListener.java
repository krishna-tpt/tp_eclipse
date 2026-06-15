package com.elevator.observer;

import com.elevator.enums.ElevatorState;

/**
 * Observer for elevator state changes (e.g., logging, monitoring, display board).
 */
public interface ElevatorEventListener {
	void onFloorReached(int elevatorId, int floor);

	void onStateChanged(int elevatorId, ElevatorState newState);

	void onDoorOpened(int elevatorId, int floor);

	void onDoorClosed(int elevatorId, int floor);
}
