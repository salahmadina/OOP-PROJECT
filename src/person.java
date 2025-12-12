

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.Serializable;


public class person implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String name;
    protected int age;
    private int digits;
    private String nationalID;
    private int phonenumber;
    private String email;
    private String role;
    private String dependants;
    private String address;
    private String username;
    private String password;
    private Scanner scanner;


    public person(String password, String username,
                  String name, int age, String nationalID, String phone, String email,
                  String role, String dependants) {

        this.name = name;
        this.age = age;
        this.nationalID = nationalID;
        this.digits = nationalID.length();
        this.phonenumber = Integer.parseInt(phone);
        this.email = email;
        this.role = role;
        this.dependants = dependants;
        this.username = username;
        this.password = password;

    }


    public void validateNationalID() {
        if (digits != 16) {
            System.out.println("Invalid National ID!");
        } else {
            System.out.println("National ID verified.");
        }
    }


    public void verifyAge() {
        if (age < 18) {
            System.out.println("Under 18 Not Allowed.");
        } else {
            System.out.println("Age verified: " + age);
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public String getNationalID() {
        return nationalID;
    }

    public void setNationalID(String nationalID) {
        this.nationalID = nationalID;
        this.digits = nationalID.length();
    }

    public int getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phone) {
        this.phonenumber = Integer.parseInt(phone);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getDependants() {
        return dependants;
    }

    public void setDependants(String dependants) {
        this.dependants = dependants;
    }

    public int getDigits() {
        return digits;
    }


    @Override
    public String toString() {
        return "person [name=" + name + ", digits=" + digits + ", nationalID=" + nationalID + ", phonenumber="
                + phonenumber + ", email=" + email + ", role=" + role + ", dependants=" + dependants + ", address="
                + address + ", transportationID=" + ", username=" + username + ", password="
                + password + ", apartmentNumber=" +
                ", scanner=" + scanner + "]";
    }
}
