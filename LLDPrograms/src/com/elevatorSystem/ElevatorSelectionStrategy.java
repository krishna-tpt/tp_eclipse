package com.elevatorSystem;

import java.util.List;

//====================== STRATEGY PATTERN ======================
public interface ElevatorSelectionStrategy {
	Elevator selectElevator(List<Elevator> elevators, Request request);
}