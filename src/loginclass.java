
import java.util.Scanner;

public class loginclass {

    private String username;
    private String password;
    private person person; // link to person class

    // Constructor that takes a person object
    public loginclass(person person) {
        this.person = person;
        this.username = person.getUsername();
        this.password = person.getPassword();
    }

    // Constructor that takes username + password only
    public loginclass(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and setters
    public person getPerson() {
        return person;
    }

    public void setPerson(person person) {
        this.person = person;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Validate login
    public boolean validateLogin(String inputUser, String inputPass) {

        if (!inputUser.equals(this.username)) {
            System.out.println("Login Failed: Incorrect username!");
            return false;
        }

        if (!inputPass.equals(this.password)) {
            System.out.println("Login Failed: Incorrect password!");
            return false;
        }

        System.out.println("Login successful! Welcome, " + inputUser + ".");

        // After successful login, ask the user if they want to update information
        offerUpdateInfo();

        return true;
    }

    // Ask user if they want to update personal info
    public void offerUpdateInfo() {

        // No person object attached
        if (this.person == null) {
            System.out.println("No personal profile found to update.");
            return;
        }

        Scanner input = new Scanner(System.in);

        System.out.println("Do you want to update your information? (yes/no)");
        String answer = input.nextLine();

        if (answer.equalsIgnoreCase("yes")) {

            System.out.print("Enter new address: ");
            String newAddress = input.nextLine();

            System.out.print("Enter new phone number: ");
            String newPhone = input.nextLine();

            System.out.print("Enter new email: ");
            String newEmail = input.nextLine();



        } else {
            System.out.println("No changes were made.");
        }
    }

    @Override
    public String toString() {
        return "loginclass [username=" + username + "]";
    }
}
