package com.elevator.request;

import com.elevator.enums.Direction;

/**
 * External request: user presses UP or DOWN on a floor panel.
 */
public class ExternalRequest extends Request {

	private final int sourceFloor;
	private final Direction direction;

	public ExternalRequest(int sourceFloor, Direction direction) {
		super(sourceFloor); // target = the floor where the button was pressed
		this.sourceFloor = sourceFloor;
		this.direction = direction;
	}

	public int getSourceFloor() {
		return sourceFloor;
	}

	public Direction getDirection() {
		return direction;
	}

	@Override
	public String toString() {
		return "ExternalRequest[floor=" + sourceFloor + ", dir=" + direction + ", priority=" + getPriority() + "]";
	}
}
