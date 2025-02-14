package com.nado.smartcare.member.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class LoginStatusController {
	
	@GetMapping("/check-login-status")
	public Map<String, Object> checkLoginStatus() {
		// 현재 사용자 인증 정보 가져오기
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		// 로그인 상태 확인
	    boolean isLoggedIn = authentication != null && authentication.isAuthenticated() &&
	                         !(authentication instanceof AnonymousAuthenticationToken);

	    Map<String, Object> response = new HashMap<>();
	    response.put("isLoggedIn", isLoggedIn);

		/* 결과값 예시
		* {
		* 	"isLoggedIn" : true,
		* 	"username":"member",
		* 	"roles":[{"authority":"ROLE_MEMBER"}],
		* }
		* */
	    if (isLoggedIn) {
	        response.put("username", authentication.getName());
	        response.put("roles", authentication.getAuthorities());
	    }

	    return response;
	}
	
}
