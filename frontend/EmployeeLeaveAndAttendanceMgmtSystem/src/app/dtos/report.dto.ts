export interface AttendanceReportRequest {
    userId: number;
    month: string; 
  }
  

  export interface AttendanceReportResponse {
    userId: number;
    totalWorkingDays: number;
    presentDays: number;
    absentDays: number;
    leaveDays: number;
  }
  
  export interface LeaveReportResponse {
    userId: number;
    totalLeaveDays: number;
    approvedPaidLeaveDays: number;
    approvedSickLeaveDays: number;
    approvedUnpaidLeaveDays: number;
    remainingPaidLeave: number;
    remainingSickLeave: number;
    remainingUnpaidLeave: number;
  }
  
  export interface ShiftReportResponse {
    userId: number;
    totalShiftsAssigned: number;
    dayShifts: number;
    nightShifts: number;
    totalSwapsRequested: number;
    successfulSwaps: number;
    rejectedSwaps: number;
    remainingSwapBalance: number;
  }
  
  
  export interface LeaveReportRequest {
    userId: number;
    month: number;
  }
  
  
  export interface ShiftReportRequest {
    userId: number;
    month: string; 
  }
  