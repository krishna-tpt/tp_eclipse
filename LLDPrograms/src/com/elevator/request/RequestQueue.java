package com.elevator.request;

import java.util.PriorityQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.List;
import java.util.ArrayList;

/**
 * Thread-safe request queue for a single elevator.
 * Uses PriorityQueue so higher-priority (aged) requests are served first.
 * Starvation prevention: periodically ages all waiting requests.
 */
public class RequestQueue {

	private final PriorityQueue<Request> queue;
	private final ReentrantLock lock;

	public RequestQueue() {
		this.queue = new PriorityQueue<>();
		this.lock = new ReentrantLock(true); // fair lock
	}

	public void addRequest(Request request) {
		lock.lock();
		try {
			queue.offer(request);
			System.out.println("  [Queue] Added: " + request);
		} finally {
			lock.unlock();
		}
	}

	public Request nextRequest() {
		lock.lock();
		try {
			return queue.poll();
		} finally {
			lock.unlock();
		}
	}

	public Request peek() {
		lock.lock();
		try {
			return queue.peek();
		} finally {
			lock.unlock();
		}
	}

	public boolean isEmpty() {
		lock.lock();
		try {
			return queue.isEmpty();
		} finally {
			lock.unlock();
		}
	}

	public int size() {
		lock.lock();
		try {
			return queue.size();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Aging: called periodically to prevent starvation. Rebuilds the queue after
	 * adjusting priorities.
	 */
	public void ageRequests() {
		lock.lock();
		try {
			List<Request> all = new ArrayList<>(queue);
			queue.clear();
			for (Request r : all) {
				r.age();
				queue.offer(r);
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Check if a specific floor is already queued (deduplication).
	 */
	public boolean containsFloor(int floor) {
		lock.lock();
		try {
			return queue.stream().anyMatch(r -> r.getTargetFloor() == floor);
		} finally {
			lock.unlock();
		}
	}
}
