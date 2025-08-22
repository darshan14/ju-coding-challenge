package models;

import java.time.LocalDateTime;

public class Timesheet {
    public Long agencyId;
    public Long timesheetId;
    public Long workerId;
    public LocalDateTime createdDate;

    @Override
    public String toString() {
        return "Timesheet{" +
                "agencyId=" + agencyId +
                ", timesheetId=" + timesheetId +
                ", workerId=" + workerId +
                ", createdDate=" + createdDate +
                '}';
    }
}
