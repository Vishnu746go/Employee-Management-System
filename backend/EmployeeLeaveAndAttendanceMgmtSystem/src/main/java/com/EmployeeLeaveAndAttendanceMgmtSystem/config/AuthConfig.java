package com.EmployeeLeaveAndAttendanceMgmtSystem.config;

import com.EmployeeLeaveAndAttendanceMgmtSystem.entity.User;
import com.EmployeeLeaveAndAttendanceMgmtSystem.enums.UserRole;
import com.EmployeeLeaveAndAttendanceMgmtSystem.repository.UserRepository;
import com.EmployeeLeaveAndAttendanceMgmtSystem.service.Impl.UserServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserServiceImpl userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public CommandLineRunner initAdmin(UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (!repo.existsByUserRole(UserRole.ADMIN)) {
                User admin = new User();
                admin.setEmail("admin@gmail.com");
                admin.setName("Admin");
                admin.setPassword(encoder.encode("admin"));
                admin.setUserRole(UserRole.ADMIN);
                repo.save(admin);
                System.out.println("Default admin user created");
            } else {
                System.out.println("Admin user already exists");
            }
        };
    }

}
