// com.zobus.model.Booking.java
package zobus.model;

import java.util.*;

public class Booking {
	private int id;
	private Customer customer;
	private Bus bus;
	private List<Seat> seats;
	private List<String> travelerNames; // Co-travelers
	private List<String> travelerGenders;
	private double totalFare;
	private boolean confirmed;
	private double cancellationFee;

	public Booking(int id, Customer customer, Bus bus, List<Seat> seats, List<String> travelerNames,
			List<String> travelerGenders) {
		this.id = id;
		this.customer = customer;
		this.bus = bus;
		this.seats = seats;
		this.travelerNames = travelerNames;
		this.travelerGenders = travelerGenders;
		this.totalFare = seats.size() * bus.getFarePerSeat();
		this.confirmed = false;
		this.cancellationFee = 0;
	}

	// Assign seats and names/genders
	public void assignSeats() {
		for (int i = 0; i < seats.size(); i++) {
			seats.get(i).assign(travelerNames.get(i), travelerGenders.get(i), this);
		}
		confirmed = true;
	}

	// Cancel specific seats
	public double cancelSeats(List<Seat> toCancel) {
		double fee = 0;
		boolean isAC = bus.getType() == Bus.Type.AC_SEATER || bus.getType() == Bus.Type.AC_SLEEPER;
		double feeRate = isAC ? 0.5 : 0.25;
		for (Seat seat : toCancel) {
			if (seats.contains(seat)) {
				fee += (bus.getFarePerSeat() * feeRate);
				seat.release();
				seats.remove(seat);
			}
		}
		totalFare = seats.size() * bus.getFarePerSeat(); // Update fare
		cancellationFee = fee;
		return fee;
	}

	// Getters
	public Customer getCustomer() {
		return customer;
	}

	public Bus getBus() {
		return bus;
	}

	public List<Seat> getSeats() {
		return seats;
	}

	public double getTotalFare() {
		return totalFare;
	}

	public double getCancellationFee() {
		return cancellationFee;
	}

	public boolean isConfirmed() {
		return confirmed;
	}
}