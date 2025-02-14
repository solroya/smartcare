package com.nado.smartcare.openai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
@Log4j2
public class CustomerController {

    @GetMapping
    public String customer() {
        return "customer/index";
    }

    @GetMapping("/consume")
    public String consume() {
        return "customer/consume";
    }

    @GetMapping("/chat")
    public String chat() {
        return "customer/customer";
    }
}
