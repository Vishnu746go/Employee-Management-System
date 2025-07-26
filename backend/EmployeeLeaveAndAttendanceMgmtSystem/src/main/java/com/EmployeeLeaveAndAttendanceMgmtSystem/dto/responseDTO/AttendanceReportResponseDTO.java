package com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO;

public class AttendanceReportResponseDTO {
    private Long userId;
    private int totalWorkingDays;
    private int presentDays;
    private int absentDays;
    private int leaveDays;

    // Getters and Setters

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getTotalWorkingDays() {
        return totalWorkingDays;
    }

    public void setTotalWorkingDays(int totalWorkingDays) {
        this.totalWorkingDays = totalWorkingDays;
    }

    public int getPresentDays() {
        return presentDays;
    }

    public void setPresentDays(int presentDays) {
        this.presentDays = presentDays;
    }

    public int getAbsentDays() {
        return absentDays;
    }

    public void setAbsentDays(int absentDays) {
        this.absentDays = absentDays;
    }

    public int getLeaveDays() {
        return leaveDays;
    }

    public void setLeaveDays(int leaveDays) {
        this.leaveDays = leaveDays;
    }
}