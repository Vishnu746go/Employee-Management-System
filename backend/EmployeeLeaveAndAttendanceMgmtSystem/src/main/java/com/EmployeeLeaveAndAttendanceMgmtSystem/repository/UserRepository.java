package com.EmployeeLeaveAndAttendanceMgmtSystem.repository;

import com.EmployeeLeaveAndAttendanceMgmtSystem.entity.User;
import com.EmployeeLeaveAndAttendanceMgmtSystem.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find users by their role
    List<User> findByUserRole(UserRole userRole);

    // Find users who report to a specific manager
    List<User> findByManager(User manager);

    // Find a user by email (used as username for login)
    Optional<User> findByEmail(String email);

    // Find all users by list of IDs
    List<User> findAllById(Iterable<Long> ids);

    boolean existsByUserRole(UserRole userRole);
}
