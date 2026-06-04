// com.zobus.model.Customer.java
package zobus.model;

public class Customer {
	private int id;
	private String name;
	private String password;
	private int age;
	private String gender; // "M" or "F"
	private Booking booking; // One booking per customer

	public Customer(int id, String name, String password, int age, String gender) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.age = age;
		this.gender = gender;
	}

	// Getters and setters
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public String getGender() {
		return gender;
	}

	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}
}