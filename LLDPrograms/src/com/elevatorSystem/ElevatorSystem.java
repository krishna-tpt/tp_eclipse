package com.elevatorSystem;

import java.util.*;
import java.util.concurrent.*;

// ====================== ENUMS ======================
enum Direction {
	UP, DOWN, IDLE
}

enum DoorState {
	OPEN, CLOSED
}

enum ElevatorStatus {
	MOVING, STOPPED, MAINTENANCE
}

// ====================== MAIN APPLICATION ======================
public class ElevatorSystem {
	public static void main(String[] args) throws InterruptedException {
		Scanner sc = new Scanner(System.in);

		System.out.print("Enter number of floors: ");
		int floors = sc.nextInt();
		System.out.print("Enter number of elevators: ");
		int elevators = sc.nextInt();
		sc.nextLine();

		ElevatorController controller = ElevatorController.getInstance(elevators);

		System.out.println("\nElevator System Started! (Floors 0-" + (floors - 1) + ")\n");

		while (true) {
			System.out.println("\n1. External UP Request");
			System.out.println("2. External DOWN Request");
			System.out.println("3. Internal Request (from inside elevator)");
			System.out.println("4. Show Status");
			System.out.println("5. Exit");
			System.out.print("Choose: ");

			int choice = sc.nextInt();
			sc.nextLine();

			switch (choice) {
			case 1:
				System.out.print("Enter floor: ");
				int f1 = sc.nextInt();
				controller.addExternalRequest(f1, Direction.UP);
				break;

			case 2:
				System.out.print("Enter floor: ");
				int f2 = sc.nextInt();
				controller.addExternalRequest(f2, Direction.DOWN);
				break;

			case 3:
				System.out.print("Enter Elevator ID: ");
				int eid = sc.nextInt();
				System.out.print("Enter Destination Floor: ");
				int dest = sc.nextInt();
				controller.addInternalRequest(eid, dest);
				break;

			case 4:
				controller.printStatus();
				break;

			case 5:
				System.out.println("Shutting down system...");
				sc.close();
				return;
			}
		}
	}
}