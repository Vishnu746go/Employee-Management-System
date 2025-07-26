package com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO;


import java.util.List;

public class AssignEmployeeRequest {
    private Long managerId;
    private List<Long> employeeIds;

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public List<Long> getEmployeeIds() {
        return employeeIds;
    }

    public void setEmployeeIds(List<Long> employeeIds) {
        this.employeeIds = employeeIds;
    }
}
