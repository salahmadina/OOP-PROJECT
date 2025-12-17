public class request {
    private Resident resident;
    private String service;
    private String details;

    public request(Resident resident, String service, String details) {
        this.resident = resident;
        this.service = service;
        this.details = details;
    }

    public void ShowTicket() {
        System.out.println("\n===== NEW REQUEST TICKET =====");
        System.out.println("Location:   Building " + resident.getbuildingNumber() + ", Apt " + resident.getApartmentNumber2());
        System.out.println("Service:   " + service);
        System.out.println("Details:    " + details);
        System.out.println("==============================\n");
    }
}