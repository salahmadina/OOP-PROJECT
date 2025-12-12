

import java.util.ArrayList;

public class QRGateSystem {

    private ArrayList<Gate> gates = new ArrayList<>();
    private ArrayList<QRCode> qrCodes = new ArrayList<>();
    private ArrayList<String> logs = new ArrayList<>();

    public QRGateSystem() {
        // Load data from files on initialization
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

    public QRCode assignQRCodeToUser(int userId) {
        QRCode qr = new QRCode(userId);
        qrCodes.add(qr);
        return qr;
    }

    public void logEntry(int userId) {
        logs.add("User " + userId + " entered.");
        saveAllData(); // Save after logging
    }

    public void logExit(int userId) {
        logs.add("User " + userId + " exited.");
        saveAllData(); // Save after logging
    }

    public void showLogs() {
        System.out.println("\n===== LOGS =====");
        for (String log : logs) System.out.println(log);
    }
    public ArrayList<String> getLogs() {
        return logs;
    }

}
