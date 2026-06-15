package com.elevator.request;

/**
 * Internal request: user inside the elevator presses a destination floor button.
 */
public class InternalRequest extends Request {

    private final int elevatorId;
    private final int destinationFloor;

    public InternalRequest(int elevatorId, int destinationFloor) {
        super(destinationFloor);
        this.elevatorId       = elevatorId;
        this.destinationFloor = destinationFloor;
    }

    public int getElevatorId()       { return elevatorId; }
    public int getDestinationFloor() { return destinationFloor; }

    @Override
    public String toString() {
        return "InternalRequest[elevatorId=" + elevatorId
            + ", dest=" + destinationFloor + ", priority=" + getPriority() + "]";
    }
}
