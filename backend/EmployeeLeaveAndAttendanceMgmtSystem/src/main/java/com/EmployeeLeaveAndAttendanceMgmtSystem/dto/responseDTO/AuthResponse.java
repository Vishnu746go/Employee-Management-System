package com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO;


public class AuthResponse {
    private String token;
    private String userRole;

    public AuthResponse(String token, String userRole) {
        this.token = token;
        this.userRole = userRole;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
