package com.nado.smartcare.Controller.sub;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/discharge")
public class dischargeController {
	
	@GetMapping("/discharge")
	public String dischargeForm() {
		return "discharge/discharge";
	}
}
