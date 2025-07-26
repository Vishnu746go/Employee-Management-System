package com.EmployeeLeaveAndAttendanceMgmtSystem.security;

import com.EmployeeLeaveAndAttendanceMgmtSystem.enums.UserRole;
import com.EmployeeLeaveAndAttendanceMgmtSystem.utils.CustomUserPrincipal;
import com.EmployeeLeaveAndAttendanceMgmtSystem.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String token = getJwtFromRequest(request); // Get the JWT from request

        if (token != null && jwtUtil.validateToken(token)) { // Validate the token
            String email = jwtUtil.extractEmail(token); // Extract the username (email) from the token
            UserRole role = jwtUtil.extractRole(token); // Extract user roles or authorities from token
            Long userId = jwtUtil.extractUserId(token); // Extract User Id from token

            // Create the authentication token (no need for password or user details)
            var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));

            CustomUserPrincipal customUserPrincipal = new CustomUserPrincipal(userId, email);

            var authentication = new UsernamePasswordAuthenticationToken(
                    customUserPrincipal,  // this becomes the 'principal'
                    null,   // credentials (we don't need them after login)
                    authorities
            );

            // Set additional details from request
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set authentication in the SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }

    // Method to extract JWT token from the request's Authorization header
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Removing "Bearer " prefix
        }
        return null;
    }
}
