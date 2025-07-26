package com.EmployeeLeaveAndAttendanceMgmtSystem.service;

import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.UserRequestDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.UserUpdateRequestDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.UserResponseDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.service.Impl.UserServiceImpl;

import java.util.List;

public interface UserService {

    String addUser(UserRequestDTO userInfo);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getUserById(Long id);

    List<UserResponseDTO> getAllManagers();

    List<UserResponseDTO> getAllEmployees();

    void assignEmployeesToManager(Long managerId, List<Long> employeeIds);

    List<UserResponseDTO> getAssignedEmployees(Long managerId);

    String updateUser(Long userId, UserUpdateRequestDTO userUpdateRequestDTO);

    void deleteUser(Long id);
}
