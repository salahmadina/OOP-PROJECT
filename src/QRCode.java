
/*
import java.time.LocalDateTime;
import java.util.UUID;

public class QRCode {
    private String code;
    private int ownerId;
    private LocalDateTime expiryDate;
    private boolean isValid;

    public QRCode(int ownerId) {
        this.ownerId = ownerId;
        generate();
    }

    public void generate() {
        this.code = UUID.randomUUID().toString();
        this.expiryDate = LocalDateTime.now().plusMinutes(5);
        this.isValid = true;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    public boolean validate() {
        return isValid && !isExpired();
    }

    public String getCode() {
        return code;
    }

    public int getOwnerId() {
        return ownerId;
    }
}*/

public class QRCode {

    private int userId;
    private String code;

    public QRCode(int userId) {
        this.setUserId(userId);
        this.code = "QR" + userId + System.currentTimeMillis();
    }

    public String getCode() {
        return code;  // This is required for qr.getCode() to work
    }

    public boolean validate() {
        return true; // Or your validation logic
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
