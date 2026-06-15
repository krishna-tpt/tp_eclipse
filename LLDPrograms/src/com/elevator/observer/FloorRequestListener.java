package com.elevator.observer;

import com.elevator.request.ExternalRequest;
import com.elevator.request.InternalRequest;

/**
 * Observer Pattern interfaces for elevator events.
 */

// ── Subscriber for floor button presses ──────────────────────────────────────
public interface FloorRequestListener {
	void onExternalRequest(ExternalRequest request);
}
