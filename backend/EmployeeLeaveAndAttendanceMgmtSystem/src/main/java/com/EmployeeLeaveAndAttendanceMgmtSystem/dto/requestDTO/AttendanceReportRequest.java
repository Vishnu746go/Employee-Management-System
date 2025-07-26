package com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO;

import java.time.YearMonth;

public class AttendanceReportRequest {

    private Long userId;
    private YearMonth month;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public YearMonth getMonth() {
        return month;
    }

    public void setMonth(YearMonth month) {
        this.month = month;
    }
}