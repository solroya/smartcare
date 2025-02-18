package com.nado.smartcare.config;


import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.member.domain.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private String username; // memberId
    private String password;
    private String name;
    private Collection<? extends GrantedAuthority> authorities;

    // Member용 생성자
    public CustomUserDetails(Member member, Collection<? extends GrantedAuthority> authorities) {
        this.username = member.getMemberId();
        this.password = member.getMemberPass();
        this.name = member.getMemberName();
        this.authorities = authorities;
    }

    // Employee용 생성자
    public CustomUserDetails(Employee employee, Collection<? extends GrantedAuthority> authorities) {
        this.username = employee.getEmployeeId();
        this.password = employee.getEmployeePass();
        this.name = employee.getEmployeeName();
        this.authorities = authorities;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;  // 생성자에서 설정된 권한을 반환
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getMemberName() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
