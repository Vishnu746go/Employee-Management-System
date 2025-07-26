package com.EmployeeLeaveAndAttendanceMgmtSystem.service;

import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.CreateLeaveRequest;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.UpdateLeaveRequest;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.LeaveBalanceResponseDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.LeaveRequestResponseDTO;

import java.util.List;

public interface LeaveService {

    void createLeaveRequest(CreateLeaveRequest leave);

    void updateLeaveStatus(UpdateLeaveRequest dto);

    List<LeaveRequestResponseDTO> getLeaveRequests(Long userId);

    List<LeaveRequestResponseDTO> getLeaveRequestsForManagerOnLeaveStatus(String managerEmail, String status);

    List<LeaveRequestResponseDTO> getLeaveRequestHistoryForManager(String managerEmail);

    void createLeaveBalance(Long userId);


    LeaveBalanceResponseDTO getLeaveBalance(Long userId);

    List<LeaveBalanceResponseDTO> getLeaveBalancesForManager(String managerEmail);

    LeaveBalanceResponseDTO getLeaveBalanceForUserUnderManager(String managerEmail, Long userId);

    List<LeaveRequestResponseDTO> getLeaveHistoryForUserUnderManager(String managerEmail, Long userId);

}