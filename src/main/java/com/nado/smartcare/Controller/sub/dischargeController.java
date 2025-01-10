package com.nado.smartcare.controller.sub;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/discharge")
public class DischargeController {
	
	@GetMapping("/discharge")
	public String dischargeForm() {
		return "discharge/discharge";
	}
}
