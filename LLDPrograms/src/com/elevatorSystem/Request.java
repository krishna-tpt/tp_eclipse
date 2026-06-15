package com.elevatorSystem;

//====================== REQUESTS ======================
public class Request {
	private int floor;
	private Direction direction; // For external requests
	private boolean isInternal;

	public Request(int floor, Direction direction, boolean isInternal) {
		this.floor = floor;
		this.direction = direction;
		this.isInternal = isInternal;
	}

	public int getFloor() {
		return floor;
	}

	public Direction getDirection() {
		return direction;
	}

	public boolean isInternal() {
		return isInternal;
	}
}