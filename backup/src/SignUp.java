



import java.util.ArrayList;
import java.util.Scanner;

public class SignUp {

    private Scanner scanner = new Scanner(System.in);
    private ArrayList<person> users = new ArrayList<>();

    public SignUp() {
        // Load users from file on initialization
        this.users = DataService.loadUsers();
    }

    public ArrayList<person> getUsers() {
        return users;
    }

    public void saveUsers() {
        DataService.saveUsers(users);
    }

    // Validation method to check for duplicate data
    public String validateDuplicates(String email, String phone, String nationalID) {
        for (person user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return "Email already registered!";
            }
            if (String.valueOf(user.getPhonenumber()).equals(phone)) {
                return "Phone number already registered!";
            }
            if (user.getNationalID().equals(nationalID)) {
                return "National ID already registered!";
            }
        }
        return null; // No duplicates found
    }

    public void createAccount() {

        System.out.println("Create account as: ");
        System.out.println("1. Resident");
        System.out.println("2. Staff");
        System.out.print("Choose: ");
        String choice = scanner.nextLine();

        // Basic info
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Phone: ");
        String phone = scanner.nextLine();

        System.out.print("Choose Username: ");
        String username = scanner.nextLine();

        System.out.print("Choose Password: ");
        String password = scanner.nextLine();

        System.out.print("enter age: ");
        int age = scanner.nextInt();

        System.out.print("enter nationalID: ");
        String nationalID = scanner.nextLine();

        scanner.nextLine();

        System.out.print("enter role: ");
        String role = scanner.nextLine();

        System.out.print("enter dependants: ");
        String dependants = scanner.nextLine();

        // Validate for duplicates
        String validationError = validateDuplicates(email, phone, nationalID);
        if (validationError != null) {
            System.out.println("ERROR: " + validationError);
            return;
        }

        if (choice.equals("1")) {

            System.out.print("Enter Building Number: ");
            int buildingNumber = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter Apartment Number: ");
            int apartmentNumber2 = Integer.parseInt(scanner.nextLine());

            person r = new Resident(password , username , name , age, nationalID , phone , email , role , dependants ,apartmentNumber2,buildingNumber);
            users.add(r);
            saveUsers(); // Save after adding

            System.out.println("Resident created successfully!");

        } else if (choice.equals("2")) {

            System.out.print("Enter Job Title: ");
            String jobTitle = scanner.nextLine();

            person s = new Staff(password, username, name, age, nationalID, phone,email,role,dependants);
            users.add(s);
            saveUsers(); // Save after adding

            System.out.println("Staff created successfully!");
        }
    }
}
