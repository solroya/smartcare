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
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    boolean isLoggedIn = authentication != null && authentication.isAuthenticated() &&
	                         !(authentication instanceof AnonymousAuthenticationToken);

	    Map<String, Object> response = new HashMap<>();
	    response.put("isLoggedIn", isLoggedIn);

	    if (isLoggedIn) {
	        response.put("username", authentication.getName());
	        response.put("roles", authentication.getAuthorities());
	    }

	    return response;
	}
	
}
