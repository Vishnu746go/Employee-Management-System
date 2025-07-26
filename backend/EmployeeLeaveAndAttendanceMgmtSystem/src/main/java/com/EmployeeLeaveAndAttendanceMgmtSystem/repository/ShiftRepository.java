package com.EmployeeLeaveAndAttendanceMgmtSystem.repository;

import com.EmployeeLeaveAndAttendanceMgmtSystem.entity.Shift;
import com.EmployeeLeaveAndAttendanceMgmtSystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    Optional<Shift> findByUserIdAndShiftDate(Long userId, LocalDate shiftDate);
    List<Shift> findByUserIdAndShiftDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    List<Shift> findByUserInAndShiftDate(List<User> users, LocalDate date);

    // New method to get shifts by month
    @Query("SELECT s FROM Shift s WHERE s.user.id = :userId AND YEAR(s.shiftDate) = :year AND MONTH(s.shiftDate) = :month")
    List<Shift> findByUserIdAndMonth(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);

    List<Shift> findByUserId(Long userId);

}
