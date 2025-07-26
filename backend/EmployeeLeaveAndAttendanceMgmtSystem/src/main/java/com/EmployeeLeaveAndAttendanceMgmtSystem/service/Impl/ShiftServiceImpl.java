package com.EmployeeLeaveAndAttendanceMgmtSystem.service.Impl;

import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.CreateShiftSwapRequest;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.ShiftAssignmentRequest;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.ShiftSwapResponseDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.UpdateShiftSwapRequest;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.ShiftResponseDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.entity.Shift;
import com.EmployeeLeaveAndAttendanceMgmtSystem.entity.ShiftBalance;
import com.EmployeeLeaveAndAttendanceMgmtSystem.entity.ShiftSwapRequest;
import com.EmployeeLeaveAndAttendanceMgmtSystem.entity.User;
import com.EmployeeLeaveAndAttendanceMgmtSystem.enums.Status;
import com.EmployeeLeaveAndAttendanceMgmtSystem.exception.ConflictException;
import com.EmployeeLeaveAndAttendanceMgmtSystem.exception.InsufficientSwapBalanceException;
import com.EmployeeLeaveAndAttendanceMgmtSystem.exception.InvalidSwapRequestException;
import com.EmployeeLeaveAndAttendanceMgmtSystem.exception.ResourceNotFoundException;
import com.EmployeeLeaveAndAttendanceMgmtSystem.repository.ShiftBalanceRepository;
import com.EmployeeLeaveAndAttendanceMgmtSystem.repository.ShiftRepository;
import com.EmployeeLeaveAndAttendanceMgmtSystem.repository.ShiftSwapRequestRepository;
import com.EmployeeLeaveAndAttendanceMgmtSystem.repository.UserRepository;
import com.EmployeeLeaveAndAttendanceMgmtSystem.service.ShiftService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;

    private final UserRepository userRepository;

    private final ShiftBalanceRepository shiftBalanceRepository;

    private final ShiftSwapRequestRepository shiftSwapRequestRepository;

    public ShiftServiceImpl(ShiftRepository shiftRepository, UserRepository userRepository, ShiftBalanceRepository shiftBalanceRepository, ShiftSwapRequestRepository shiftSwapRequestRepository) {
        this.shiftRepository = shiftRepository;
        this.userRepository = userRepository;
        this.shiftBalanceRepository = shiftBalanceRepository;
        this.shiftSwapRequestRepository = shiftSwapRequestRepository;
    }

    private ShiftBalance getOrCreateShiftBalance(User user, YearMonth ym) {
        return shiftBalanceRepository.findByUserIdAndYearAndMonth(user.getId(), ym.getYear(), ym.getMonthValue())
                .orElseGet(() -> {
                    ShiftBalance balance = new ShiftBalance();
                    balance.setUser(user);
                    balance.setYear(ym.getYear());
                    balance.setMonth(ym.getMonthValue());
                    balance.setSwapCount(7);  // Default swap allowance per month
                    return shiftBalanceRepository.save(balance);
                });
    }

    @Override
    public void assignShift(ShiftAssignmentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new InvalidSwapRequestException("User not found"));

        if (shiftRepository.findByUserIdAndShiftDate(user.getId(), request.getDate()).isPresent()) {
            throw new InvalidSwapRequestException("Shift already assigned on this date for user");
        }

        YearMonth ym = YearMonth.from(request.getDate());
        getOrCreateShiftBalance(user, ym);

        Shift shift = new Shift();
        shift.setUser(user);
        shift.setShiftDate(request.getDate());
        shift.setShiftType(request.getShiftType());
        shift.setCreatedDate(LocalDateTime.now());

        shiftRepository.save(shift);
    }

    @Override
    public void assignWeeklyShifts(ShiftAssignmentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new InvalidSwapRequestException("User not found"));

        LocalDate monday = request.getDate();
        LocalDate friday = monday.plusDays(4);

        List<Shift> existingShifts = shiftRepository.findByUserIdAndShiftDateBetween(user.getId(), monday, friday);

        if(existingShifts.size()==5){
            throw new ConflictException("Shift is already assigned!");
        }

        Set<LocalDate> existingDates = existingShifts.stream()
                .map(Shift::getShiftDate)
                .collect(Collectors.toSet());

        Set<YearMonth> involvedMonths = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            involvedMonths.add(YearMonth.from(monday.plusDays(i)));
        }
        involvedMonths.forEach(ym -> getOrCreateShiftBalance(user, ym));

        for (int i = 0; i < 5; i++) {
            LocalDate date = monday.plusDays(i);
            if (!existingDates.contains(date)) {
                Shift shift = new Shift();
                shift.setUser(user);
                shift.setShiftDate(date);
                shift.setShiftType(request.getShiftType());
                shift.setCreatedDate(LocalDateTime.now());
                shiftRepository.save(shift);
            }
        }
    }

    @Override
    public ShiftResponseDTO getEmployeeShiftByDate(Long userId, LocalDate date) {
        return shiftRepository.findByUserIdAndShiftDate(userId, date)
                .map(Shift::getDto)
                .orElse(null);
    }

    @Override
    public int getSwapBalance(Long userId) {
        YearMonth now = YearMonth.now();
        return shiftBalanceRepository.findByUserIdAndYearAndMonth(userId, now.getYear(), now.getMonthValue())
                .map(ShiftBalance::getSwapCount)
                .orElse(0);
    }

    @Override
    public List<ShiftResponseDTO> getShiftsByDateForManager(Long managerId, LocalDate date) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new InvalidSwapRequestException("Manager not found"));

        List<User> subordinates = userRepository.findByManager(manager);
        if (subordinates.isEmpty()) {
            return Collections.emptyList();
        }

        return shiftRepository.findByUserInAndShiftDate(subordinates, date)
                .stream()
                .map(Shift::getDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createSwapRequest(CreateShiftSwapRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new InvalidSwapRequestException("User not found"));

        Shift currentShift = shiftRepository.findById(request.getShiftId())
                .orElseThrow(() -> new InvalidSwapRequestException("Current shift not found"));

        int year = currentShift.getShiftDate().getYear();
        int month = currentShift.getShiftDate().getMonthValue();

        ShiftBalance balance = shiftBalanceRepository.findByUserIdAndYearAndMonth(user.getId(), year, month)
                .orElseThrow(() -> new InvalidSwapRequestException("Shift balance record not found"));

        if (balance.getSwapCount() <= 0) {
            throw new InsufficientSwapBalanceException("No swap balance left");
        }

        if (shiftSwapRequestRepository.findByUserIdAndShiftDate(user.getId(), request.getShiftDate()).isPresent()) {
            throw new InvalidSwapRequestException("User already has shift on requested date");
        }

        if (!request.getShiftDate().isAfter(LocalDate.now())) {
            throw new InvalidSwapRequestException("Swap request must be at least 1 day in advance");
        }

        if(request.getShiftType() == currentShift.getShiftType()){
            throw new ConflictException("Swap request should not have same assigned shift type.");
        }

        ShiftSwapRequest swapRequest = new ShiftSwapRequest();
        swapRequest.setUser(user);
        swapRequest.setCreatedDate(LocalDateTime.now());
        swapRequest.setShift(currentShift);
        swapRequest.setShiftType(request.getShiftType());
        swapRequest.setShiftDate(request.getShiftDate());
        swapRequest.setStatus(Status.PENDING);

        shiftSwapRequestRepository.save(swapRequest);
    }

    @Override
    @Transactional
    public void updateSwapRequestStatus(UpdateShiftSwapRequest request) {
        ShiftSwapRequest swapRequest = shiftSwapRequestRepository.findById(request.getSwapId())
                .orElseThrow(() -> new InvalidSwapRequestException("Shift swap request not found"));

        if (request.getStatus() == Status.APPROVED) {
            Shift currentShift = swapRequest.getShift();
            YearMonth ym = YearMonth.from(currentShift.getShiftDate());

            ShiftBalance balance = shiftBalanceRepository.findByUserIdAndYearAndMonth(swapRequest.getUser().getId(), ym.getYear(), ym.getMonthValue())
                    .orElseThrow(() -> new InvalidSwapRequestException("Shift balance not found"));

            if (balance.getSwapCount() <= 0) {
                throw new InsufficientSwapBalanceException("No swap balance left");
            }

            balance.setSwapCount(balance.getSwapCount() - 1);
            shiftBalanceRepository.save(balance);

            currentShift.setShiftDate(swapRequest.getShiftDate());
            currentShift.setShiftType(swapRequest.getShiftType());
            shiftRepository.save(currentShift);

            swapRequest.setStatus(Status.APPROVED);
            shiftSwapRequestRepository.save(swapRequest);

        } else if (request.getStatus() == Status.REJECTED) {
            swapRequest.setStatus(Status.REJECTED);
            shiftSwapRequestRepository.save(swapRequest);
        } else {
            throw new InvalidSwapRequestException("Invalid status update");
        }
    }

    @Override
    public List<ShiftSwapResponseDTO> getSwapRequestsByUser(Long userId) {
        List<ShiftSwapRequest> requests = shiftSwapRequestRepository.findByUserId(userId);
        return requests.stream()
                .map(ShiftSwapRequest::getDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShiftSwapResponseDTO> getShiftSwapRequestsForManagerOnStatus(String managerEmail, String statusStr) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new InvalidSwapRequestException("Manager not found"));

        List<User> subordinates = userRepository.findByManager(manager);
        if (subordinates.isEmpty()) {
            return Collections.emptyList();
        }

        Status status;
        try {
            status = Status.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidSwapRequestException("Invalid status value");
        }

        List<ShiftSwapRequest> requests = shiftSwapRequestRepository.findByUserInAndStatus(subordinates, status);
        return requests.stream()
                .map(ShiftSwapRequest::getDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShiftSwapResponseDTO> getSwapRequestsForManager(Long managerId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new InvalidSwapRequestException("Manager not found"));

        List<User> subordinates = userRepository.findByManager(manager);
        if (subordinates.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> ids = new ArrayList<>();
        for (User user : subordinates){
            ids.add(user.getId());
        }

        List<ShiftSwapRequest> requests = shiftSwapRequestRepository.findByUserIdIn(ids);
        return requests.stream()
                .map(ShiftSwapRequest::getDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShiftResponseDTO> getShiftHistoryForUserUnderManager(String managerEmail, Long userId) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new InvalidSwapRequestException("Manager not found"));

        User employee = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidSwapRequestException("User not found"));

        if (!employee.getManager().getId().equals(manager.getId())) {
            throw new AccessDeniedException("This employee is not assigned to the current manager.");
        }

        List<Shift> assignedShifts = shiftRepository.findByUserId(userId);

        return assignedShifts.stream()
                .map(Shift::getDto)
                .collect(Collectors.toList());
    }


    @Override
    public int getSwapBalanceForUserUnderManager(String managerEmail, Long userId) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new InvalidSwapRequestException("Manager not found"));

        User employee = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidSwapRequestException("User not found"));

        if (!employee.getManager().getId().equals(manager.getId())) {
            throw new AccessDeniedException("This employee is not assigned to the current manager.");
        }

        YearMonth now = YearMonth.now();

        return shiftBalanceRepository.findByUserIdAndYearAndMonth(userId, now.getYear(), now.getMonthValue())
                .map(ShiftBalance::getSwapCount)
                .orElse(0);
    }



}