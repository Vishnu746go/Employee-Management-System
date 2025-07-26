package com.EmployeeLeaveAndAttendanceMgmtSystem.repository;

import com.EmployeeLeaveAndAttendanceMgmtSystem.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
    Optional<LeaveBalance> findByUserId(Long userId);

    List<LeaveBalance> findByUserIdIn(List<Long> userIds);

}

