package com.nado.smartcare.employee.controller;

import com.nado.smartcare.employee.domain.dto.EmployeeDto;
import com.nado.smartcare.employee.service.EmployeeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("register")
    public String join() {
        return "employee/register";
    }

    @PostMapping("register")
    public String join(@Valid @ModelAttribute EmployeeDto employeeDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("Validation Errors : {}", bindingResult.getAllErrors());
            return "employee/register";
        }
        
        log.info("employeeDto register: {} ", employeeDto);

        employeeService.saveEmployee(employeeDto);
        return "redirect:/main";
    }

    @ResponseBody
    @GetMapping("search-employeeId")
    public Map<String, Boolean> checkId(@RequestParam("employeeId") String employeeId) {
        boolean result = employeeService.searchEmployee(employeeId).isPresent();
        return Map.of("result", result);
    }

    @ResponseBody
    @GetMapping("search-employeeEmail")
    public Map<String, Boolean> checkEmployeeEmail(@RequestParam("employeeEmail") String employeeEmail) {
        boolean result = employeeService.searchEmployeeEmail(employeeEmail).isPresent();
        return Map.of("result", result);
    }



}
