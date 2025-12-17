import java.util.Scanner;
import java.util.ArrayList;

public class main {

    private static Scanner scanner = new Scanner(System.in);
    private static SignUp signUpSystem = new SignUp(); // Manages the list of users
    private static BusSchedules busScheduleSystem = new BusSchedules(); // Initializes the bus system

    // --- NEW: QR Gate System Integration ---
    private static QRGateSystem qrGateSystem = new QRGateSystem();
    private static ArrayList<Gate> compoundGates = new ArrayList<>();

    public static void main(String[] args) {
        // Initialize Gates
        Gate mainGate = new Gate(1, "Main Entrance");
        Gate backGate = new Gate(2, "Back Entrance");

        // Add to local list for interaction and to system
        compoundGates.add(mainGate);
        compoundGates.add(backGate);
        qrGateSystem.addGate(mainGate);
        qrGateSystem.addGate(backGate);

        // No hardcoded data (seedData removed).
        // User must manually Sign Up to create accounts.

        boolean running = true;
        while (running) {
            System.out.println("\n=======================================");
            System.out.println("      COMPOUND MANAGEMENT SYSTEM       ");
            System.out.println("=======================================");
            System.out.println("1. Login");
            System.out.println("2. Sign Up");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    handleLogin();
                    break;
                case "2":
                    signUpSystem.createAccount();
                    break;
                case "3":
                    System.out.println("Exiting system. Saving data...");
                    signUpSystem.saveUsers(); // Save users before exit
                    busScheduleSystem.saveBuses(); // Save buses before exit
                    qrGateSystem.saveAllData(); // Save QR and logs before exit
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // --------------------------------------------------------
    // LOGIN LOGIC
    // --------------------------------------------------------
    private static void handleLogin() {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        // 1. Find the user in the SignUp system list
        person foundPerson = null;
        for (person p : signUpSystem.getUsers()) {
            if (p.getUsername().equals(username)) {
                foundPerson = p;
                break;
            }
        }

        // 2. Validate Credentials using loginclass
        if (foundPerson != null) {
            loginclass login = new loginclass(foundPerson);

            // validateLogin handles the logic and checking password
            boolean isAuthenticated = login.validateLogin(username, password);

            if (isAuthenticated) {
                // 3. Route to appropriate Menu based on Role
                if (foundPerson instanceof Resident) {
                    residentMenu((Resident) foundPerson);
                } else if (foundPerson instanceof Staff) {
                    staffMenu((Staff) foundPerson);
                } else {
                    System.out.println("Error: Unknown role type.");
                }
            }
        } else {
            System.out.println("User not found. Please Sign Up first.");
        }
    }

    // --------------------------------------------------------
    // RESIDENT MENU & FEATURES
    // --------------------------------------------------------
    private static void residentMenu(Resident resident) {
        boolean back = false;
        while (!back) {
            System.out.println("\n=======================================");
            System.out.println("        RESIDENT MENU (" + resident.getName() + ")");
            System.out.println("=======================================");
            System.out.println("1. Transportation (Search & Book Bus)");
            System.out.println("2. Request House Service");
            System.out.println("3. Report an Issue");
            System.out.println("4. View Profile / ID");
            System.out.println("5. Gate Access Control"); // NEW OPTION
            System.out.println("6. Logout");
            System.out.print("Choose: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    handleBusServices();
                    break;
                case "2":
                    handleServiceRequest(resident);
                    break;
                case "3":
                    handleReporting(resident);
                    break;
                case "4":
                    System.out.println("\n--- Profile Info ---");
                    System.out.println(resident.toString());
                    resident.verifyAge();
                    resident.validateNationalID();
                    break;
                case "5":
                    handleGateAccess(resident); // NEW METHOD
                    break;
                case "6":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void handleBusServices() {
        System.out.println("\n--- Bus Services ---");
        System.out.print("Enter Destination to search (e.g., Downtown, Airport, Mall): ");
        String destination = scanner.nextLine();

        // Use BusSchedules to search
        boolean found = busScheduleSystem.searchByDestination(destination);

        if (found && !BusSchedules.matchedBuses.isEmpty()) {
            System.out.println("\nSelect a bus to book (Enter index, 1 for first bus):");
            int i = 1;
            for (Bus b : BusSchedules.matchedBuses) {
                System.out.println(i + ". Bus ID: " + b.getBusId() + " (Fare: " + (b.getDistanceKm() * b.getPricePerKm()) + ")");
                i++;
            }

            try {
                String input = scanner.nextLine();
                int selection = Integer.parseInt(input);

                if (selection > 0 && selection <= BusSchedules.matchedBuses.size()) {
                    Bus selectedBus = BusSchedules.matchedBuses.get(selection - 1);
                    System.out.print("Enter number of seats to book: ");
                    int seats = Integer.parseInt(scanner.nextLine());

                    selectedBus.bookSeats(seats);
                    busScheduleSystem.saveBuses(); // Save after booking
                } else {
                    System.out.println("Invalid selection.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }

    private static void handleServiceRequest(Resident resident) {
        System.out.println("\n--- New Service Request ---");
        System.out.print("Enter Service Type (e.g., Plumbing, Electric): ");
        String service = scanner.nextLine();
        System.out.print("Enter Details: ");
        String details = scanner.nextLine();

        request newRequest = new request(resident, service, details);
        newRequest.ShowTicket();
    }

    private static void handleReporting(Resident resident) {
        System.out.println("\n--- File a Report ---");
        System.out.print("Report Type (e.g., Noise, Maintenance): ");
        String type = scanner.nextLine();
        System.out.print("Description: ");
        String desc = scanner.nextLine();

        Report report = new Report(resident, type, desc);
        System.out.println("Report created successfully! ID: " + report.getReportId());

        // AUTOMATICALLY ASSIGN TO A STAFF MEMBER (Logic to connect Report to Staff)
        // We search through the user list for the first available Staff member
        boolean assigned = false;
        for (person p : signUpSystem.getUsers()) {
            if (p instanceof Staff) {
                Staff s = (Staff) p;
                s.assignReport(report);
                System.out.println(">> System: Report assigned to Staff member: " + s.getName());
                assigned = true;
                break; // Assign to the first found staff
            }
        }

        if (!assigned) {
            System.out.println(">> System: No staff accounts found to assign this report to.");
        }
        
        signUpSystem.saveUsers(); // Save to persist staff reports
    }

    // --- NEW METHOD: Handles Gate Logic ---
    private static void handleGateAccess(Resident resident) {
        System.out.println("\n--- Gate Access Control ---");
        // Using phone number as a unique int ID for QR generation
        String userId = resident.getPhonenumber();

        System.out.println("1. Generate Entry QR Code");
        System.out.println("2. Go Back");
        System.out.print("Choose: ");
        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            // Assign/Create QR
            QRCode myQR = qrGateSystem.assignQRCodeToUser(userId);
            System.out.println("QR Code Generated: " + myQR.getCode());

            // Choose Gate
            System.out.println("\nSelect Gate to Enter:");
            for (Gate g : compoundGates) {
                System.out.println(g.getGateId() + ". " + g.getLocation());
            }
            System.out.print("Enter Gate ID: ");
            try {
                int gateId = Integer.parseInt(scanner.nextLine());
                Gate selectedGate = null;
                for(Gate g : compoundGates) {
                    if(g.getGateId() == gateId) {
                        selectedGate = g;
                        break;
                    }
                }

                if(selectedGate != null) {
                    System.out.println("Scanning QR Code at " + selectedGate.getLocation() + "...");
                    boolean accessGranted = selectedGate.scanQRCode(myQR);
                    if(accessGranted) {
                        qrGateSystem.logEntry(userId);
                        System.out.println("Welcome Home, " + resident.getName() + "!");
                    } else {
                        System.out.println("Access Denied.");
                    }
                } else {
                    System.out.println("Invalid Gate ID.");
                }

            } catch (Exception e) {
                System.out.println("Invalid input.");
            }
        }
    }

    // --------------------------------------------------------
    // STAFF MENU & FEATURES
    // --------------------------------------------------------
    private static void staffMenu(Staff staff) {
        boolean back = false;
        while (!back) {
            System.out.println("\n=======================================");
            System.out.println("          STAFF MENU (" + staff.getName() + ")");
            System.out.println("=======================================");
            System.out.println("1. View All Residents");
            System.out.println("2. Remove a Resident");
            System.out.println("3. Manage My Assigned Reports");
            System.out.println("4. View Gate Logs"); // NEW OPTION
            System.out.println("5. View Profile");
            System.out.println("6. Logout");
            System.out.print("Choose: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    staff.viewAllResidents(signUpSystem.getUsers());
                    break;
                case "2":
                    staff.removeResidentByChoosing(signUpSystem.getUsers());
                    break;
                case "3":
                    manageReports(staff);
                    break;
                case "4":
                    qrGateSystem.showLogs(); // NEW CALL
                    break;
                case "5":
                    System.out.println(staff.toString());
                    break;
                case "6":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void manageReports(Staff staff) {
        ArrayList<Report> reports = staff.getAssignedReports();

        if (reports.isEmpty()) {
            System.out.println("\nNo reports assigned to you.");
            return;
        }

        System.out.println("\n--- Your Assigned Reports ---");
        int index = 1;
        for (Report r : reports) {
            System.out.println(index + ". " + r.getType() + " by " + r.getCitizenName() + " [" + r.getStatus() + "]");
            index++;
        }

        System.out.println("Enter number to view/update details (or 0 to go back):");
        try {
            int selection = Integer.parseInt(scanner.nextLine());
            if (selection > 0 && selection <= reports.size()) {
                Report selectedReport = reports.get(selection - 1);
                staff.readReport(selectedReport);

                System.out.print("Do you want to update status? (yes/no): ");
                if (scanner.nextLine().equalsIgnoreCase("yes")) {
                    System.out.print("Enter new status (e.g., In Progress, Solved): ");
                    String newStatus = scanner.nextLine();
                    staff.updateReportStatus(selectedReport, newStatus);
                    System.out.println("Status updated.");
                }
            }
        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }
}