package com.EmployeeLeaveAndAttendanceMgmtSystem.exception;

public class InvalidLeaveRequestException extends RuntimeException {
    public InvalidLeaveRequestException(String message) {
        super(message);
    }
}
