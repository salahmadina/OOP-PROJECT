
import java.time.LocalDateTime;
import java.util.UUID;

public class Report {

    private String reportId;
    private String resdientName;
    private String type;
    private String description;
    private LocalDateTime dateTime;
    private String status;

    public Report(person Resdient, String type, String description) {
        this.reportId = UUID.randomUUID().toString();
        this.resdientName = Resdient.getName();
        this.type = type;
        this.description = description;
        this.dateTime = LocalDateTime.now();
        this.status = "Sent";
    }

    public String getReportId() { return reportId; }
    public String getCitizenName() { return resdientName; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public LocalDateTime getDateTime() { return dateTime; }
    public String getStatus() { return status; }


    public void setType(String type) { this.type = type; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Report{" +
                "reportId='" + reportId + '\'' +
                ", resdientName='" + resdientName + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", dateTime=" + dateTime +
                ", status='" + status + '\'' +
                '}';
    }
}
