import java.util.ArrayList;

public class BusSchedules {
    public static ArrayList<Bus> matchedBuses = new ArrayList<>();


    private static final String compoundBusStation = "Madienty";
    private String BusNum;
    private static String busStation = compoundBusStation;
    private String BusPlate;
    private String ArrivingTime;
    private String departureTime;
    private String route;
    private String busId;
    private ArrayList<BusTrips> trips;
    private ArrayList<String> stops;

    public BusSchedules(String busId) {
        this.busId = busId;
        setTrips(new ArrayList<>());
        setStops(new ArrayList<>());
    }

    public String getBusId() {
        return busId;
    }



    public BusSchedules(String busNum, String busPlate, String arrivingTime, String departureTime) {

        this.BusNum = busNum;
        this.BusPlate = busPlate;
        this.ArrivingTime = arrivingTime;
        this.departureTime = departureTime;
    }
    public String getBusNum() {
        return BusNum;
    }
    public void setBusNum(String busNum) {
        BusNum = busNum;
    }
    public String getBusPlate() {
        return BusPlate;
    }
    public void setBusPlate(String busPlate) {
        BusPlate = busPlate;
    }
    public String getArrivingTime() {
        return ArrivingTime;
    }
    public void setArrivingTime(String arrivingTime) {
        ArrivingTime = arrivingTime;
    }
    public String getDepartureTime() {
        return departureTime;
    }
    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    private ArrayList<Bus> buses = new ArrayList<>();

    public BusSchedules() {
        ArrayList<Bus> loaded = DataService.loadBuses();
        if (loaded != null && !loaded.isEmpty()) {
            this.buses = loaded;
        } else {
            initializeDefaultBuses();
        }
    }

    private void initializeDefaultBuses() {
        Bus b1 = new Bus("B1");
        b1.addStop("Downtown");
        b1.addStop("Airport");
        b1.addStop("University");
        b1.addTrip(new BusTrips("Downtown", "08:00", "08:20"));
        b1.addTrip(new BusTrips("Airport", "09:00", "09:45"));
        b1.addTrip(new BusTrips("University", "10:00", "10:30"));

        Bus b2 = new Bus("B2");
        b2.addStop("Mall");
        b2.addStop("Downtown");
        b2.addTrip(new BusTrips("Mall", "11:00", "11:25"));
        b2.addTrip(new BusTrips("Downtown", "12:00", "12:20"));

        Bus b3 = new Bus("B3");
        b3.addStop("Airport");
        b3.addStop("Hospital");
        b3.addTrip(new BusTrips("Airport", "13:00", "13:40"));

        buses.add(b1);
        buses.add(b2);
        buses.add(b3);
    }

    public void saveBuses() {
        DataService.saveBuses(buses);
    }
    public boolean searchByDestination(String destination) {
        boolean anyFound = false;

        matchedBuses.clear();

        for (Bus b : buses) {
            if (b.passesThrough(destination)) {
                b.displayTripsForDestination(destination);
                matchedBuses.add(b);
                anyFound = true;
            }
        }

        if (!anyFound) {
            System.out.println("No buses pass through: " + destination);
        }

        return anyFound;
    }



    @Override
    public String toString() {
        return "BusSchedules [BusNum=" + BusNum + ", BusPlate=" + BusPlate + ", ArrivingTime=" + ArrivingTime
                + ", departureTime=" + departureTime + "]";
    }
    public static String getBusStation() {
        return busStation;
    }
    public static void setBusStation(String busStation) {
        BusSchedules.busStation = busStation;
    }
    public String getRoute() {
        return route;
    }
    public void setRoute(String route) {
        this.route = route;
    }

    public ArrayList<String> getStops() {
        return stops;
    }

    public void setStops(ArrayList<String> stops) {
        this.stops = stops;
    }

    public ArrayList<BusTrips> getTrips() {
        return trips;
    }

    public void setTrips(ArrayList<BusTrips> trips) {
        this.trips = trips;
    }



}
