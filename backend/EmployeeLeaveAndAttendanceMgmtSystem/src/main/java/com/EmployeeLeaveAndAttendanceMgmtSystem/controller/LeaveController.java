package com.EmployeeLeaveAndAttendanceMgmtSystem.controller;

import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.CreateLeaveRequest;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.LeaveBalanceResponseDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.LeaveRequestResponseDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.UpdateLeaveRequest;
import com.EmployeeLeaveAndAttendanceMgmtSystem.service.Impl.LeaveServiceImpl;
import com.EmployeeLeaveAndAttendanceMgmtSystem.utils.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    private final LeaveServiceImpl leaveServiceImpl;

    @Autowired
    public LeaveController(LeaveServiceImpl leaveServiceImpl){
        this.leaveServiceImpl = leaveServiceImpl;
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/create-request")
    public ResponseEntity<String> createLeaveRequest(@Valid @RequestBody CreateLeaveRequest request) {
        // Override userId from security context to prevent tampering
        Long userId = SecurityUtil.getCurrentUserId();
        request.setUserId(userId);

        leaveServiceImpl.createLeaveRequest(request);
        return ResponseEntity.ok("Leave request submitted successfully.");
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/requests")
    public ResponseEntity<List<LeaveRequestResponseDTO>> getLeaveRequests() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<LeaveRequestResponseDTO> leaveRequests = leaveServiceImpl.getLeaveRequests(userId);
        return ResponseEntity.ok(leaveRequests);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/balance")
    public ResponseEntity<LeaveBalanceResponseDTO> getLeaveBalance() {
        Long userId = SecurityUtil.getCurrentUserId();
        LeaveBalanceResponseDTO balance = leaveServiceImpl.getLeaveBalance(userId);
        return ResponseEntity.ok(balance);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager/{status}")
    public ResponseEntity<List<LeaveRequestResponseDTO>> getLeavesForManagerByStatus(@PathVariable String status) {
        String managerEmail = SecurityUtil.getCurrentUserEmail();
        List<LeaveRequestResponseDTO> leaveRequests = leaveServiceImpl.getLeaveRequestsForManagerOnLeaveStatus(managerEmail, status);
        return ResponseEntity.ok(leaveRequests);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/manager/update-leave")
    public ResponseEntity<String> updateLeaveStatus(@Valid @RequestBody UpdateLeaveRequest updateRequest) {
        Long managerId = SecurityUtil.getCurrentUserId();
        updateRequest.setManagerId(managerId);

        leaveServiceImpl.updateLeaveStatus(updateRequest);
        return ResponseEntity.ok("Leave request status updated successfully.");
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager/history")
    public ResponseEntity<List<LeaveRequestResponseDTO>> getLeaveHistoryForManager() {
        String managerEmail = SecurityUtil.getCurrentUserEmail();
        List<LeaveRequestResponseDTO> leaveRequests = leaveServiceImpl.getLeaveRequestHistoryForManager(managerEmail);
        return ResponseEntity.ok(leaveRequests);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager/balances")
    public ResponseEntity<List<LeaveBalanceResponseDTO>> getLeaveBalancesForManager() {
        String managerEmail = SecurityUtil.getCurrentUserEmail();
        List<LeaveBalanceResponseDTO> balances = leaveServiceImpl.getLeaveBalancesForManager(managerEmail);
        return ResponseEntity.ok(balances);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/balance/create/{userId}")
    public ResponseEntity<String> createLeaveBalance(@PathVariable Long userId) {
        leaveServiceImpl.createLeaveBalance(userId);
        return ResponseEntity.ok("Leave balance created for user: " + userId);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager/history/{userId}")
    public ResponseEntity<List<LeaveRequestResponseDTO>> getLeaveHistoryForUserUnderManager(@PathVariable Long userId) {
        String managerEmail = SecurityUtil.getCurrentUserEmail();
        List<LeaveRequestResponseDTO> history = leaveServiceImpl.getLeaveHistoryForUserUnderManager(managerEmail, userId);
        return ResponseEntity.ok(history);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager/balance/{userId}")
    public ResponseEntity<LeaveBalanceResponseDTO> getLeaveBalanceForUserUnderManager(@PathVariable Long userId) {
        String managerEmail = SecurityUtil.getCurrentUserEmail();
        LeaveBalanceResponseDTO balance = leaveServiceImpl.getLeaveBalanceForUserUnderManager(managerEmail, userId);
        return ResponseEntity.ok(balance);
    }

}
