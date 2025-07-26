package com.EmployeeLeaveAndAttendanceMgmtSystem.service.Impl;

import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.UserRequestDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.UserUpdateRequestDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.UserResponseDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.entity.User;
import com.EmployeeLeaveAndAttendanceMgmtSystem.enums.UserRole;
import com.EmployeeLeaveAndAttendanceMgmtSystem.exception.ConflictException;
import com.EmployeeLeaveAndAttendanceMgmtSystem.exception.ResourceNotFoundException;
import com.EmployeeLeaveAndAttendanceMgmtSystem.repository.UserRepository;
import com.EmployeeLeaveAndAttendanceMgmtSystem.service.UserService;
import com.EmployeeLeaveAndAttendanceMgmtSystem.utils.UserInfoDetails;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final LeaveServiceImpl leaveService;

    @Autowired
    public UserServiceImpl(UserRepository repository, PasswordEncoder encoder,  LeaveServiceImpl leaveService) {
        this.repository = repository;
        this.encoder = encoder;
        this.leaveService = leaveService;
    }

    // implemented from userDetailService for Authentication purpose
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userDetail = repository.findByEmail(username); // email as username
        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Override
    @Transactional // Adding user to the database
    public String addUser(UserRequestDTO userInfo) {

        if (repository.findByEmail(userInfo.getEmail()).isPresent()) {
            throw new ConflictException("Email already exists."); // Prevent duplicate emails
        }

        User user = new User();
        user.setName(userInfo.getName());
        user.setEmail(userInfo.getEmail());
        user.setPassword(encoder.encode(userInfo.getPassword())); // Encode password before saving
        user.setUserRole(userInfo.getUserRole());

        if (userInfo.getManagerId() != null) {
            // Added null check for manager id to avoid NullPointerException
            User manager = repository.findById(userInfo.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
            user.setManager(manager);
        }

        repository.save(user);

        if(userInfo.getUserRole().equals(UserRole.EMPLOYEE)) {
            leaveService.createLeaveBalance(repository.findByEmail(userInfo.getEmail()).get().getId());
        }

        return "User Added Successfully";
    }

    // To get all users
    @Override
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = repository.findAll();
        return users.stream().map(User::getDTO).collect(Collectors.toList());
    }

    // To get a single user by id
    @Override
    public UserResponseDTO getUserById(Long id) {
        return repository.findById(id)
                .map(User::getDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    // To get all managers
    @Override
    public List<UserResponseDTO> getAllManagers() {
        return repository.findByUserRole(UserRole.MANAGER)
                .stream().map(User::getDTO).collect(Collectors.toList());
    }

    // To get all employees
    @Override
    public List<UserResponseDTO> getAllEmployees() {
        return repository.findByUserRole(UserRole.EMPLOYEE)
                .stream().map(User::getDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional  // To assign employees to manager
    public void assignEmployeesToManager(Long managerId, List<Long> employeeIds) {
        // checking whether manager is present or not
        User manager = repository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found with id " + managerId));

        // fetch all employees
        List<User> employeesToUpdate = repository.findAllById(employeeIds);

        // Check if all employee IDs are valid by comparing size
        if (employeesToUpdate.size() != employeeIds.size()) {
            // Find which IDs are missing
            List<Long> foundIds = employeesToUpdate.stream().map(User::getId).toList();
            List<Long> missingIds = employeeIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new ResourceNotFoundException("Employees not found with ids " + missingIds);
        }

        // Assign manager to all employees
        employeesToUpdate.forEach(employee -> employee.setManager(manager));
        repository.saveAll(employeesToUpdate);
    }

    @Override
    public List<UserResponseDTO> getAssignedEmployees(Long managerId) {
        // Checking managerId is valid or not
        User manager = repository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found with id " + managerId));
        List<User> employees = repository.findByManager(manager);
        return employees.stream().map(User::getDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String updateUser(Long userId, UserUpdateRequestDTO userUpdateRequestDTO) {
        // Checking userId is valid or not
        User user = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Checking whether there is a account with same emailId
        if (!user.getEmail().equals(userUpdateRequestDTO.getEmail())) {
            if (repository.findByEmail(userUpdateRequestDTO.getEmail()).isPresent()) {
                throw new ConflictException("Email already exists.");
            }
            user.setEmail(userUpdateRequestDTO.getEmail());
        }

        // Checking the name
        if (userUpdateRequestDTO.getName() != null && !user.getName().trim().equals(userUpdateRequestDTO.getName().trim())) {
            user.setName(userUpdateRequestDTO.getName().trim());
        }

        // Checking the password
        if (userUpdateRequestDTO.getPassword() != null && !userUpdateRequestDTO.getPassword().isEmpty()) {
            user.setPassword(encoder.encode(userUpdateRequestDTO.getPassword())); // Encode new password if provided
        }

        // Safe manager assignment with null checks
        if (userUpdateRequestDTO.getManagerId() != null) {
            User manager = repository.findById(userUpdateRequestDTO.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
            user.setManager(manager);
        } else {
            user.setManager(null);  // Remove manager if null passed
        }

        repository.save(user);
        return "User updated successfully";
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        // Only clear manager references if this user is a manager
        if (user.getUserRole() == UserRole.MANAGER) {
            List<User> subordinates = repository.findByManager(user);
            for (User subordinate : subordinates) {
                subordinate.setManager(null);
            }
            repository.saveAll(subordinates);
        }

        repository.delete(user);
    }
}
