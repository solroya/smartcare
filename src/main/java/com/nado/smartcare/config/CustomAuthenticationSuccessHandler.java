package com.nado.smartcare.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Log4j2
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 현재 로그인한 사용자 정보 가져오기
        User user = (User) authentication.getPrincipal();
        String username = user.getUsername();

        // 사용자의 역할(Role) 확인
        boolean isEmployee = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_EMPLOYEE"));

        if (isEmployee) {
            log.info("직원 로그인 : {}, ERP로 이동", username);
            response.sendRedirect("/erp");
        } else {
            log.info("일반 사용자 로그인 : {}, 메인 페이지로 이동", username);
            response.sendRedirect("/main");
        }
    }
}
