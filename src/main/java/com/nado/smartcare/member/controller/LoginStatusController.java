package com.nado.smartcare.member.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class LoginStatusController {
	
	@GetMapping("/check-login-status")
	public Map<String, Boolean> checkLoginStatus(HttpSession session) {
		boolean isLoggedIn = session.getAttribute("member") != null;
		return Collections.singletonMap("isLoggedIn", isLoggedIn);
	}
	
}
