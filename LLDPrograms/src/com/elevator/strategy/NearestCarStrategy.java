package com.elevator.strategy;

import com.elevator.enums.Direction;
import com.elevator.enums.ElevatorState;
import com.elevator.model.Elevator;
import com.elevator.request.ExternalRequest;

import java.util.List;

/**
 * Nearest Car dispatching algorithm.
 *
 * Scoring rules (lower = better): 1. IDLE elevator on same floor → score 0
 * (best) 2. MOVING elevator en-route in same dir → score = distance (good) 3.
 * IDLE elevator on different floor → score = distance 4. MOVING elevator in
 * opposite direction → score = high penalty
 */
public class NearestCarStrategy implements DispatchStrategy {

	private static final int OPPOSITE_DIRECTION_PENALTY = 100;

	@Override
	public Elevator dispatch(ExternalRequest request, List<Elevator> elevators) {
		Elevator best = null;
		int bestScore = Integer.MAX_VALUE;

		for (Elevator e : elevators) {
			if (!e.isAvailable())
				continue;

			int score = computeScore(e, request);
			System.out.println("  [NearestCar] Elevator " + e.getId() + " score=" + score + " for " + request);

			if (score < bestScore) {
				bestScore = score;
				best = e;
			}
		}
		return best;
	}

	private int computeScore(Elevator e, ExternalRequest req) {
		int distance = Math.abs(e.getCurrentFloor() - req.getSourceFloor());

		if (e.getState() == ElevatorState.IDLE) {
			return distance == 0 ? 0 : distance;
		}

		// Elevator is moving — check if it's heading toward the request in the right
		// direction
		boolean headingToward = isHeadingToward(e, req.getSourceFloor());
		boolean sameDirection = e.getDirection() == req.getDirection();

		if (headingToward && sameDirection) {
			return distance; // best case for a moving elevator
		}
		return distance + OPPOSITE_DIRECTION_PENALTY;
	}

	private boolean isHeadingToward(Elevator e, int targetFloor) {
		if (e.getDirection() == Direction.UP)
			return e.getCurrentFloor() < targetFloor;
		if (e.getDirection() == Direction.DOWN)
			return e.getCurrentFloor() > targetFloor;
		return false;
	}
}
