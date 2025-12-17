import java.util.ArrayList;
public class QRGateSystem {

    private ArrayList<Gate> gates = new ArrayList<>();
    private ArrayList<QRCode> qrCodes = new ArrayList<>();
    private ArrayList<String> logs = new ArrayList<>();

    public QRGateSystem() {
        this.gates = DataService.loadGates();
        this.qrCodes = DataService.loadQRCodes();
        this.logs = DataService.loadLogs();
    }

    public void saveAllData() {
        DataService.saveGates(gates);
        DataService.saveQRCodes(qrCodes);
        DataService.saveLogs(logs);
    }

    public void addGate(Gate gate) {
        gates.add(gate);
    }

    public QRCode assignQRCodeToUser(String userId) {
        QRCode qr = new QRCode(userId);
        qrCodes.add(qr);
        return qr;
    }

    public void logEntry(String userId) {
        logs.add("User " + userId + " entered.");
        saveAllData(); 
    }

    public void logExit(String userId) {
        logs.add("User " + userId + " exited.");
        saveAllData(); 
    }

    public void showLogs() {
        System.out.println("\n===== LOGS =====");
        for (String log : logs) System.out.println(log);
    }

}
