package com.EmployeeLeaveAndAttendanceMgmtSystem.service;

import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.CreateShiftSwapRequest;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.ShiftAssignmentRequest;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.UpdateShiftSwapRequest;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.ShiftResponseDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.ShiftSwapResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface ShiftService {

    void assignShift(ShiftAssignmentRequest request);

    void assignWeeklyShifts(ShiftAssignmentRequest request);

    ShiftResponseDTO getEmployeeShiftByDate(Long userId, LocalDate date);

    int getSwapBalance(Long userId);

    List<ShiftResponseDTO> getShiftsByDateForManager(Long managerId, LocalDate date);

    void createSwapRequest(CreateShiftSwapRequest request);

    void updateSwapRequestStatus(UpdateShiftSwapRequest request);

    List<ShiftSwapResponseDTO> getSwapRequestsByUser(Long userId);

    List<ShiftSwapResponseDTO> getShiftSwapRequestsForManagerOnStatus(String managerEmail, String status);

    List<ShiftSwapResponseDTO> getSwapRequestsForManager(Long managerId);

    List<ShiftResponseDTO> getShiftHistoryForUserUnderManager(String managerEmail, Long userId);

    int getSwapBalanceForUserUnderManager(String managerEmail, Long userId);
}
