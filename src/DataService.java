import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class DataService {

    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + "/users.txt";
    private static final String BUSES_FILE = DATA_DIR + "/buses.txt";
    private static final String GATES_FILE = DATA_DIR + "/gates.txt";
    private static final String QR_CODES_FILE = DATA_DIR + "/qrcodes.txt";
    private static final String LOGS_FILE = DATA_DIR + "/logs.txt";
    private static final String REPORTS_FILE = DATA_DIR + "/reports.txt";

    

    public static void saveUsers(ArrayList<person> users) {
        try (FileWriter fw = new FileWriter(USERS_FILE)) {
            for (person p : users) {
                if (p instanceof Resident) {
                    Resident r = (Resident) p;
                    fw.write(r.getPassword() + "|" + r.getUsername() + "|" + r.getName() + "|" 
                        + r.getAge() + "|" + r.getNationalID() + "|" + r.getPhonenumber() + "|" 
                        + r.getEmail() + "|" + r.getRole() + "|" + r.getDependants() + "|"
                         + r.getApartmentNumber2() + "|" + r.getbuildingNumber() + "\n");
                } else if (p instanceof Staff) {
                    Staff s = (Staff) p;
                    fw.write(s.getPassword() + "|" + s.getUsername() + "|" + s.getName() + "|" 
                        + s.getAge() + "|" + s.getNationalID() + "|" + s.getPhonenumber() + "|" 
                        + s.getEmail() + "|" + s.getRole() + "|" + s.getDependants() + "|"
                        + "0|0\n");
                }
            }
            System.out.println("[DataService] Users saved to " + USERS_FILE);
        } catch (IOException e) {
            System.out.println("[DataService] Error saving users: " + e.getMessage());
        }
    }

   
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
                if (parts.length < 8) continue;
                
                String password = parts[0];
                String username = parts[1];
                String name = parts[2];
                int age = Integer.parseInt(parts[3]);
                String nationalID = parts[4];
                String phone = parts[5];
                String email = parts[6];
                String role = parts[7];
                String dependants = parts[8];
                
                if ("Resident".equals(role) && parts.length == 11) {
                    int apt = Integer.parseInt(parts[9]);
                    int building = Integer.parseInt(parts[10]);
                    users.add(new Resident(password, username, name, age, nationalID, phone, email, role, dependants, apt, building));
                } else if ("Staff".equals(role)) {
                    users.add(new Staff(password, username, name, age, nationalID, phone, email, role, dependants));
                }
            }
            System.out.println("[DataService] Users loaded from " + USERS_FILE + " (" + users.size() + " users)");
        } catch (IOException e) {
            System.out.println("[DataService] Error loading users: " + e.getMessage());
        }
        
        return users;
    }

    public static void saveBuses(ArrayList<Bus> buses) {
        try (FileWriter fw = new FileWriter(BUSES_FILE)) {
            fw.write("# Bus Schedule Data File\n");
            fw.write("# Format: busId|stops|trips\n");
            System.out.println("[DataService] Buses state saved to " + BUSES_FILE);
        } catch (IOException e) {
            System.out.println("[DataService] Error saving buses: " + e.getMessage());
        }
    }

    public static ArrayList<Bus> loadBuses() {
        File file = new File(BUSES_FILE);
        if (file.exists()) {
            System.out.println("[DataService] Buses file found. Using existing configuration.");
            return new ArrayList<>();
        }
        return null;
    }

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

    public static ArrayList<Gate> loadGates() {
        ArrayList<Gate> gates = new ArrayList<>();
        File file = new File(GATES_FILE);
        
        if (!file.exists()) {
            System.out.println("[DataService] No gates file found. Starting fresh.");
            return gates;
        }
        
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                
                String[] parts = line.split("\\|");
                if (parts.length < 3) continue;
                
                int gateId = Integer.parseInt(parts[0]);
                String location = parts[1];
                boolean isOpen = Boolean.parseBoolean(parts[2]);
                
                Gate g = new Gate(gateId, location);
                if (isOpen) g.openGate();
                gates.add(g);
            }
            System.out.println("[DataService] Gates loaded from " + GATES_FILE + " (" + gates.size() + " gates)");
        } catch (IOException e) {
            System.out.println("[DataService] Error loading gates: " + e.getMessage());
        }
        
        return gates;
    }

    public static void saveQRCodes(ArrayList<QRCode> qrCodes) {
        try (FileWriter fw = new FileWriter(QR_CODES_FILE)) {
            fw.write("# QR Codes Data File\n");
            fw.write("# Format: userId|code\n");
            for (QRCode qr : qrCodes) {
                fw.write(qr.getUserId() + "|" + qr.getCode() + "\n");
            }
            System.out.println("[DataService] QR Codes saved to " + QR_CODES_FILE);
        } catch (IOException e) {
            System.out.println("[DataService] Error saving QR codes: " + e.getMessage());
        }
    }

    public static ArrayList<QRCode> loadQRCodes() {
        ArrayList<QRCode> qrCodes = new ArrayList<>();
        File file = new File(QR_CODES_FILE);
        
        if (!file.exists()) {
            System.out.println("[DataService] No QR codes file found. Starting fresh.");
            return qrCodes;
        }
        
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                
                String[] parts = line.split("\\|");
                if (parts.length < 2) continue;
                
                String userId = parts[0];
                String code = parts[1];
                
                QRCode qr = new QRCode(userId);
                qrCodes.add(qr);
            }
            System.out.println("[DataService] QR Codes loaded from " + QR_CODES_FILE + " (" + qrCodes.size() + " codes)");
        } catch (IOException e) {
            System.out.println("[DataService] Error loading QR codes: " + e.getMessage());
        }
        
        return qrCodes;
    }

    public static void saveLogs(ArrayList<String> logs) {
        try (FileWriter fw = new FileWriter(LOGS_FILE)) {
            fw.write("# Gate Access Logs\n");
            fw.write("# Format: timestamp/userId/action\n");
            for (String log : logs) {
                fw.write(log + "\n");
            }
            System.out.println("[DataService] Logs saved to " + LOGS_FILE);
        } catch (IOException e) {
            System.out.println("[DataService] Error saving logs: " + e.getMessage());
        }
    }

    public static ArrayList<String> loadLogs() {
        ArrayList<String> logs = new ArrayList<>();
        File file = new File(LOGS_FILE);
        
        if (!file.exists()) {
            System.out.println("[DataService] No logs file found. Starting fresh.");
            return logs;
        }
        
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                logs.add(line);
            }
            System.out.println("[DataService] Logs loaded from " + LOGS_FILE + " (" + logs.size() + " entries)");
        } catch (IOException e) {
            System.out.println("[DataService] Error loading logs: " + e.getMessage());
        }
        
        return logs;
    }

    public static void saveAllReports(ArrayList<Staff> staffMembers) {
        try (FileWriter fw = new FileWriter(REPORTS_FILE)) {
            fw.write("# Reports Data File\n");
            fw.write("# Format: reportId|citizenName|type|description|dateTime|status\n");
           for (Staff s : staffMembers) {
                for (Report r : s.getAssignedReports()) {
                    fw.write(r.getReportId() + "|" + r.getCitizenName() + "|" + r.getType() + "|" 
                        + r.getDescription() + "|" + r.getDateTime() + "|" + r.getStatus() + "\n");
                }
            }       
                 System.out.println("[DataService] Reports saved to " + REPORTS_FILE);
        } catch (IOException e) {
            System.out.println("[DataService] Error saving reports: " + e.getMessage());
        }
    }

    public static ArrayList<Report> loadReports() {
        ArrayList<Report> reports = new ArrayList<>();
        File file = new File(REPORTS_FILE);
        
        if (!file.exists()) {
            System.out.println("[DataService] No reports file found. Starting fresh.");
            return reports;
        }
        
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
               
            }
            System.out.println("[DataService] Reports loaded from " + REPORTS_FILE + " (" + reports.size() + " reports)");
        } catch (IOException e) {
            System.out.println("[DataService] Error loading reports: " + e.getMessage());
        }
        
        return reports;
    }

    public static void clearAllData() {
        File[] files = {
            new File(USERS_FILE),
            new File(BUSES_FILE),
            new File(GATES_FILE),
            new File(QR_CODES_FILE),
            new File(LOGS_FILE),
            new File(REPORTS_FILE)
        };
        for (File f : files) {
            if (f.exists() && f.delete()) {
                System.out.println("[DataService] Deleted " + f.getName());
            }
        }
    }
}
