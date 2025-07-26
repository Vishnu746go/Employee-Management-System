package com.EmployeeLeaveAndAttendanceMgmtSystem.controller;

import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.AssignEmployeeRequest;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.UserRequestDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.UserUpdateRequestDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.UserResponseDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.service.UserService;
import com.EmployeeLeaveAndAttendanceMgmtSystem.utils.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;
    private UserService userService;

    @BeforeEach
    public void setup() {
        userService = mock(UserService.class);
        UserController userController = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    //  Test Get All Users
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllUsers() throws Exception {
        List<UserResponseDTO> mockResponse = Collections.singletonList(new UserResponseDTO());
        doReturn(mockResponse).when(userService).getAllUsers();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/allUsers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    //  Test Get All Managers
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllManagers() throws Exception {
        List<UserResponseDTO> mockResponse = Collections.singletonList(new UserResponseDTO());
        doReturn(mockResponse).when(userService).getAllManagers();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/allManagers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    //  Test Get All Employees
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllEmployees() throws Exception {
        List<UserResponseDTO> mockResponse = Collections.singletonList(new UserResponseDTO());
        doReturn(mockResponse).when(userService).getAllEmployees();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/allEmployees")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    //  Test Delete User
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(any(Long.class));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));
    }

    //  Test Assign Employees to Manager
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAssignEmployeesToManager() throws Exception {
        doNothing().when(userService).assignEmployeesToManager(any(Long.class), any(List.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/assign-employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"managerId\": 1, \"employeeIds\": [2, 3, 4] }"))
                .andExpect(status().isOk())
                .andExpect(content().string("Employees assigned to manager successfully"));
    }

    // Test Get Assigned Employees (Admin)
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAssignedEmployees() throws Exception {
        List<UserResponseDTO> mockResponse = Collections.singletonList(new UserResponseDTO());
        doReturn(mockResponse).when(userService).getAssignedEmployees(any(Long.class));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/assigned-employees/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


}
