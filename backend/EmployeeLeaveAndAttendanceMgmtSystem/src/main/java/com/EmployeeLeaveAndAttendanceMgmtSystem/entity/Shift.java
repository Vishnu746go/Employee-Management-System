package com.EmployeeLeaveAndAttendanceMgmtSystem.entity;

import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.ShiftResponseDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.enums.ShiftType;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(nullable = false)
    private LocalDate shiftDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShiftType shiftType;

    private LocalDateTime createdDate;

    @OneToMany(mappedBy = "shift", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ShiftSwapRequest> shiftSwapRequests;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(LocalDate shiftDate) {
        this.shiftDate = shiftDate;
    }

    public ShiftType getShiftType() {
        return shiftType;
    }

    public void setShiftType(ShiftType shiftType) {
        this.shiftType = shiftType;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public ShiftResponseDTO getDto(){
        ShiftResponseDTO dto = new ShiftResponseDTO();

        dto.setId(id);
        dto.setUserId(user.getId());
        dto.setShiftType(shiftType);
        dto.setShiftDate(shiftDate);

        return dto;
    }
}
