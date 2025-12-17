
public class Resident extends person implements info {
    private static final long serialVersionUID = 1L;

    private String dependants;
    int apartmentNumber2;
     int buildingNumber;

    public Resident(String password, String username, String name, int age, String nationalID,
                    String phone, String email,String role, String dependants, int apartmentNumber2,
                    int buildingNumber) {

        super(password, username, name, age, nationalID,phone, email,role,dependants);
        this.apartmentNumber2 = apartmentNumber2;
        this.buildingNumber = buildingNumber;
        this.dependants = dependants;

    }

    public int getApartmentNumber2() {
        return apartmentNumber2;
    }
    public  int getbuildingNumber() {
        return buildingNumber;
    }


    public String getDependants() {
        return dependants;
    }

    public void setDependants(String dependants) {
        this.dependants = dependants;
    }

    @Override
    public void displayInfo() {
        System.out.println("----- Resident Info -----");
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

