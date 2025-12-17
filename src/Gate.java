public class Gate{

    private int gateId;
    private String location;
    private boolean isOpen;

    public Gate(int gateId, String location) {
        this.gateId = gateId;
        this.location = location;
        this.isOpen = false;
    }

    public boolean scanQRCode(QRCode qr) {
        if (qr.validate()) {   
            openGate();
            return true;
        } else {
            closeGate();
            return false;
        }
    }

    public int getGateId() {
        return gateId;
    }

    public void setGateId(int gateId) {
        this.gateId = gateId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public void openGate() {
        isOpen = true;
        System.out.println("✔ Gate " + gateId + " opened.");
    }

    public void closeGate() {
        isOpen = false;
        System.out.println("✖ Gate " + gateId + " closed.");
    }
}
