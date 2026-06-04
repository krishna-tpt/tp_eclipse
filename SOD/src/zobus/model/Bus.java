// com.zobus.model.Bus.java (Updated getRemainingSeats and canAssignSeats - no streams)
package zobus.model;

import java.util.*;

public class Bus {
    public enum Type { AC_SLEEPER, AC_SEATER, NON_AC_SLEEPER, NON_AC_SEATER }
    private Type type;
    private List<Seat> seats;
    private double farePerSeat;

    public Bus(Type type, List<String> seatIds, double farePerSeat) {
        this.type = type;
        this.seats = new ArrayList<>();
        for (String id : seatIds) {
            seats.add(new Seat(id));
        }
        this.farePerSeat = farePerSeat;
    }

    public Type getType() { return type; }
    public List<Seat> getSeats() { return seats; }
    
    // Updated: No streams
    public int getRemainingSeats() {
        int count = 0;
        for (Seat seat : seats) {
            if (seat.isVacant()) {
                count++;
            }
        }
        return count;
    }
    
    public double getFarePerSeat() { return farePerSeat; }
    public boolean isFull() { return getRemainingSeats() == 0; }

    // Display seat layout (unchanged)
    public void displaySeats() {
        Map<String, List<Seat>> rows = new TreeMap<>();
        for (Seat seat : seats) {
            String row = seat.getId().split("[A-C]")[0]; // e.g., "1", "Lower1"
            List<Seat> rowSeats = rows.get(row);
            if (rowSeats == null) {
                rowSeats = new ArrayList<>();
                rows.put(row, rowSeats);
            }
            rowSeats.add(seat);
        }
        for (Map.Entry<String, List<Seat>> entry : rows.entrySet()) {
            System.out.println("Row " + entry.getKey() + ": ");
            for (Seat seat : entry.getValue()) {
                String status = seat.isVacant() ? "   " : (seat.getOccupantGender().equals("F") ? " F " : " M ");
                System.out.print("|" + status + "| ");
            }
            System.out.println();
        }
    }

    // Updated canAssignSeats: Loop-based adjacency check (unchanged logic)
    public boolean canAssignSeats(List<Seat> selectedSeats, List<String> travelerGenders, Booking booking) {
        for (int i = 0; i < selectedSeats.size() - 1; i++) {
            Seat s1 = selectedSeats.get(i);
            Seat s2 = selectedSeats.get(i + 1);
            String row1 = s1.getId().split("[A-C]")[0];
            String row2 = s2.getId().split("[A-C]")[0];
            if (!row1.equals(row2)) continue; // Not adjacent if different rows
            
            // Check s1
            if (s1.getBooking() != null && s1.getBooking() != booking) {
                if ("F".equals(s1.getOccupantGender()) && !"F".equals(travelerGenders.get(i))) {
                    return false;
                }
            }
            // Check s2
            if (s2.getBooking() != null && s2.getBooking() != booking) {
                if ("F".equals(s2.getOccupantGender()) && !"F".equals(travelerGenders.get(i + 1))) {
                    return false;
                }
            }
        }
        return true;
    }
}

// Other models (Customer, Seat, Booking) unchanged.