// com.zobus.service.BookingService.java (Major updates: No streams)
package zobus.service;

import java.util.*;

import zobus.model.*;

public class BookingService {
	private List<Bus> buses;
	private List<Customer> customers;
	private List<Booking> bookings;
	private int nextBookingId = 1;

	public BookingService() {
		initialize();
	}

	private void initialize() {
		buses = new ArrayList<>();
		customers = new ArrayList<>();
		bookings = new ArrayList<>();

		// Initial Customers (unchanged)
		customers.add(new Customer(1, "aaa", "111", 25, "F"));
		customers.add(new Customer(2, "bbb", "222", 61, "M"));
		customers.add(new Customer(3, "ccc", "333", 22, "M"));
		customers.add(new Customer(4, "ddd", "444", 36, "F"));

		// Buses (unchanged)
		List<String> sleeperSeats = Arrays.asList("Lower1A", "Lower1B", "Lower2A", "Lower2B", "Lower3A", "Lower3B",
				"Upper4A", "Upper4B", "Upper5A", "Upper5B", "Upper6A", "Upper6B");
		buses.add(new Bus(Bus.Type.AC_SLEEPER, sleeperSeats, 700));
		buses.add(new Bus(Bus.Type.NON_AC_SLEEPER, sleeperSeats, 600));

		List<String> seaterSeats = Arrays.asList("1A", "1B", "1C", "2A", "2B", "2C", "3A", "3B", "3C", "4A", "4B",
				"4C");
		buses.add(new Bus(Bus.Type.AC_SEATER, seaterSeats, 550));
		buses.add(new Bus(Bus.Type.NON_AC_SEATER, seaterSeats, 450));
	}

	// Task 2: Sign-up (unchanged)
	public Customer signUp(String name, String password, int age, String gender) {
		int id = customers.size() + 1;
		Customer cust = new Customer(id, name, password, age, gender);
		customers.add(cust);
		return cust;
	}

	// Updated Task 3: Login - No streams
	public Customer login(String name, String password) {
		for (Customer c : customers) {
			if (c.getName().equals(name) && c.getPassword().equals(password)) {
				return c;
			}
		}
		return null;
	}

	// Updated Task 4: Book - No streams
	public Booking book(Customer customer, int numPassengers, Bus selectedBus, List<String> travelerNames,
			List<String> travelerGenders) {
		if (customer.getBooking() != null)
			return null; // One ticket rule
		if (selectedBus.getRemainingSeats() < numPassengers)
			return null;

		// Collect available seats
		List<Seat> availableSeats = new ArrayList<>();
		for (Seat seat : selectedBus.getSeats()) {
			if (seat.isVacant()) {
				availableSeats.add(seat);
			}
		}
		List<Seat> selectedSeats = new ArrayList<>();
		for (int i = 0; i < numPassengers; i++) {
			selectedSeats.add(availableSeats.get(i)); // First available
		}

		if (!selectedBus.canAssignSeats(selectedSeats, travelerGenders, null))
			return null;

		Booking booking = new Booking(nextBookingId++, customer, selectedBus, selectedSeats, travelerNames,
				travelerGenders);
		booking.assignSeats();
		customer.setBooking(booking);
		bookings.add(booking);
		return booking;
	}

	// Task 7: Cancel (unchanged, already loop-based)
	public double cancel(Customer customer, List<Seat> toCancel) {
		Booking booking = customer.getBooking();
		if (booking == null)
			return 0;
		double fee = booking.cancelSeats(toCancel);
		if (booking.getSeats().isEmpty()) {
			customer.setBooking(null);
			bookings.remove(booking);
		}
		return fee;
	}

	// Updated Task 8: Filter Buses - No streams, manual sort
	public List<Bus> filterBuses(String acFilter, String typeFilter) {
		List<Bus> filtered = new ArrayList<>();
		for (Bus bus : buses) {
			if (!bus.isFull() && matchesFilter(bus, acFilter, typeFilter)) {
				filtered.add(bus);
			}
		}

		// Manual sort: Remaining seats desc, then AC > Sleeper priority
		Collections.sort(filtered, new Comparator<Bus>() {
			@Override
			public int compare(Bus b1, Bus b2) {
				// Remaining seats desc
				int cmp = Integer.compare(b2.getRemainingSeats(), b1.getRemainingSeats());
				if (cmp != 0)
					return cmp;

				// AC first: AC_SLEEPER/AC_SEATER > NON_AC
				boolean b1IsAC = (b1.getType() == Bus.Type.AC_SLEEPER || b1.getType() == Bus.Type.AC_SEATER);
				boolean b2IsAC = (b2.getType() == Bus.Type.AC_SLEEPER || b2.getType() == Bus.Type.AC_SEATER);
				if (b1IsAC != b2IsAC) {
					return b1IsAC ? -1 : 1;
				}

				// Sleeper > Seater (but since AC already sorted, this is secondary)
				boolean b1IsSleeper = (b1.getType() == Bus.Type.AC_SLEEPER || b1.getType() == Bus.Type.NON_AC_SLEEPER);
				boolean b2IsSleeper = (b2.getType() == Bus.Type.AC_SLEEPER || b2.getType() == Bus.Type.NON_AC_SLEEPER);
				if (b1IsSleeper != b2IsSleeper) {
					return b1IsSleeper ? -1 : 1;
				}

				return 0;
			}
		});
		return filtered;
	}

	private boolean matchesFilter(Bus bus, String acFilter, String typeFilter) {
		boolean acMatch = acFilter.equals("Both")
				|| (acFilter.equals("AC")
						&& (bus.getType() == Bus.Type.AC_SEATER || bus.getType() == Bus.Type.AC_SLEEPER))
				|| (acFilter.equals("Non-AC")
						&& (bus.getType() == Bus.Type.NON_AC_SEATER || bus.getType() == Bus.Type.NON_AC_SLEEPER));
		boolean typeMatch = typeFilter.equals("Both")
				|| (typeFilter.equals("Seater")
						&& (bus.getType() == Bus.Type.AC_SEATER || bus.getType() == Bus.Type.NON_AC_SEATER))
				|| (typeFilter.equals("Sleeper")
						&& (bus.getType() == Bus.Type.AC_SLEEPER || bus.getType() == Bus.Type.NON_AC_SLEEPER));
		return acMatch && typeMatch;
	}

	// Updated Task 9: Bus Summary - Loop-based totalFare calc
	public void printBusSummary(Bus bus) {
		int filled = bus.getSeats().size() - bus.getRemainingSeats();
		double totalFare = 0;
		for (Booking b : bookings) {
			if (b.getBus() == bus) {
				totalFare += b.getTotalFare() + b.getCancellationFee();
			}
		}
		System.out.println("- " + bus.getType());
		System.out.println("- Number of Seats Filled: " + filled);
		System.out.println("Total Fare Collected: " + totalFare);
		System.out.println("Seat Details:");
		System.out.println("| Seat | Name | Gender |");
		for (Seat seat : bus.getSeats()) {
			if (!seat.isVacant()) {
				System.out.println(
						"| " + seat.getId() + " | " + seat.getOccupantName()+ " | " + seat.getOccupantGender() + " |");
			}
		}
	}

	// Getters (unchanged)
	public List<Bus> getBuses() {
		return buses;
	}

	public List<Customer> getCustomers() {
		return customers;
	}
}