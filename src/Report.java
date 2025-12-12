

import java.time.LocalDateTime;
import java.util.UUID;
import java.io.Serializable;

public class Report implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reportId;
    private String resdientnName;
    private String type;
    private String description;
    private LocalDateTime dateTime;
    private String status;

    public Report(person Resdient, String type, String description) {
        this.reportId = UUID.randomUUID().toString();
        this.resdientnName = Resdient.getName();
        this.type = type;
        this.description = description;
        this.dateTime = LocalDateTime.now();
        this.status = "Sent";
    }

    public String getReportId() { return reportId; }
    public String getCitizenName() { return resdientnName; }
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
                ", resdientnName='" + resdientnName + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", dateTime=" + dateTime +
                ", status='" + status + '\'' +
                '}';
    }
}
