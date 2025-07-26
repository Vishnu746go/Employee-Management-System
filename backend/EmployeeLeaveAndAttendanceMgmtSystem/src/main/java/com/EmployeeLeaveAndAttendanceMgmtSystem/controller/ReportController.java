package com.EmployeeLeaveAndAttendanceMgmtSystem.controller;


import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.AttendanceReportRequest;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.LeaveReportRequest;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.ShiftReportRequestDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.AttendanceReportResponseDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.LeaveReportResponseDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.ShiftReportResponseDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @PostMapping("/attendance/")
    public AttendanceReportResponseDTO getAttendanceReport(@RequestBody AttendanceReportRequest attendanceReportRequest) {
        YearMonth month = attendanceReportRequest.getMonth();
        return reportService.generateAttendanceReport(attendanceReportRequest.getUserId(), month);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @PostMapping("/leave/")
    public LeaveReportResponseDTO getLeaveReport(@RequestBody LeaveReportRequest leaveReportRequest) {
        YearMonth yearMonth = YearMonth.parse(leaveReportRequest.getMonth());
        return reportService.generateLeaveReport(leaveReportRequest.getUserId(),yearMonth);
    }

    @PostMapping("/shift")
    public ShiftReportResponseDTO getShiftReport(@RequestBody ShiftReportRequestDTO request) {
        YearMonth yearMonth = YearMonth.parse(request.getMonth());
        return reportService.generateShiftReport(request.getUserId(), yearMonth);
    }
}
