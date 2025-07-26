package com.EmployeeLeaveAndAttendanceMgmtSystem.controller;

import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.AuthRequest;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.requestDTO.UserRequestDTO;
import com.EmployeeLeaveAndAttendanceMgmtSystem.dto.responseDTO.AuthResponse;
import com.EmployeeLeaveAndAttendanceMgmtSystem.entity.User;
import com.EmployeeLeaveAndAttendanceMgmtSystem.repository.UserRepository;
import com.EmployeeLeaveAndAttendanceMgmtSystem.service.UserService;
import com.EmployeeLeaveAndAttendanceMgmtSystem.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/")
public class AuthController {

    private final UserService service;

    private final JwtUtil jwtService;

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    @Autowired
    public AuthController(UserService service, JwtUtil jwtService, AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.service = service;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public AuthResponse authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );
        if (authentication.isAuthenticated()) {
            User user = userRepository.findByEmail(authRequest.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            return new AuthResponse(jwtService.generateToken(authRequest.getEmail(), user.getUserRole().name(), user.getId()), user.getUserRole().name());
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }
}

