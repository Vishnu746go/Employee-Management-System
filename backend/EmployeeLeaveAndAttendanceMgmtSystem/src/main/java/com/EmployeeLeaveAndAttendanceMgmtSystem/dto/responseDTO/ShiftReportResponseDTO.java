package com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO;

public class ShiftReportResponseDTO {
    private Long userId;
    private int totalShiftsAssigned;
    private int dayShifts;
    private int nightShifts;
    private int totalSwapsRequested;
    private int successfulSwaps;
    private int rejectedSwaps;
    private int remainingSwapBalance;

    // Getters and Setters


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getTotalShiftsAssigned() {
        return totalShiftsAssigned;
    }

    public void setTotalShiftsAssigned(int totalShiftsAssigned) {
        this.totalShiftsAssigned = totalShiftsAssigned;
    }

    public int getDayShifts() {
        return dayShifts;
    }

    public void setDayShifts(int dayShifts) {
        this.dayShifts = dayShifts;
    }

    public int getNightShifts() {
        return nightShifts;
    }

    public void setNightShifts(int nightShifts) {
        this.nightShifts = nightShifts;
    }

    public int getTotalSwapsRequested() {
        return totalSwapsRequested;
    }

    public void setTotalSwapsRequested(int totalSwapsRequested) {
        this.totalSwapsRequested = totalSwapsRequested;
    }

    public int getSuccessfulSwaps() {
        return successfulSwaps;
    }

    public void setSuccessfulSwaps(int successfulSwaps) {
        this.successfulSwaps = successfulSwaps;
    }

    public int getRejectedSwaps() {
        return rejectedSwaps;
    }

    public void setRejectedSwaps(int rejectedSwaps) {
        this.rejectedSwaps = rejectedSwaps;
    }

    public int getRemainingSwapBalance() {
        return remainingSwapBalance;
    }

    public void setRemainingSwapBalance(int remainingSwapBalance) {
        this.remainingSwapBalance = remainingSwapBalance;
    }
}
