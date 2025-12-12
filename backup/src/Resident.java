

public class Resident extends person {

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

}
