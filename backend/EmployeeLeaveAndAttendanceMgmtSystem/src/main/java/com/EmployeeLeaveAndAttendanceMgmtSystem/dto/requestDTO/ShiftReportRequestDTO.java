package com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO;

public class ShiftReportRequestDTO {

    private Long userId;
    private String month;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}