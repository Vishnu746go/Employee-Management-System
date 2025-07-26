package com.EmployeeLeaveAndAttendanceMgmtSystem.service.Impl;

import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.AttendanceResponseDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.AttendanceRequest;
import com.EmployeeLeaveAndAttendanceMgmtSystem.entity.Attendance;
import com.EmployeeLeaveAndAttendanceMgmtSystem.entity.User;
import com.EmployeeLeaveAndAttendanceMgmtSystem.enums.AttendanceStatus;
import com.EmployeeLeaveAndAttendanceMgmtSystem.exception.ConflictException;
import com.EmployeeLeaveAndAttendanceMgmtSystem.exception.ResourceNotFoundException;
import com.EmployeeLeaveAndAttendanceMgmtSystem.repository.AttendanceRepository;
import com.EmployeeLeaveAndAttendanceMgmtSystem.repository.UserRepository;
import com.EmployeeLeaveAndAttendanceMgmtSystem.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;

    private final UserRepository userRepository;

    @Autowired
    public AttendanceServiceImpl(AttendanceRepository attendanceRepository, UserRepository userRepository){
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void clockIn(AttendanceRequest request) {
        Optional<Attendance> existingAttendance = attendanceRepository.findByUserIdAndDate(request.getUserId(), LocalDate.now());
        if(existingAttendance.isPresent()){
            throw new ConflictException("Employee already clocked in today!");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setDate(LocalDate.now());
        attendance.setClockInTime(request.getTime());
        attendance.setStatus(AttendanceStatus.PRESENT);

        attendanceRepository.save(attendance);
    }

    @Override
    public void clockOut(AttendanceRequest request) {
        LocalDate today = LocalDate.now();

        Optional<Attendance> optionalAttendance = attendanceRepository.findByUserIdAndDate(request.getUserId(), today);
        if (optionalAttendance.isPresent()) {
            Attendance attendance = optionalAttendance.get();

            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));


            if(request.getTime().isBefore(attendance.getClockInTime())) {
                throw new ConflictException("Clock-out time cannot be before clock-in time");
            }

            attendance.setClockOutTime(request.getTime());
            Duration duration = Duration.between(attendance.getClockInTime(), request.getTime());
            double hoursWorked = duration.toMinutes() / 60.0;
            attendance.setWorkHours(hoursWorked);
            attendanceRepository.save(attendance);
        } else {
            throw new ResourceNotFoundException("Clock in is not yet completed for today");
        }
    }

    public AttendanceResponseDTO getTodayAttendance(Long userId) {
        LocalDate today = LocalDate.now();
        Optional<Attendance> attendance = attendanceRepository.findByUserIdAndDate(userId, today);

        if (attendance.isPresent()) {
            return attendance.get().getDTO();
        }
        return new AttendanceResponseDTO();
    }


    @Override
    public List<AttendanceResponseDTO> getAttendanceHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Attendance> attendances = attendanceRepository.findByUserId(userId);
        return attendances.stream().map(Attendance::getDTO).collect(Collectors.toList());
    }

    @Override
    public List<AttendanceResponseDTO> getAttendanceHistoryForManager(String managerEmail) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        List<User> users = userRepository.findByManager(manager);

        List<Long> employeeIds = users.stream()
                .map(User::getId)
                .collect(Collectors.toList());

        List<Attendance> allAttendances = attendanceRepository.findByUserIdIn(employeeIds);

        return allAttendances.stream()
                .map(Attendance::getDTO)
                .collect(Collectors.toList());
    }

}
