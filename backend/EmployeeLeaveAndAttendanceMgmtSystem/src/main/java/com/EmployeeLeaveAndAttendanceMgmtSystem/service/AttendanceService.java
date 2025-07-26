package com.EmployeeLeaveAndAttendanceMgmtSystem.service;

import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.AttendanceResponseDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.AttendanceRequest;

import java.util.List;

public interface AttendanceService {

    void clockIn(AttendanceRequest request);

    void clockOut(AttendanceRequest request);

    List<AttendanceResponseDTO> getAttendanceHistory(Long userId);

    List<AttendanceResponseDTO> getAttendanceHistoryForManager(String managerEmail);
}

