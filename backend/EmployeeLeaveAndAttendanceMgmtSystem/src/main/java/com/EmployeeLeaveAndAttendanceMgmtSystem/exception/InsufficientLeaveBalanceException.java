package com.EmployeeLeaveAndAttendanceMgmtSystem.exception;

public class InsufficientLeaveBalanceException extends RuntimeException {
  public InsufficientLeaveBalanceException(String message) {
    super(message);
  }
}