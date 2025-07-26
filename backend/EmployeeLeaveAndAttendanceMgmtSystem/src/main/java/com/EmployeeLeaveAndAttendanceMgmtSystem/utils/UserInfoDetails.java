package com.EmployeeLeaveAndAttendanceMgmtSystem.utils;

import com.EmployeeLeaveAndAttendanceMgmtSystem.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserInfoDetails implements UserDetails {

    private String username;
    private String password;
    private String authority;

    public UserInfoDetails(User userInfo) {
        this.username = userInfo.getEmail(); // Using email as username
        this.password = userInfo.getPassword();
        this.authority = userInfo.getUserRole().name();
    }

    public String getAuthority() {
        return authority;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + authority));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}


