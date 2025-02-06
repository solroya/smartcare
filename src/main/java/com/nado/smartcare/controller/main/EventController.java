package com.nado.smartcare.controller.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/event")
public class EventController {

    @GetMapping("")
    public String queue() {
        return "redirect:http://133.186.219.22:8090";
    }

    @GetMapping("/index")
    public String index() {
        return "event/index";
    }
}
