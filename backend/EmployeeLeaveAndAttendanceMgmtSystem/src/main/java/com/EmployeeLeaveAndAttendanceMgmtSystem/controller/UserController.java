package com.EmployeeLeaveAndAttendanceMgmtSystem.controller;

import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.AssignEmployeeRequest;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.UserRequestDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.UserUpdateRequestDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.UserResponseDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.service.UserService;
import com.EmployeeLeaveAndAttendanceMgmtSystem.utils.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // To create a user(EMPLOYEE OR MANAGER)
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        userService.addUser(userRequestDTO);
        return ResponseEntity.ok("User created successfully");
    }

    // To get all users
    @GetMapping("/allUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    // To get all Managers
    @GetMapping("/allManagers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllManagers() {
        return ResponseEntity.ok(userService.getAllManagers());
    }

    // To get all Employees
    @GetMapping("/allEmployees")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllEmployees() {
        return ResponseEntity.ok(userService.getAllEmployees());
    }

    // To get current user details
    @GetMapping("/current")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public UserResponseDTO getCurrentUser() {
        Long userId = SecurityUtil.getCurrentUserId();
        return userService.getUserById(userId);
    }

    // To update the details of the current user
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<String> updateUser(@Valid @RequestBody UserUpdateRequestDTO userUpdateRequestDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        String response = userService.updateUser(userId, userUpdateRequestDTO);
        if (response.equals("User not found")) {
            return ResponseEntity.badRequest().body("Update failed: User not found");
        }
        return ResponseEntity.ok(response);
    }

    // To delete the user
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    // To assign employees under a manager
    @PostMapping("/assign-employees")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignEmployeesToManager(@Valid @RequestBody AssignEmployeeRequest assignEmployeeRequest) {
        userService.assignEmployeesToManager(assignEmployeeRequest.getManagerId(), assignEmployeeRequest.getEmployeeIds());
        return ResponseEntity.ok("Employees assigned to manager successfully");
    }

    // To get all employees assigned to a manager for Admin purpose
    @GetMapping("/assigned-employees/{managerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseDTO> getAssignedEmployees(@PathVariable Long managerId) {
        return userService.getAssignedEmployees(managerId);
    }

    // To get all employees assigned to the current manager
    @GetMapping("/assigned-employees")
    @PreAuthorize("hasRole('MANAGER')")
    public List<UserResponseDTO> getAssignedEmployeesForManager() {
        Long managerId = SecurityUtil.getCurrentUserId();
        return userService.getAssignedEmployees(managerId);
    }
}
