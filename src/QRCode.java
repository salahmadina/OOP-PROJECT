public class QRCode {

    private String userId;
    private String code;

    public QRCode(String userId) {
        this.userId = userId;
        this.code = "QR-" + userId + "-" + System.currentTimeMillis();}

    public String getCode() {
        return code;  
    }

    public boolean validate() {
        return true; 
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
