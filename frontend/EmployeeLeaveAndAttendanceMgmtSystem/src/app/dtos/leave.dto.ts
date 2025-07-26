export interface CreateLeaveRequest {
    userId: number;
    leaveType: string;
    startDate: string;
    endDate: string;
    reason: string;
  }
  
  export interface LeaveRequestResponseDTO {
    id: number;
    userId: number;
    employeeName?: string;
    leaveType: string;
    startDate: string;
    endDate: string;
    reason: string;
    status: string;
  }
  
  export interface LeaveBalanceResponseDTO {
    sickLeaveBalance: number;
    paidLeaveBalance: number;
    unpaidLeaveBalance: number;
  }

  export interface UpdateLeaveRequest {
    leaveRequestId: number;
    status: string;  
}