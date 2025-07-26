package com.EmployeeLeaveAndAttendanceMgmtSystem.entity;

import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.UserResponseDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.enums.UserRole;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<LeaveRequest> leaveRequests;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Attendance> attendanceList;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private LeaveBalance leaveBalance;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Shift> shifts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ShiftBalance> shiftBalances;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ShiftSwapRequest> swapRequests;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    //converting entity to dto for response purposes
    public UserResponseDTO getDTO() {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(this.getId());
        dto.setName(this.getName());
        dto.setEmail(this.getEmail());
        dto.setUserRole(this.getUserRole());

        if (this.getManager() != null) {
            UserResponseDTO.ManagerInfo managerInfo = new UserResponseDTO.ManagerInfo();
            managerInfo.setId(this.getManager().getId());
            managerInfo.setName(this.getManager().getName());
            managerInfo.setEmail(this.getManager().getEmail());
            dto.setManager(managerInfo);
        } else {
            dto.setManager(null);
        }

        return dto;
    }

}
