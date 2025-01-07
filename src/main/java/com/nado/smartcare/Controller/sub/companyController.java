package com.nado.smartcare.Controller.sub;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/company")
public class companyController {

	@GetMapping("/company")
	public String companyForm() {
		return "company/company";
	}
	
	@GetMapping("/vision")
	public String visionForm() {
		return "company/vision";
	}
	
	@GetMapping("/location")
	public String locationForm() {
		return "company/location";
	}
}

