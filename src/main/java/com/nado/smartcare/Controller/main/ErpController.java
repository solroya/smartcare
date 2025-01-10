package com.nado.smartcare.Controller.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/erp")
public class ErpController {

    @GetMapping
    public String index() {
        return "erp/index";
    }
}
