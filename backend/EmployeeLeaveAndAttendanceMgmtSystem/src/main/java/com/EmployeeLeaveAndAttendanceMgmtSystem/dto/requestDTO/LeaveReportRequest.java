package com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO;

import java.time.Year;

public class LeaveReportRequest {
    private Long userId;
    private String month;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMonth() {
        return this.month;
    }

}
