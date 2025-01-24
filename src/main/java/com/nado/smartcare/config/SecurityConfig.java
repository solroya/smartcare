package com.nado.smartcare.config;

import com.nado.smartcare.employee.domain.type.EmployeeStatus;
import com.nado.smartcare.employee.repository.EmployeeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Configuration
@EnableWebSecurity
@Log4j2
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filter(HttpSecurity http, EmployeeRepository employeeRepository) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        // 정적 리로스 및 특정 경로는 접근 가능
                        .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()
                        .requestMatchers("/member/**", "/sms/**", "redis/**").permitAll()
                        .requestMatchers("/employee/**").permitAll()
                        .requestMatchers("/api/check-login-status").permitAll()
                        .requestMatchers("/oauth2/authorization/kakao").permitAll()
                        .requestMatchers("/auth/kakao/**").permitAll()
                        // TODO: 개발 편의를 위해 일단 모두 허용 함
           /*             .requestMatchers("/erp/**").access((authentication, object) -> {
                            // 인증된 사용자 정보(Approved) 만 접속 가능
                            Object principal = authentication.get().getPrincipal();
                            if (principal instanceof User user) {
                                String userId = user.getUsername();
                                // EmployeeStatus 가 Approved 체크
                                boolean isApproved = employeeRepository.findByEmployeeId(userId)
                                        .map(employee -> employee.getEmployeeStatus() == EmployeeStatus.APPROVED)
                                        .orElse(false);
                                return new AuthorizationDecision(isApproved);
                            }
                            return new AuthorizationDecision(false);
                        })*/
//                        .anyRequest().authenticated()
                        .anyRequest().permitAll()

                )
                .formLogin((form) -> form
                        .loginPage("/member/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/main", true)
                        .failureUrl("/member/login?error")
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/member/login")
                        .successHandler((request, response, authentication) -> {
                            if (authentication instanceof OAuth2AuthenticationToken token) {
                                response.sendRedirect("/auth/kakao/callback");
                            } else {
                                response.sendRedirect("/member/login");
                            }
                        })
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable())
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/main")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                // Floating Action Button 의 Same-Origin Policy 문제 패스
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions
                                .sameOrigin()));

        return http.build();
    }


}
