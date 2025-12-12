

public class BusTrips{
    private String route;
    private String departureTime;
    private String arrivalTime;

    public BusTrips(String route, String departureTime, String arrivalTime) {
        this.route = route;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }
    public String getRoute() {
        return route;
    }

    @Override
    public String toString() {
        return "Route: " + route +
                " | Departure: " + departureTime +
                " | Arrival: " + arrivalTime;
    }

}
