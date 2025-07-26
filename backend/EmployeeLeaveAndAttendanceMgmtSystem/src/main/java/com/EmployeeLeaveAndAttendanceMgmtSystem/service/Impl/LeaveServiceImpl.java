package com.EmployeeLeaveAndAttendanceMgmtSystem.service.Impl;

import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.CreateLeaveRequest;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.UpdateLeaveRequest;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.LeaveBalanceResponseDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.LeaveRequestResponseDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.entity.Attendance;
import com.EmployeeLeaveAndAttendanceMgmtSystem.entity.LeaveBalance;
import com.EmployeeLeaveAndAttendanceMgmtSystem.entity.LeaveRequest;
import com.EmployeeLeaveAndAttendanceMgmtSystem.entity.User;
import com.EmployeeLeaveAndAttendanceMgmtSystem.enums.AttendanceStatus;
import com.EmployeeLeaveAndAttendanceMgmtSystem.enums.Status;
import com.EmployeeLeaveAndAttendanceMgmtSystem.exception.InsufficientLeaveBalanceException;
import com.EmployeeLeaveAndAttendanceMgmtSystem.exception.InvalidLeaveRequestException;
import com.EmployeeLeaveAndAttendanceMgmtSystem.exception.ResourceNotFoundException;
import com.EmployeeLeaveAndAttendanceMgmtSystem.repository.AttendanceRepository;
import com.EmployeeLeaveAndAttendanceMgmtSystem.repository.LeaveBalanceRepository;
import com.EmployeeLeaveAndAttendanceMgmtSystem.repository.LeaveRequestRepository;
import com.EmployeeLeaveAndAttendanceMgmtSystem.repository.UserRepository;
import com.EmployeeLeaveAndAttendanceMgmtSystem.service.LeaveService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveServiceImpl implements LeaveService {

    private final LeaveBalanceRepository leaveBalanceRepository;

    private final LeaveRequestRepository leaveRequestRepository;

    private final UserRepository userRepository;

    private final AttendanceRepository attendanceRepository;

    @Autowired
    public LeaveServiceImpl(LeaveRequestRepository leaveRequestRepository, LeaveBalanceRepository leaveBalanceRepository, UserRepository userRepository, AttendanceRepository attendanceRepository){
        this.attendanceRepository = attendanceRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.userRepository = userRepository;
    }

    private static final int DEFAULT_PAID_LEAVE = 20;
    private static final int DEFAULT_SICK_LEAVE = 10;
    private static final int DEFAULT_UNPAID_LEAVE = 20;

    @Override
    public void createLeaveRequest(CreateLeaveRequest leave) {
        User user = userRepository.findById(leave.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + leave.getUserId()));

        if (leave.getEndDate().isBefore(leave.getStartDate())) {
            throw new InvalidLeaveRequestException("End date cannot be before start date");
        }

        boolean hasOverlap = leaveRequestRepository.existsOverlappingLeaveRequest(user.getId(), leave.getStartDate(), leave.getEndDate());
        if (hasOverlap) {
            throw new InvalidLeaveRequestException("Overlapping leave request exists.");
        }


        LeaveBalance balance = leaveBalanceRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found for user"));

        long daysRequested = getWorkingDays(leave.getStartDate(), leave.getEndDate());

        // Check leave balance based on leave type
        switch (leave.getLeaveType()) {
            case PAID:
                if (balance.getPaidLeaveBalance() < daysRequested) {
                    throw new InsufficientLeaveBalanceException("Insufficient paid leave balance");
                }
                break;
            case SICK:
                if (balance.getSickLeaveBalance() < daysRequested) {
                    throw new InsufficientLeaveBalanceException("Insufficient sick leave balance");
                }
                break;
            case UNPAID:
                if (balance.getUnpaidLeaveBalance() < daysRequested) {
                    throw new InsufficientLeaveBalanceException("Insufficient unpaid leave balance");
                }
                break;
            default:
                throw new InvalidLeaveRequestException("Unknown leave type");
        }

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setUser(user);
        leaveRequest.setLeaveType(leave.getLeaveType());
        leaveRequest.setStartDate(leave.getStartDate());
        leaveRequest.setEndDate(leave.getEndDate());
        leaveRequest.setReason(leave.getReason());
        leaveRequest.setStatus(Status.PENDING);
        leaveRequest.setRequestDate(LocalDateTime.now());

        leaveRequestRepository.save(leaveRequest);
    }

    @Override
    @Transactional
    public void updateLeaveStatus(UpdateLeaveRequest dto) {
        User manager = userRepository.findById(dto.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        LeaveRequest leaveRequest = leaveRequestRepository.findById(dto.getLeaveRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));

        if (leaveRequest.getStatus() != Status.PENDING) {
            throw new InvalidLeaveRequestException("Only pending requests can be updated");
        }

        leaveRequest.setStatus(dto.getStatus());

        if (dto.getStatus() == Status.APPROVED) {
            LeaveBalance balance = leaveBalanceRepository.findByUserId(leaveRequest.getUser().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found"));

            long days = getWorkingDays(leaveRequest.getStartDate(), leaveRequest.getEndDate());

            // Deduct leave balance atomically & check
            switch (leaveRequest.getLeaveType()) {
                case PAID:
                    if (balance.getPaidLeaveBalance() < days)
                        throw new InsufficientLeaveBalanceException("Insufficient paid leave balance");
                    balance.setPaidLeaveBalance(balance.getPaidLeaveBalance() - (int) days);
                    break;
                case SICK:
                    if (balance.getSickLeaveBalance() < days)
                        throw new InsufficientLeaveBalanceException("Insufficient sick leave balance");
                    balance.setSickLeaveBalance(balance.getSickLeaveBalance() - (int) days);
                    break;
                case UNPAID:
                    if (balance.getUnpaidLeaveBalance() < days)
                        throw new InsufficientLeaveBalanceException("Insufficient unpaid leave balance");
                    balance.setUnpaidLeaveBalance(balance.getUnpaidLeaveBalance() - (int) days);
                    break;
                default:
                    throw new InvalidLeaveRequestException("Unknown leave type");
            }

            leaveBalanceRepository.save(balance);

            List<Attendance> attendanceList = new ArrayList<>();
            LocalDate startDate = leaveRequest.getStartDate();
            LocalDate endDate = leaveRequest.getEndDate();

            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    continue;
                }
                Attendance attendance = new Attendance();
                attendance.setUser(leaveRequest.getUser());
                attendance.setDate(date);
                attendance.setStatus(AttendanceStatus.LEAVE);
                attendanceList.add(attendance);
            }

            attendanceRepository.saveAll(attendanceList);
        }

        leaveRequestRepository.save(leaveRequest);
    }

    // method to get only working days
    private long getWorkingDays(LocalDate start, LocalDate end) {
        long workingDays = 0;
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            if (!(date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                workingDays++;
            }
        }
        return workingDays;
    }

    @Override
    public List<LeaveRequestResponseDTO> getLeaveRequests(Long userId) {
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findByUserId(userId);
        return leaveRequests.stream()
                .map(LeaveRequest::getDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveRequestResponseDTO> getLeaveRequestsForManagerOnLeaveStatus(String managerEmail, String status) {
        Status leaveStatus;
        try {
            leaveStatus = Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidLeaveRequestException("Invalid leave status: " + status);
        }

        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        List<User> users = userRepository.findByManager(manager);
        List<Long> userIds = users.stream().map(User::getId).toList();

        List<LeaveRequest> leaveRequests = leaveRequestRepository.findByUserIdInAndStatus(userIds, leaveStatus);

        return leaveRequests.stream()
                .map(LeaveRequest::getDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveRequestResponseDTO> getLeaveRequestHistoryForManager(String managerEmail) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        List<User> users = userRepository.findByManager(manager);
        List<Long> userIds = users.stream().map(User::getId).toList();

        List<LeaveRequest> leaveRequests = leaveRequestRepository.findByUserIdIn(userIds);

        return leaveRequests.stream()
                .map(LeaveRequest::getDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void createLeaveBalance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        LeaveBalance leaveBalance = new LeaveBalance();
        leaveBalance.setUser(user);
        leaveBalance.setPaidLeaveBalance(DEFAULT_PAID_LEAVE);
        leaveBalance.setSickLeaveBalance(DEFAULT_SICK_LEAVE);
        leaveBalance.setUnpaidLeaveBalance(DEFAULT_UNPAID_LEAVE);

        leaveBalanceRepository.save(leaveBalance);
    }

    @Override
    public LeaveBalanceResponseDTO getLeaveBalance(Long userId) {
        LeaveBalance leaveBalance = leaveBalanceRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveBalance not found with id " + userId));
        return leaveBalance.getDTO();
    }

    @Override
    public List<LeaveBalanceResponseDTO> getLeaveBalancesForManager(String managerEmail) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        List<User> users = userRepository.findByManager(manager);
        List<Long> userIds = users.stream().map(User::getId).toList();

        List<LeaveBalance> leaveBalances = leaveBalanceRepository.findByUserIdIn(userIds);

        return leaveBalances.stream()
                .map(LeaveBalance::getDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LeaveBalanceResponseDTO getLeaveBalanceForUserUnderManager(String managerEmail, Long userId) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getManager().getId().equals(manager.getId())) {
            throw new AccessDeniedException("This user is not assigned to the current manager.");
        }

        LeaveBalance leaveBalance = leaveBalanceRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found for user: " + userId));

        return leaveBalance.getDTO();
    }

    @Override
    public List<LeaveRequestResponseDTO> getLeaveHistoryForUserUnderManager(String managerEmail, Long userId) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getManager().getId().equals(manager.getId())) {
            throw new AccessDeniedException("This user is not assigned to the current manager.");
        }

        List<LeaveRequest> leaveRequests = leaveRequestRepository.findByUserId(userId);

        return leaveRequests.stream()
                .map(LeaveRequest::getDTO)
                .collect(Collectors.toList());
    }

}