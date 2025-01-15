package com.nado.smartcare.controller.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/erp")
public class ErpController {

    @GetMapping
    public String index() {
        return "erp/index";
    }
}
