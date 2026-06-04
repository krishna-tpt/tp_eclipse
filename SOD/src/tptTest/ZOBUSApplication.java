package tptTest;

import java.util.*;
import java.util.stream.Collectors;

// Enums
enum BusType {
	SEATER, SLEEPER
}

enum ACType {
	AC, NON_AC
}

enum Gender {
	M, F
}

// Customer Class
class Customer {
	private static int idCounter = 5;
	private int id;
	private String name;
	private String password;
	private int age;
	private Gender gender;

	public Customer(int id, String name, String password, int age, Gender gender) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.age = age;
		this.gender = gender;
	}

	public Customer(String name, String password, int age, Gender gender) {
		this.id = idCounter++;
		this.name = name;
		this.password = password;
		this.age = age;
		this.gender = gender;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public int getAge() {
		return age;
	}

	public Gender getGender() {
		return gender;
	}
}

// Seat Class
class Seat {
	private String seatNumber;
	private boolean isOccupied;
	private String passengerName;
	private Gender passengerGender;
	private int customerId;
	private int bookingId;

	public Seat(String seatNumber) {
		this.seatNumber = seatNumber;
		this.isOccupied = false;
	}

	public void book(String passengerName, Gender passengerGender, int customerId, int bookingId) {
		this.isOccupied = true;
		this.passengerName = passengerName;
		this.passengerGender = passengerGender;
		this.customerId = customerId;
		this.bookingId = bookingId;
	}

	public void cancel() {
		this.isOccupied = false;
		this.passengerName = null;
		this.passengerGender = null;
		this.customerId = 0;
		this.bookingId = 0;
	}

	public String getSeatNumber() {
		return seatNumber;
	}

	public boolean isOccupied() {
		return isOccupied;
	}

	public String getPassengerName() {
		return passengerName;
	}

	public Gender getPassengerGender() {
		return passengerGender;
	}

	public int getCustomerId() {
		return customerId;
	}

	public int getBookingId() {
		return bookingId;
	}
}

// Booking Class
class Booking {
	private static int bookingCounter = 1;
	private int bookingId;
	private int customerId;
	private Bus bus;
	private List<String> seatNumbers;
	private double totalFare;
	private boolean isActive;

	public Booking(int customerId, Bus bus, List<String> seatNumbers, double totalFare) {
		this.bookingId = bookingCounter++;
		this.customerId = customerId;
		this.bus = bus;
		this.seatNumbers = new ArrayList<>(seatNumbers);
		this.totalFare = totalFare;
		this.isActive = true;
	}

	public int getBookingId() {
		return bookingId;
	}

	public int getCustomerId() {
		return customerId;
	}

	public Bus getBus() {
		return bus;
	}

	public List<String> getSeatNumbers() {
		return seatNumbers;
	}

	public double getTotalFare() {
		return totalFare;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}
}

// Bus Class
abstract class Bus {
	protected String busName;
	protected BusType busType;
	protected ACType acType;
	protected int totalSeats;
	protected Map<String, Seat> seats;
	protected double farePerSeat;
	protected double cancellationFeePercentage;
	protected double totalFareCollected;

	public Bus(String busName, BusType busType, ACType acType, int totalSeats, double farePerSeat) {
		this.busName = busName;
		this.busType = busType;
		this.acType = acType;
		this.totalSeats = totalSeats;
		this.farePerSeat = farePerSeat;
		this.cancellationFeePercentage = acType == ACType.AC ? 0.5 : 0.25;
		this.totalFareCollected = 0;
		this.seats = new LinkedHashMap<>();
	}

	public abstract void initializeSeats();

	public int getRemainingSeats() {
		return (int) seats.values().stream().filter(s -> !s.isOccupied()).count();
	}

	public boolean bookSeats(List<String> seatNumbers, List<String> passengerNames, List<Gender> passengerGenders,
			int customerId, int bookingId) {
		// Validate adjacent seat rule
		if (!validateAdjacentSeats(seatNumbers, passengerGenders)) {
			return false;
		}

		for (int i = 0; i < seatNumbers.size(); i++) {
			Seat seat = seats.get(seatNumbers.get(i));
			seat.book(passengerNames.get(i), passengerGenders.get(i), customerId, bookingId);
		}
		totalFareCollected += seatNumbers.size() * farePerSeat;
		return true;
	}

	public double cancelSeats(List<String> seatNumbers) {
		double refund = 0;
		for (String seatNumber : seatNumbers) {
			Seat seat = seats.get(seatNumber);
			if (seat != null && seat.isOccupied()) {
				seat.cancel();
				double cancellationFee = farePerSeat * cancellationFeePercentage;
				refund += farePerSeat - cancellationFee;
				totalFareCollected += cancellationFee;
			}
		}
		return refund;
	}

	protected boolean validateAdjacentSeats(List<String> seatNumbers, List<Gender> genders) {
		// Check if lady passengers from different tickets can sit adjacent
		for (String seatNumber : seatNumbers) {
			List<String> adjacentSeats = getAdjacentSeats(seatNumber);
			for (String adjSeat : adjacentSeats) {
				Seat seat = seats.get(adjSeat);
				if (seat != null && seat.isOccupied()) {
					int index = seatNumbers.indexOf(seatNumber);
					// If adjacent seat has a lady and current seat is for a male from different
					// booking
					if (seat.getPassengerGender() == Gender.F && genders.get(index) == Gender.M) {
						return false;
					}
				}
			}
		}
		return true;
	}

	protected abstract List<String> getAdjacentSeats(String seatNumber);

	public void displaySeats() {
		System.out.println("\n" + getBusDescription() + " - Seat Layout:");
		for (Seat seat : seats.values()) {
			String status = seat.isOccupied() ? "[X]" : "[ ]";
			System.out.print(seat.getSeatNumber() + status + " ");
		}
		System.out.println();
	}

	public String getBusDescription() {
		return acType + " " + busType;
	}

	public String getBusName() {
		return busName;
	}

	public BusType getBusType() {
		return busType;
	}

	public ACType getAcType() {
		return acType;
	}

	public int getTotalSeats() {
		return totalSeats;
	}

	public Map<String, Seat> getSeats() {
		return seats;
	}

	public double getFarePerSeat() {
		return farePerSeat;
	}

	public double getTotalFareCollected() {
		return totalFareCollected;
	}
}

// Seater Bus Class
class SeaterBus extends Bus {
	public SeaterBus(ACType acType) {
		super((acType == ACType.AC ? "AC Seater" : "Non-AC Seater"), BusType.SEATER, acType, 12,
				acType == ACType.AC ? 550 : 450);
		initializeSeats();
	}

	@Override
	public void initializeSeats() {
		String[] rows = { "A", "B", "C", "D" };
		for (String row : rows) {
			for (int i = 1; i <= 3; i++) {
				seats.put(row + i, new Seat(row + i));
			}
		}
	}

	@Override
	protected List<String> getAdjacentSeats(String seatNumber) {
		List<String> adjacent = new ArrayList<>();
		char row = seatNumber.charAt(0);
		int col = Integer.parseInt(seatNumber.substring(1));

		// Same row adjacent seats
		if (col > 1)
			adjacent.add(row + "" + (col - 1));
		if (col < 3)
			adjacent.add(row + "" + (col + 1));

		return adjacent;
	}
}

// Sleeper Bus Class
class SleeperBus extends Bus {
	public SleeperBus(ACType acType) {
		super((acType == ACType.AC ? "AC Sleeper" : "Non-AC Sleeper"), BusType.SLEEPER, acType, 12,
				acType == ACType.AC ? 700 : 600);
		initializeSeats();
	}

	@Override
	public void initializeSeats() {
		// Lower Deck
		for (int i = 1; i <= 6; i++) {
			seats.put("L" + i, new Seat("L" + i));
		}
		// Upper Deck
		for (int i = 1; i <= 6; i++) {
			seats.put("U" + i, new Seat("U" + i));
		}
	}

	@Override
	protected List<String> getAdjacentSeats(String seatNumber) {
		List<String> adjacent = new ArrayList<>();
		char deck = seatNumber.charAt(0);
		int num = Integer.parseInt(seatNumber.substring(1));

		// Adjacent seats in same deck
		if (num > 1)
			adjacent.add(deck + "" + (num - 1));
		if (num < 6)
			adjacent.add(deck + "" + (num + 1));

		return adjacent;
	}
}

// ZOBUS Application Class
public class ZOBUSApplication {
	private Map<Integer, Customer> customers;
	private List<Bus> buses;
	private Map<Integer, List<Booking>> customerBookings;
	private Customer loggedInCustomer;
	private Scanner scanner;

	public ZOBUSApplication() {
		customers = new HashMap<>();
		buses = new ArrayList<>();
		customerBookings = new HashMap<>();
		scanner = new Scanner(System.in);
		initializeData();
	}

	// Task 1: Initialization
	private void initializeData() {
		// Initialize customers
		customers.put(1, new Customer(1, "aaa", "111", 25, Gender.F));
		customers.put(2, new Customer(2, "bbb", "222", 61, Gender.M));
		customers.put(3, new Customer(3, "ccc", "333", 22, Gender.M));
		customers.put(4, new Customer(4, "ddd", "444", 36, Gender.F));

		// Initialize buses
		buses.add(new SleeperBus(ACType.AC));
		buses.add(new SleeperBus(ACType.NON_AC));
		buses.add(new SeaterBus(ACType.AC));
		buses.add(new SeaterBus(ACType.NON_AC));
	}

	// Task 2: Customer Sign-up
	public void signUp() {
		System.out.println("\n=== Customer Sign Up ===");
		System.out.print("Enter Name: ");
		String name = scanner.nextLine();
		System.out.print("Enter Password: ");
		String password = scanner.nextLine();
		System.out.print("Enter Age: ");
		int age = scanner.nextInt();
		scanner.nextLine();
		System.out.print("Enter Gender (M/F): ");
		Gender gender = Gender.valueOf(scanner.nextLine().toUpperCase());

		Customer newCustomer = new Customer(name, password, age, gender);
		customers.put(newCustomer.getId(), newCustomer);
		System.out.println("Sign up successful! Your ID: " + newCustomer.getId());
	}

	// Task 3: Customer Login
	public boolean login() {
		System.out.println("\n=== Customer Login ===");
		System.out.print("Enter Name: ");
		String name = scanner.nextLine();
		System.out.print("Enter Password: ");
		String password = scanner.nextLine();

		for (Customer customer : customers.values()) {
			if (customer.getName().equals(name) && customer.getPassword().equals(password)) {
				loggedInCustomer = customer;
				System.out.println("Login successful! Welcome, " + customer.getName());
				return true;
			}
		}
		System.out.println("Invalid credentials!");
		return false;
	}

	// Task 4: Booking a Ticket
	public void bookTicket() {
		if (loggedInCustomer == null) {
			System.out.println("Please login first!");
			return;
		}

		System.out.print("\nEnter number of passengers: ");
		int numPassengers = scanner.nextInt();
		scanner.nextLine();

		// Display bus options
		displayBusOptions();

		System.out.print("\nSelect Bus (1-4): ");
		int busChoice = scanner.nextInt() - 1;
		scanner.nextLine();

		if (busChoice < 0 || busChoice >= buses.size()) {
			System.out.println("Invalid bus selection!");
			return;
		}

		Bus selectedBus = buses.get(busChoice);

		if (selectedBus.getRemainingSeats() < numPassengers) {
			System.out.println("Not enough seats available!");
			return;
		}

		selectedBus.displaySeats();

		// Get passenger details
		List<String> passengerNames = new ArrayList<>();
		List<Gender> passengerGenders = new ArrayList<>();
		List<String> selectedSeats = new ArrayList<>();

		for (int i = 0; i < numPassengers; i++) {
			System.out.print("\nPassenger " + (i + 1) + " Name: ");
			passengerNames.add(scanner.nextLine());
			System.out.print("Passenger " + (i + 1) + " Gender (M/F): ");
			passengerGenders.add(Gender.valueOf(scanner.nextLine().toUpperCase()));
			System.out.print("Select Seat: ");
			selectedSeats.add(scanner.nextLine().toUpperCase());
		}

		// Calculate fare
		double totalFare = numPassengers * selectedBus.getFarePerSeat();
		System.out.println("\nTotal Fare: Rs. " + totalFare);

		// Confirm booking
		System.out.print("Confirm booking? (Y/N): ");
		String confirm = scanner.nextLine();

		if (confirm.equalsIgnoreCase("Y")) {
			Booking booking = new Booking(loggedInCustomer.getId(), selectedBus, selectedSeats, totalFare);
			if (selectedBus.bookSeats(selectedSeats, passengerNames, passengerGenders, loggedInCustomer.getId(),
					booking.getBookingId())) {
				customerBookings.computeIfAbsent(loggedInCustomer.getId(), k -> new ArrayList<>()).add(booking);
				System.out.println("Booking successful! Booking ID: " + booking.getBookingId());
			} else {
				System.out.println("Booking failed! Please check seating rules.");
			}
		} else {
			System.out.println("Booking cancelled!");
		}
	}

	// Task 7: Ticket Cancellation
	public void cancelTicket() {
		if (loggedInCustomer == null) {
			System.out.println("Please login first!");
			return;
		}

		List<Booking> bookings = customerBookings.get(loggedInCustomer.getId());
		if (bookings == null || bookings.isEmpty()) {
			System.out.println("No bookings found!");
			return;
		}

		System.out.println("\n=== Your Bookings ===");
		for (int i = 0; i < bookings.size(); i++) {
			Booking b = bookings.get(i);
			if (b.isActive()) {
				System.out.println((i + 1) + ". Booking ID: " + b.getBookingId() + ", Bus: "
						+ b.getBus().getBusDescription() + ", Seats: " + b.getSeatNumbers());
			}
		}

		System.out.print("\nSelect booking to cancel: ");
		int choice = scanner.nextInt() - 1;
		scanner.nextLine();

		Booking selectedBooking = bookings.get(choice);
		System.out.println("Seats: " + selectedBooking.getSeatNumbers());
		System.out.print("Cancel all seats? (Y/N): ");
		String cancelAll = scanner.nextLine();

		List<String> seatsToCancel;
		if (cancelAll.equalsIgnoreCase("Y")) {
			seatsToCancel = new ArrayList<>(selectedBooking.getSeatNumbers());
		} else {
			seatsToCancel = new ArrayList<>();
			System.out.print("Enter seats to cancel (comma-separated): ");
			String[] seats = scanner.nextLine().split(",");
			for (String seat : seats) {
				seatsToCancel.add(seat.trim().toUpperCase());
			}
		}

		double refund = selectedBooking.getBus().cancelSeats(seatsToCancel);
		System.out.println("Cancellation successful! Refund: Rs. " + refund);

		if (seatsToCancel.size() == selectedBooking.getSeatNumbers().size()) {
			selectedBooking.setActive(false);
		}
	}

	// Task 8: Bus Filtering
	public void filterBuses() {
		System.out.println("\n=== Filter Buses ===");
		System.out.print("AC Type (AC/NON_AC/BOTH): ");
		String acFilter = scanner.nextLine().toUpperCase();
		System.out.print("Bus Type (SEATER/SLEEPER/BOTH): ");
		String typeFilter = scanner.nextLine().toUpperCase();

		List<Bus> filteredBuses = buses.stream().filter(b -> b.getRemainingSeats() > 0)
				.filter(b -> acFilter.equals("BOTH") || b.getAcType().name().equals(acFilter))
				.filter(b -> typeFilter.equals("BOTH") || b.getBusType().name().equals(typeFilter)).sorted((b1, b2) -> {
					int capacityCompare = Integer.compare(b2.getRemainingSeats(), b1.getRemainingSeats());
					if (capacityCompare != 0)
						return capacityCompare;

					if (b1.getAcType() != b2.getAcType()) {
						return b1.getAcType() == ACType.AC ? -1 : 1;
					}

					return b1.getBusType() == BusType.SLEEPER ? -1 : 1;
				}).collect(Collectors.toList());

		System.out.println("\n=== Filtered Buses ===");
		for (Bus bus : filteredBuses) {
			System.out.println(bus.getBusDescription() + " - " + bus.getRemainingSeats() + " Seats");
		}
	}

	// Task 9: Bus Summary (Admin)
	public void showBusSummary() {
		System.out.println("\n=== BUS SUMMARY ===");
		for (Bus bus : buses) {
			int filledSeats = bus.getTotalSeats() - bus.getRemainingSeats();
			System.out.println("\n" + bus.getBusDescription());
			System.out.println("Number of Seats Filled: " + filledSeats);
			System.out.println("Total Fare Collected: Rs. " + bus.getTotalFareCollected());
			System.out.println("\nSeat Details:");
			System.out.println("Seat\tName\tGender");
			System.out.println("-----------------------------");

			for (Seat seat : bus.getSeats().values()) {
				if (seat.isOccupied()) {
					System.out.println(
							seat.getSeatNumber() + "\t" + seat.getPassengerName() + "\t" + seat.getPassengerGender());
				}
			}
		}
	}

	private void displayBusOptions() {
		System.out.println("\n=== Available Buses ===");
		for (int i = 0; i < buses.size(); i++) {
			Bus bus = buses.get(i);
			System.out.println((i + 1) + ". " + bus.getBusDescription() + " - " + bus.getRemainingSeats()
					+ " Seats (Rs. " + bus.getFarePerSeat() + "/seat)");
		}
	}

	public void run() {
		while (true) {
			System.out.println("\n=== ZOBUS - Bus Booking System ===");
			System.out.println("1. Sign Up");
			System.out.println("2. Login");
			System.out.println("3. Book Ticket");
			System.out.println("4. Cancel Ticket");
			System.out.println("5. Filter Buses");
			System.out.println("6. Bus Summary (Admin)");
			System.out.println("7. Exit");
			System.out.print("Choose option: ");

			int choice = scanner.nextInt();
			scanner.nextLine();

			switch (choice) {
			case 1:
				signUp();
				break;
			case 2:
				login();
				break;
			case 3:
				bookTicket();
				break;
			case 4:
				cancelTicket();
				break;
			case 5:
				filterBuses();
				break;
			case 6:
				showBusSummary();
				break;
			case 7:
				System.out.println("Thank you!");
				return;
			default:
				System.out.println("Invalid option!");
			}
		}
	}

	public static void main(String[] args) {
		ZOBUSApplication app = new ZOBUSApplication();
		app.run();
	}
}