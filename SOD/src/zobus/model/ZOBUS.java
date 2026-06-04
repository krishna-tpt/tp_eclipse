package zobus.model;

import zobus.model.*;
import zobus.service.BookingService;

import java.util.*;

public class ZOBUS {
    private static BookingService service = new BookingService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to ZOBUS - Chennai to Bengaluru Booking System");
        while (true) {
            System.out.println("\n1. Sign Up | 2. Login | 3. View Buses | 4. Book Ticket | 5. Cancel Ticket | 6. Bus Summary | 0. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1: signUp(); break;
                case 2: login(); break;
                case 3: viewBuses(); break;
                case 4: bookTicket(); break;
                case 5: cancelTicket(); break;
                case 6: busSummary(); break;
                case 0: return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private static void signUp() {
        System.out.print("Name: "); String name = scanner.nextLine();
        System.out.print("Password: "); String pwd = scanner.nextLine();
        System.out.print("Age: "); int age = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Gender (M/F): "); String gender = scanner.nextLine();
        Customer cust = service.signUp(name, pwd, age, gender);
        System.out.println("Signed up! ID: " + cust.getId());
    }

    private static void login() {
        System.out.print("Name: "); String name = scanner.nextLine();
        System.out.print("Password: "); String pwd = scanner.nextLine();
        Customer cust = service.login(name, pwd);
        if (cust != null) {
            System.out.println("Logged in as " + cust.getName());
            // Proceed to menu, but simplified here
        } else {
            System.out.println("Invalid credentials.");
        }
    }

    private static void viewBuses() {
        System.out.print("AC Filter (AC/Non-AC/Both): "); String ac = scanner.nextLine();
        System.out.print("Type Filter (Seater/Sleeper/Both): "); String type = scanner.nextLine();
        List<Bus> filtered = service.filterBuses(ac, type);
        for (int i = 0; i < filtered.size(); i++) {
            Bus b = filtered.get(i);
            String t = b.getType().toString().replace("_", " ");
            System.out.println(i + ") " + t + " - " + b.getRemainingSeats() + " Seats");
        }
    }

    private static void bookTicket() {
        // Assume logged in customer; simplified - use ID 1 for demo
        Customer cust = service.getCustomers().get(0);
        System.out.print("Num Passengers: "); int num = scanner.nextInt();
        scanner.nextLine();
        // Select bus index from filtered (simplified: pick first non-full)
        Bus bus = service.getBuses().stream().filter(b -> !b.isFull()).findFirst().orElse(null);
        if (bus == null) { System.out.println("No buses available."); return; }

        System.out.println("Select seats for " + num + " passengers (manual input simplified):");
        bus.displaySeats();
        // Simulated input: assume valid selection
        List<String> names = Arrays.asList("Trav1", "Trav2"); // Demo
        List<String> genders = Arrays.asList("M", "F");
        Booking booking = service.book(cust, num, bus, names, genders);
        if (booking != null) {
            System.out.println("Booked! Fare: " + booking.getTotalFare());
        } else {
            System.out.println("Booking failed (rules/seats).");
        }
    }

    private static void cancelTicket() {
        // Demo for customer 1
        Customer cust = service.getCustomers().get(0);
        // Simulated: cancel first seat
        List<Seat> toCancel = new ArrayList<>();
        if (cust.getBooking() != null) {
            toCancel.add(cust.getBooking().getSeats().get(0));
            double fee = service.cancel(cust, toCancel);
            System.out.println("Cancelled. Fee: " + fee);
        }
    }

    private static void busSummary() {
        Bus bus = service.getBuses().get(0); // Demo first bus
        service.printBusSummary(bus);
    }
}