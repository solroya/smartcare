package com.nado.smartcare.health.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/health")
public class healthController {
	
	@GetMapping("/health")
	public String healthForm() {
		return "health/health";
	}
}
