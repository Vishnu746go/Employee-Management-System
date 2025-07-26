package com.EmployeeLeaveAndAttendanceMgmtSystem.service.Impl;

import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.AttendanceReportResponseDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.LeaveReportResponseDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.ShiftReportResponseDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.entity.*;
import com.EmployeeLeaveAndAttendanceMgmtSystem.enums.AttendanceStatus;
import com.EmployeeLeaveAndAttendanceMgmtSystem.enums.ShiftType;
import com.EmployeeLeaveAndAttendanceMgmtSystem.enums.Status;
import com.EmployeeLeaveAndAttendanceMgmtSystem.exception.ResourceNotFoundException;
import com.EmployeeLeaveAndAttendanceMgmtSystem.repository.*;
import com.EmployeeLeaveAndAttendanceMgmtSystem.service.ReportService;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final AttendanceRepository attendanceRepository;

    private final UserRepository userRepository;

    private final LeaveBalanceRepository leaveBalanceRepository;

    private final LeaveRequestRepository leaveRequestRepository;

    private final ShiftRepository shiftRepository;

    private final ShiftBalanceRepository shiftBalanceRepository;

    private final ShiftSwapRequestRepository shiftSwapRequestRepository;

    @Autowired
    public ReportServiceImpl(AttendanceRepository attendanceRepository, UserRepository userRepository, LeaveBalanceRepository leaveBalanceRepository, LeaveRequestRepository leaveRequestRepository, ShiftRepository shiftRepository, ShiftBalanceRepository shiftBalanceRepository, ShiftSwapRequestRepository shiftSwapRequestRepository){
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.shiftRepository = shiftRepository;
        this.shiftBalanceRepository = shiftBalanceRepository;
        this.shiftSwapRequestRepository = shiftSwapRequestRepository;
        this.userRepository = userRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public AttendanceReportResponseDTO generateAttendanceReport(Long userId, YearMonth month) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        int totalWorkingDays = 0, presentDays = 0, absentDays = 0, leaveDays = 0;

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // Skip weekends
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                continue;
            }
            totalWorkingDays++;

            Attendance attendance = attendanceRepository.findByUserIdAndDate(userId, date).orElse(null);

            if (attendance == null) {
                absentDays++;
            } else if (attendance.getStatus() == AttendanceStatus.LEAVE) {
                leaveDays++;
            } else if (attendance.getClockInTime() != null && attendance.getClockOutTime() != null) {
                presentDays++;
            } else {
                absentDays++;
            }
        }

        AttendanceReportResponseDTO report = new AttendanceReportResponseDTO();
        report.setUserId(userId);
        report.setTotalWorkingDays(totalWorkingDays);
        report.setPresentDays(presentDays);
        report.setAbsentDays(absentDays);
        report.setLeaveDays(leaveDays);

        return report;
    }

    @Override
    public LeaveReportResponseDTO generateLeaveReport(Long userId, YearMonth month) {
        // Fetch leave balance for user
        LeaveBalance balance = leaveBalanceRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found"));

        // Get all approved leave requests for the user
        List<LeaveRequest> approvedLeaves = leaveRequestRepository.findByUserIdAndStatus(userId, Status.APPROVED);

        // Initialize counters
        int totalLeaveDays = 0;
        int paidLeaveDays = 0;
        int sickLeaveDays = 0;
        int unpaidLeaveDays = 0;

        // Filter by the specific YearMonth
        for (LeaveRequest leave : approvedLeaves) {
            LocalDate startDate = leave.getStartDate();
            if (YearMonth.from(startDate).equals(month)) {
                int days = (int) (leave.getEndDate().toEpochDay() - startDate.toEpochDay() + 1);
                totalLeaveDays += days;

                // Categorize leave type
                switch (leave.getLeaveType()) {
                    case PAID -> paidLeaveDays += days;
                    case SICK -> sickLeaveDays += days;
                    case UNPAID -> unpaidLeaveDays += days;
                }
            }
        }

        // Create and populate the response DTO
        LeaveReportResponseDTO report = new LeaveReportResponseDTO();
        report.setUserId(userId);
        report.setTotalLeaveDays(totalLeaveDays);
        report.setApprovedPaidLeaveDays(paidLeaveDays);
        report.setApprovedSickLeaveDays(sickLeaveDays);
        report.setApprovedUnpaidLeaveDays(unpaidLeaveDays);
        report.setRemainingPaidLeave(balance.getPaidLeaveBalance());
        report.setRemainingSickLeave(balance.getSickLeaveBalance());
        report.setRemainingUnpaidLeave(balance.getUnpaidLeaveBalance());

        return report;
    }



    @Override
    public ShiftReportResponseDTO generateShiftReport(Long userId, YearMonth month) {
        List<Shift> shifts = shiftRepository.findByUserIdAndMonth(userId, month.getYear(), month.getMonthValue());
        List<ShiftSwapRequest> swaps = shiftSwapRequestRepository.findByUserIdAndMonth(userId, month.getYear(), month.getMonthValue());
        ShiftBalance balance = shiftBalanceRepository.findByUserIdAndYearAndMonth(userId, month.getYear(), month.getMonthValue())
                .orElseThrow(() -> new ResourceNotFoundException("Shift balance not found"));

        int dayShifts = 0, nightShifts = 0, totalSwaps = 0, successfulSwaps = 0, rejectedSwaps = 0;

        for (Shift shift : shifts) {
            if (shift.getShiftType() == ShiftType.DAY) {
                dayShifts++;
            } else if (shift.getShiftType() == ShiftType.NIGHT) {
                nightShifts++;
            }
        }

        for (ShiftSwapRequest swap : swaps) {
            totalSwaps++;
            if (swap.getStatus() == Status.APPROVED) {
                successfulSwaps++;
            } else if (swap.getStatus() == Status.REJECTED) {
                rejectedSwaps++;
            }
        }

        ShiftReportResponseDTO report = new ShiftReportResponseDTO();
        report.setUserId(userId);
        report.setTotalShiftsAssigned(shifts.size());
        report.setDayShifts(dayShifts);
        report.setNightShifts(nightShifts);
        report.setTotalSwapsRequested(totalSwaps);
        report.setSuccessfulSwaps(successfulSwaps);
        report.setRejectedSwaps(rejectedSwaps);
        report.setRemainingSwapBalance(balance.getSwapCount());

        return report;
    }

}