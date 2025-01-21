package com.nado.smartcare.openai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @GetMapping
    public String customer() {
        return "customer/index";
    }

    @GetMapping("/consume")
    public String consume() {
        return "customer/consume";
    }
}
