package com.nado.smartcare.config.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.employee.repository.EmployeeRepository;
import com.nado.smartcare.member.domain.Member;
import com.nado.smartcare.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService{
	
	private final MemberRepository memberRepository;
	private final EmployeeRepository employeeRepository;
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	    log.info("Attempting to authenticate user: {}", username);

	    Optional<Member> member = memberRepository.findByMemberId(username);
	    if (member.isPresent()) {
	        log.info("Found member: {}", member.get());
	        return User.builder()
	                .username(member.get().getMemberId())
	                .password(member.get().getMemberPass())
	                .roles("MEMBER")
	                .build();
	    }

	    Optional<Employee> employee = employeeRepository.findByEmployeeId(username);
	    if (employee.isPresent()) {
	        log.info("Found employee: {}", employee.get());
	        return User.builder()
	                .username(employee.get().getEmployeeId())
	                .password(employee.get().getEmployeePass())
	                .roles("EMPLOYEE")
	                .build();
	    }

	    log.warn("User not found with username: {}", username);
	    throw new UsernameNotFoundException("User not found with username: " + username);
	}
	
}
