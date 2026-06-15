package com.elevator.strategy;

import com.elevator.model.Elevator;
import com.elevator.request.ExternalRequest;

import java.util.List;

/**
 * Strategy Pattern interface for elevator dispatch algorithms. Swap
 * implementations without changing ElevatorController.
 */
public interface DispatchStrategy {
	/**
	 * Select the best elevator for a given external request. Returns null if no
	 * elevator is available.
	 */
	Elevator dispatch(ExternalRequest request, List<Elevator> elevators);
}
