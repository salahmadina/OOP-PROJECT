import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * DataService: Helper class for reading and writing ArrayLists to TEXT files.
 * Each ArrayList is persisted to its own .txt file for data persistence across sessions.
 * Data files are stored in the 'data' folder relative to the project root.
 */
public class DataService {

    // File paths for data storage (text files in 'data' directory)
    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + "/users.txt";
    private static final String BUSES_FILE = DATA_DIR + "/buses.txt";
    private static final String GATES_FILE = DATA_DIR + "/gates.txt";
    private static final String QR_CODES_FILE = DATA_DIR + "/qrcodes.txt";
    private static final String LOGS_FILE = DATA_DIR + "/logs.txt";
    private static final String REPORTS_FILE = DATA_DIR + "/reports.txt";

    // Static initializer to create data directory if it doesn't exist
    static {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    /**
     * Save users list to TEXT file (from SignUp)
     * Format: password|username|name|age|nationalID|phonenumber|email|role|dependants|type|apartmentNumber|buildingNumber
     */
    public static void saveUsers(ArrayList<person> users) {
        try (FileWriter fw = new FileWriter(USERS_FILE)) {
            for (person p : users) {
                if (p instanceof Resident) {
                    Resident r = (Resident) p;
                    fw.write(r.getPassword() + "|" + r.getUsername() + "|" + r.getName() + "|" 
                        + r.getAge() + "|" + r.getNationalID() + "|" + r.getPhonenumber() + "|" 
                        + r.getEmail() + "|" + r.getRole() + "|" + r.getDependants() + "|"
                        + "RESIDENT" + "|" + r.getApartmentNumber2() + "|" + r.getbuildingNumber() + "\n");
                } else if (p instanceof Staff) {
                    Staff s = (Staff) p;
                    fw.write(s.getPassword() + "|" + s.getUsername() + "|" + s.getName() + "|" 
                        + s.getAge() + "|" + s.getNationalID() + "|" + s.getPhonenumber() + "|" 
                        + s.getEmail() + "|" + s.getRole() + "|" + s.getDependants() + "|"
                        + "STAFF|0|0\n");
                }
            }
            System.out.println("[DataService] Users saved to " + USERS_FILE);
        } catch (IOException e) {
            System.out.println("[DataService] Error saving users: " + e.getMessage());
        }
    }

    /**
     * Load users list from TEXT file (for SignUp)
     */
    public static ArrayList<person> loadUsers() {
        ArrayList<person> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        
        if (!file.exists()) {
            System.out.println("[DataService] No users file found. Starting fresh.");
            return users;
        }
        
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                
                String[] parts = line.split("\\|");
                if (parts.length < 9) continue;
                
                String password = parts[0];
                String username = parts[1];
                String name = parts[2];
                int age = Integer.parseInt(parts[3]);
                String nationalID = parts[4];
                String phone = parts[5];
                String email = parts[6];
                String role = parts[7];
                String dependants = parts[8];
                String type = parts[9];
                
                if ("RESIDENT".equals(type) && parts.length >= 12) {
                    int apt = Integer.parseInt(parts[10]);
                    int building = Integer.parseInt(parts[11]);
                    users.add(new Resident(password, username, name, age, nationalID, phone, email, role, dependants, apt, building));
                } else if ("STAFF".equals(type)) {
                    users.add(new Staff(password, username, name, age, nationalID, phone, email, role, dependants));
                }
            }
            System.out.println("[DataService] Users loaded from " + USERS_FILE + " (" + users.size() + " users)");
        } catch (IOException e) {
            System.out.println("[DataService] Error loading users: " + e.getMessage());
        }
        
        return users;
    }

    /**
     * Save buses list to TEXT file (from BusSchedules)
     * Format: busId|stop1,stop2,stop3|trip1(route-departure-arrival),trip2,...
     */
    public static void saveBuses(ArrayList<Bus> buses) {
        try (FileWriter fw = new FileWriter(BUSES_FILE)) {
            // Just save a marker that buses are initialized; actual bus data is managed in BusSchedules
            fw.write("# Bus Schedule Data File\n");
            fw.write("# Format: busId|stops|trips\n");
            System.out.println("[DataService] Buses state saved to " + BUSES_FILE);
        } catch (IOException e) {
            System.out.println("[DataService] Error saving buses: " + e.getMessage());
        }
    }

    /**
     * Load buses list from TEXT file (for BusSchedules)
     */
    public static ArrayList<Bus> loadBuses() {
        // Buses are managed internally in BusSchedules, return null to use defaults
        File file = new File(BUSES_FILE);
        if (file.exists()) {
            System.out.println("[DataService] Buses file found. Using existing configuration.");
            return new ArrayList<>();
        }
        return null;
    }

    /**
     * Save gates list to TEXT file (from QRGateSystem)
     * Format: gateId|location|isOpen
     */
    public static void saveGates(ArrayList<Gate> gates) {
        try (FileWriter fw = new FileWriter(GATES_FILE)) {
            fw.write("# Gate Data File\n");
            fw.write("# Format: gateId|location|isOpen\n");
            for (Gate g : gates) {
                fw.write(g.getGateId() + "|" + g.getLocation() + "|" + g.isOpen() + "\n");
            }
            System.out.println("[DataService] Gates saved to " + GATES_FILE);
        } catch (IOException e) {
            System.out.println("[DataService] Error saving gates: " + e.getMessage());
        }
    }

