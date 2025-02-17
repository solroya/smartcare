package com.nado.smartcare.config;

import com.nado.smartcare.config.service.CustomUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@Log4j2
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailService userDetailsService;

    // 비밀번호 인코더(BCrypt 알고리즘 설정)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 인증 성공 핸들러(로그인 성공시 추가 처리할 수 있도록 커스텀 핸들러 등록)
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    SecurityFilterChain filter(HttpSecurity http) throws Exception {
        http
                .userDetailsService(userDetailsService) // 커스텀 유저 서비스 설정
                .authorizeHttpRequests((request) -> request
                        // URL별 접근 권한 설정
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/assets/**").permitAll()
                        .requestMatchers("/main", "/member/**").permitAll()
                        .requestMatchers("/company/**").permitAll()
                        .requestMatchers("/departments/**").permitAll()
                        .requestMatchers("/discharge/**").permitAll()
                        .requestMatchers("/customer/**").permitAll()
                        .requestMatchers("/notice/**").permitAll()
                        .requestMatchers("/event/**").permitAll()
                        .requestMatchers("/employee/**").permitAll()
                        .requestMatchers("/api/check-login-status").permitAll()
                        .requestMatchers("/oauth2/authorization/kakao").permitAll()
                        .requestMatchers("/auth/kakao/**").permitAll()
                        .requestMatchers("/redis/test", "/sms/**").permitAll()
                        .requestMatchers("/erp/**").hasRole("EMPLOYEE") // ERP는 직원만 접근 가능
                        .anyRequest().authenticated() // 그외는 인증된 사용자만
                )
                .formLogin((form) -> form
                        .loginPage("/member/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler(authenticationSuccessHandler())
                        .failureUrl("/member/login?error")
                        .permitAll()
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
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/main")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                );

        return http.build();
    }
}
