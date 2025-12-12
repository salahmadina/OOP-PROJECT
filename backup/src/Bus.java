

import java.util.ArrayList;

public class Bus {

    private String BusId;
    private static int BusCapacity=45;
    private static int BusAvaliableSeats=45;
    public static double distanceKm = 10.0;
    public static double priceperKm = 2.5;


    public String getBusId() {
        return busId;
    }

    public String getBusNum() {
        return BusId;
    }
    public static int getBusCapacity() {
        return BusCapacity;
    }

    public static void setBusCapacity(int busCapacity) {
        BusCapacity = busCapacity;
    }

    public static int getBusAvaliableSeats() {
        return BusAvaliableSeats;
    }

    public void setBusAvaliableSeats(int busAvaliableSeats) {
        BusAvaliableSeats = busAvaliableSeats;
    }

    private String busId;
    private ArrayList<BusTrips> trips;
    private ArrayList<String> stops;

    public Bus(String busId) {
        this.busId = busId;
        trips = new ArrayList<>();
        stops = new ArrayList<>();
    }

    public void addStop(String stop) {
        stops.add(stop);
    }

    public void addTrip(BusTrips trip) {
        trips.add(trip);
    }

    public boolean passesThrough(String destination) {
        return stops.contains(destination);
    }


    public void displayTripsForDestination(String destination) {
        if (!passesThrough(destination)) {
            System.out.println("This bus doesn't pass the destination");

        }
        boolean found = false;

        for (BusTrips t : trips) {
            if (t.getRoute() != null && t.getRoute().equalsIgnoreCase(destination)) {
                System.out.println("Bus " + busId + " → " + t);
                found = true;
            }
        }

        if (!found) {
            System.out.println("Bus " + busId + " passes through " + destination);
        }
    }
    public double getDistanceKm() {
        return distanceKm;
    }

    public double getPricePerKm() {
        return priceperKm;
    }

    public boolean bookSeats(int seatsToBook) {

        if (seatsToBook <= 0) {
            System.out.println(" Invalid number of seats.");
            return false;
        }

        if (seatsToBook > BusAvaliableSeats) {
            System.out.println(" Not enough seats available.");
            return false;
        }

        BusAvaliableSeats -= seatsToBook;
        System.out.println("Booking successful! Seats left: " + BusAvaliableSeats);
        return true;
    }

}
