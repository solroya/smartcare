package com.nado.smartcare.Controller.sub;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class subController {

	@GetMapping("company")
	public String companyForm() {
		return "/company/company";
	}
	
}

