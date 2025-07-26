package com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO;

public class LeaveReportResponseDTO {
    private Long userId;
    private int totalLeaveDays;
    private int approvedPaidLeaveDays;
    private int approvedSickLeaveDays;
    private int approvedUnpaidLeaveDays;
    private int remainingPaidLeave;
    private int remainingSickLeave;
    private int remainingUnpaidLeave;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getTotalLeaveDays() {
        return totalLeaveDays;
    }

    public void setTotalLeaveDays(int totalLeaveDays) {
        this.totalLeaveDays = totalLeaveDays;
    }

    public int getApprovedPaidLeaveDays() {
        return approvedPaidLeaveDays;
    }

    public void setApprovedPaidLeaveDays(int approvedPaidLeaveDays) {
        this.approvedPaidLeaveDays = approvedPaidLeaveDays;
    }

    public int getApprovedSickLeaveDays() {
        return approvedSickLeaveDays;
    }

    public void setApprovedSickLeaveDays(int approvedSickLeaveDays) {
        this.approvedSickLeaveDays = approvedSickLeaveDays;
    }

    public int getApprovedUnpaidLeaveDays() {
        return approvedUnpaidLeaveDays;
    }

    public void setApprovedUnpaidLeaveDays(int approvedUnpaidLeaveDays) {
        this.approvedUnpaidLeaveDays = approvedUnpaidLeaveDays;
    }

    public int getRemainingPaidLeave() {
        return remainingPaidLeave;
    }

    public void setRemainingPaidLeave(int remainingPaidLeave) {
        this.remainingPaidLeave = remainingPaidLeave;
    }

    public int getRemainingSickLeave() {
        return remainingSickLeave;
    }

    public void setRemainingSickLeave(int remainingSickLeave) {
        this.remainingSickLeave = remainingSickLeave;
    }

    public int getRemainingUnpaidLeave() {
        return remainingUnpaidLeave;
    }

    public void setRemainingUnpaidLeave(int remainingUnpaidLeave) {
        this.remainingUnpaidLeave = remainingUnpaidLeave;
    }
}
