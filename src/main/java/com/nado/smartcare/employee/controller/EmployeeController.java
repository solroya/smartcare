package com.nado.smartcare.employee.controller;

import ch.qos.logback.core.model.Model;
import com.nado.smartcare.employee.domain.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/employee")
public class EmployeeController {


    @GetMapping("join")
    public String join(ModelMap map) {
        return "employee/join";
    }

}
