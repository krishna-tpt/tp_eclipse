package com.elevatorSystem;

import java.util.List;

public class NearestElevatorStrategy implements ElevatorSelectionStrategy {
	@Override
	public Elevator selectElevator(List<Elevator> elevators, Request request) {
		Elevator best = null;
		int minDistance = Integer.MAX_VALUE;

		for (Elevator e : elevators) {
			if (!e.canServe(request))
				continue;

			int distance = Math.abs(e.getCurrentFloor() - request.getFloor());
			if (distance < minDistance) {
				minDistance = distance;
				best = e;
			}
		}
		return best;
	}
}