package com.EmployeeLeaveAndAttendanceMgmtSystem.repository;

import com.EmployeeLeaveAndAttendanceMgmtSystem.entity.ShiftSwapRequest;
import com.EmployeeLeaveAndAttendanceMgmtSystem.entity.User;
import com.EmployeeLeaveAndAttendanceMgmtSystem.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShiftSwapRequestRepository extends JpaRepository<ShiftSwapRequest, Long> {
    List<ShiftSwapRequest> findByUserId(Long userId);
    List<ShiftSwapRequest> findByUserIdAndStatus(Long userId, Status status);

    List<ShiftSwapRequest> findByUserInAndStatus(List<User> subordinates, Status swapStatus);

    List<ShiftSwapRequest> findByUserIdIn(List<Long> userIds);

    // New method to get shift swap requests by month
    @Query("SELECT sr FROM ShiftSwapRequest sr WHERE sr.user.id = :userId AND YEAR(sr.shiftDate) = :year AND MONTH(sr.shiftDate) = :month")
    List<ShiftSwapRequest> findByUserIdAndMonth(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);


    Optional<Object> findByUserIdAndShiftDate(Long id, LocalDate shiftDate);
}
