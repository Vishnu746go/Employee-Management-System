package com.EmployeeLeaveAndAttendanceMgmtSystem.controller;

import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.AttendanceRequest;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.AttendanceResponseDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.service.Impl.AttendanceServiceImpl;
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

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AttendanceControllerTest {

    private MockMvc mockMvc;
    private AttendanceController attendanceController;
    private AttendanceServiceImpl attendanceServiceImpl;

    @BeforeEach
    public void setup() {
        attendanceServiceImpl = Mockito.mock(AttendanceServiceImpl.class);
        attendanceController = new AttendanceController(attendanceServiceImpl);
        mockMvc = MockMvcBuilders.standaloneSetup(attendanceController).build();
    }

    // Test Clock In
    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testClockIn() throws Exception {
        Mockito.doNothing().when(attendanceServiceImpl).clockIn(any(AttendanceRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/attendance/clock-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"time\": \"08:00:00\" }"))
                .andExpect(status().isOk())
                .andExpect(content().string("Clock In saved successfully"));
    }

    // Test Clock Out
    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testClockOut() throws Exception {
        Mockito.doNothing().when(attendanceServiceImpl).clockOut(any(AttendanceRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/attendance/clock-out")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"time\": \"17:00:00\" }"))
                .andExpect(status().isOk())
                .andExpect(content().string("Clock Out saved successfully"));
    }

    // Test Get Attendance History by User ID (Manager Access)
    @Test
    @WithMockUser(roles = "MANAGER")
    public void testGetAttendanceHistory() throws Exception {
        List<AttendanceResponseDTO> mockResponse = Collections.singletonList(new AttendanceResponseDTO());
        when(attendanceServiceImpl.getAttendanceHistory(any(Long.class))).thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/attendance/attendance-history/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // Test Get Attendance History for Current User (Employee Access)
    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testGetAttendanceHistoryForCurrentUser() throws Exception {
        List<AttendanceResponseDTO> mockResponse = Collections.singletonList(new AttendanceResponseDTO());

        // Fix: Use `doReturn().when()` to avoid argument mismatch issues
        Long userId = SecurityUtil.getCurrentUserId(); // Ensure user ID is obtained
        Mockito.doReturn(mockResponse).when(attendanceServiceImpl).getAttendanceHistory(userId);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/attendance/attendance-history/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    // Test Get Attendance History for Manager
    @Test
    @WithMockUser(roles = "MANAGER")
    public void testGetAttendanceHistoryForManager() throws Exception {
        List<AttendanceResponseDTO> mockResponse = Collections.singletonList(new AttendanceResponseDTO());

        // Mock SecurityUtil to return a non-null manager email
        String managerEmail = "test.manager@example.com"; // Ensure this is not null
        Mockito.mockStatic(SecurityUtil.class);
        Mockito.when(SecurityUtil.getCurrentUserEmail()).thenReturn(managerEmail);

        // Ensure Mockito allows any string argument for stubbing
        Mockito.doReturn(mockResponse).when(attendanceServiceImpl).getAttendanceHistoryForManager(any(String.class));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/attendance/manager-history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }




}
