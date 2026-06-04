// com.zobus.model.Seat.java
package zobus.model;

public class Seat {
	private String id; // e.g., "1A", "Lower1A"
	private String occupantName;
	private String occupantGender; // null if vacant
	private Booking booking; // null if vacant

	public Seat(String id) {
		this.id = id;
		this.occupantName = null;
		this.occupantGender = null;
		this.booking = null;
	}

	// Getters and setters
	public String getId() {
		return id;
	}

	public String getOccupantName() {
		return occupantName;
	}

	public void setOccupantName(String occupantName) {
		this.occupantName = occupantName;
	}

	public boolean isVacant() {
		return booking == null;
	}

	public String getOccupantGender() {
		return occupantGender;
	}

	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}

	public void assign(String name, String gender, Booking booking) {
		this.occupantName = name;
		this.occupantGender = gender;
		this.booking = booking;
	}

	public void release() {
		this.occupantName = null;
		this.occupantGender = null;
		this.booking = null;
	}
}