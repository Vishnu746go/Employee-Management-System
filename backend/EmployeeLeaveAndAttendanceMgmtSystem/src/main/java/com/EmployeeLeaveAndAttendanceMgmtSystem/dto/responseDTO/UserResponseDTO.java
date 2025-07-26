package com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO;

import com.EmployeeLeaveAndAttendanceMgmtSystem.enums.UserRole;

public class UserResponseDTO {

    private Long id;

    private String name;

    private String email;

    private UserRole userRole;

    private ManagerInfo manager;

    // ManagerInfo inner class
    public static class ManagerInfo {
        private Long id;
        private String name;
        private String email;

        public Long getId() {
            return id;
        }
        public void setId(Long id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public ManagerInfo getManager() {
        return manager;
    }

    public void setManager(ManagerInfo manager) {
        this.manager = manager;
    }
}
