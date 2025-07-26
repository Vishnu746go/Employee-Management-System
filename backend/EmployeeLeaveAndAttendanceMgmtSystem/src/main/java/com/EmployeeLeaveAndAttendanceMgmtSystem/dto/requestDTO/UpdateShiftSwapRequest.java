package com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO;

import com.EmployeeLeaveAndAttendanceMgmtSystem.enums.Status;
import jakarta.validation.constraints.NotNull;

public class UpdateShiftSwapRequest {
    @NotNull(message = "Swap ID cannot be null")
    private Long swapId;

    @NotNull(message = "Status cannot be null")
    private Status status;

    public Long getSwapId() {
        return swapId;
    }

    public void setSwapId(Long swapId) {
        this.swapId = swapId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
