package com.elevator.request;

import java.time.Instant;
import java.util.UUID;

/**
 * Abstract base for all elevator requests.
 * Implements Command Pattern — each request is a command to be executed.
 */
public abstract class Request implements Comparable<Request> {

    private final String requestId;
    private final int targetFloor;
    private final Instant createdAt;
    private int priority;   // lower = higher priority (for starvation prevention)

    protected Request(int targetFloor) {
        this.requestId  = UUID.randomUUID().toString();
        this.targetFloor = targetFloor;
        this.createdAt  = Instant.now();
        this.priority   = 0;
    }

    public String getRequestId()   { return requestId; }
    public int getTargetFloor()    { return targetFloor; }
    public Instant getCreatedAt()  { return createdAt; }
    public int getPriority()       { return priority; }
    public void setPriority(int p) { this.priority = p; }

    /**
     * Aging mechanism: called periodically to prevent starvation.
     * Increases priority so long-waiting requests get served first.
     */
    public void age() {
        this.priority = Math.max(0, this.priority - 1);
    }

    @Override
    public int compareTo(Request other) {
        // PriorityQueue: lower value = higher priority
        return Integer.compare(this.priority, other.priority);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[id=" + requestId.substring(0, 8)
            + ", floor=" + targetFloor + ", priority=" + priority + "]";
    }
}
