

public class smartcard {

    private String cardID;
    private String homeZone;

    public smartcard(String cardID, String homeZone) {
        this.cardID = cardID;
        this.homeZone = homeZone;
    }

    public String getCardID() {
        return cardID;
    }

    public String getHomeZone() {
        return homeZone;
    }

    @Override
    public String toString() {
        return "SmartCard ID: " + cardID + ", Home Zone: " + homeZone;
    }
}
