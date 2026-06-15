package com.elevator.strategy;

import com.elevator.model.Elevator;
import com.elevator.request.ExternalRequest;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Round-Robin dispatching strategy. Distributes requests evenly across all
 * available elevators. Good for peak traffic (follow-up interview: optimizing
 * for peak).
 */
public class RoundRobinStrategy implements DispatchStrategy {

	private final AtomicInteger counter = new AtomicInteger(0);

	@Override
	public Elevator dispatch(ExternalRequest request, List<Elevator> elevators) {
		List<Elevator> available = elevators.stream().filter(Elevator::isAvailable).toList();

		if (available.isEmpty())
			return null;

		int idx = Math.abs(counter.getAndIncrement() % available.size());
		Elevator chosen = available.get(idx);
		System.out.println("  [RoundRobin] Assigned to Elevator " + chosen.getId());
		return chosen;
	}
}
