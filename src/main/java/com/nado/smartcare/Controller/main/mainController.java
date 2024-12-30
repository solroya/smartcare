package com.nado.smartcare.Controller.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class mainController {

	@GetMapping("main")
	public String mainForm() {
		return "main";
	}
	
}

