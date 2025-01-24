package com.nado.smartcare.controller.main;

import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.employee.domain.dto.EmployeeDto;
import com.nado.smartcare.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/erp")
public class ErpController {
    private final EmployeeService employeeService;

    @GetMapping
    public String newIndex(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserDetails) {
            try {
                String username = ((UserDetails) authentication.getPrincipal()).getUsername();
                EmployeeDto employeeDto = employeeService.searchEmployee(username)
                        .orElseThrow(() -> new RuntimeException("Employee not found"));
                model.addAttribute("employee", employeeDto);
                model.addAttribute("loginStatus", "authenticated");
            } catch (Exception e) {
                // 로그인은 되어있지만 사용자 정보를 불러오는데 실패한 경우
                model.addAttribute("loginStatus", "error");
                model.addAttribute("errorMessage", "사용자 정보를 불러올 수 없습니다.");
            }
        } else {
            // 비로그인 상태
            model.addAttribute("loginStatus", "anonymous");
        }
        model.addAttribute("content", "erp/index :: content");

        return "erp/index";
    }
}