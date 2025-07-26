package com.EmployeeLeaveAndAttendanceMgmtSystem.entity;

import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.ShiftSwapResponseDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.enums.ShiftType;
import com.EmployeeLeaveAndAttendanceMgmtSystem.enums.Status;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class ShiftSwapRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  // changed from OneToOne to ManyToOne
    @JoinColumn(name = "shift_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Shift shift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(nullable = false)
    private LocalDate shiftDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShiftType shiftType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    private LocalDateTime createdDate;

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public ShiftSwapResponseDTO getDto(){
        ShiftSwapResponseDTO dto = new ShiftSwapResponseDTO();

        dto.setId(id);
        dto.setShiftId(shift.getDto().getId());
        dto.setUserId(user.getId());
        dto.setShiftType(shiftType);
        dto.setShiftDate(shiftDate);
        dto.setStatus(status);
        dto.setCreatedDate(createdDate);

        return dto;
    }
}