import java.util.ArrayList;
import java.util.Scanner;

public class Staff extends person implements info 
{
   

    private ArrayList<Report> assignedReports;

    public Staff(String password, String username,
                 String name, int age, String nationalID, String phone, String email,
                 String role, String dependants) {

        super(password, username, name, age, nationalID, phone, email, role, dependants);
        this.assignedReports = new ArrayList<>();
    }

    public ArrayList<Report> getAssignedReports() {
        return assignedReports;
    }

    public void assignReport(Report report) {
        assignedReports.add(report);
    }

    public void readReport(Report report) {
        System.out.println("----- Report -----");
        System.out.println("Citizen: " + report.getCitizenName());
        System.out.println("Type: " + report.getType());
        System.out.println("Description: " + report.getDescription());
        System.out.println("Date: " + report.getDateTime());
        System.out.println("Status: " + report.getStatus());
        System.out.println("-----------------");
    }

    public void updateReportStatus(Report report, String status) {
        report.setStatus(status);
    }



    public void viewAllResidents(ArrayList<person> users) {
        System.out.println("---- All Residents ----");
        for (person p : users) {
            if (p instanceof Resident) {
                System.out.println("Name: " + p.getName());
                System.out.println("Username: " + p.getUsername());
                System.out.println("Email: " + p.getEmail());
                System.out.println("Phone: " + p.getPhonenumber());
                System.out.println("---------------------");
            }
        }
    }

    public void removeResidentByChoosing(ArrayList<person> users) {
        Scanner scanner = new Scanner(System.in);


        System.out.println("----- Residents List -----");
        ArrayList<Resident> residents = new ArrayList<>();
        int index = 1;

        for (person p : users) {
            if (p instanceof Resident) {
                residents.add((Resident) p);
                System.out.println(index + ". " + p.getName() + " (Username: " + p.getUsername() + ")");
                index++;
            }
        }

        if (residents.isEmpty()) {
            System.out.println("No residents found!");
            return;
        }

        System.out.print("Enter the number of the resident to remove: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > residents.size()) {
            System.out.println("Invalid choice!");
            return;
        }

        Resident toRemove = residents.get(choice - 1);
        users.remove(toRemove);

        System.out.println("Resident " + toRemove.getUsername() + " removed successfully!");
    }

    @Override
    public void displayInfo() {
        System.out.println("----- Staff Info -----");
        System.out.println("Name: " + getName());
        System.out.println("Age: " + getAge());
        System.out.println("National ID: " + getNationalID());
        System.out.println("Phone: " + getPhonenumber());
        System.out.println("Email: " + getEmail());
        System.out.println("Role: " + getRole());
        System.out.println("Dependants: " + getDependants());
        System.out.println("----------------------");
    }
}
