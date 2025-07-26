package com.EmployeeLeaveAndAttendanceMgmtSystem.service;

import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.AttendanceReportResponseDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.LeaveReportResponseDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.ShiftReportResponseDTO;

import java.time.Year;
import java.time.YearMonth;

public interface ReportService {
    AttendanceReportResponseDTO generateAttendanceReport(Long userId, YearMonth month);
    LeaveReportResponseDTO generateLeaveReport(Long userId, YearMonth month);
    ShiftReportResponseDTO generateShiftReport(Long userId, YearMonth month);
}