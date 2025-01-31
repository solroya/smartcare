package com.nado.smartcare.controller.main;

import com.nado.smartcare.employee.service.DepartmentService;
import com.nado.smartcare.employee.service.EmployeeService;
import com.nado.smartcare.notice.dto.NoticeDto;
import com.nado.smartcare.notice.service.NoticeService;
import com.nado.smartcare.patient.domain.dto.ReceptionDto;
import com.nado.smartcare.patient.service.ReceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/erp")
@RequiredArgsConstructor
public class ErpController {
    private final EmployeeService employeeService;
    private final NoticeService noticeService;
    private final ReceptionService receptionService;
    private final DepartmentService departmentService;

    @GetMapping
    public String newIndex(Model model, Authentication authentication) {
/*        if (authentication != null && authentication.isAuthenticated()
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
        }*/

        // Card
        long receptionCount = receptionService.getTotalReceptionCount();
        model.addAttribute("receptionCount", receptionCount);

        // DashBoard : 환자 접수
        List<ReceptionDto> receptions = receptionService.getAllReceptions();
        model.addAttribute("receptions", receptions);

        // DashBoard : 공지사항
        List<NoticeDto> latestNotices = noticeService.findLatestNotices(6); // 공지사항 4개만 가져옴
        model.addAttribute("latestNotices", latestNotices);

        model.addAttribute("content", "erp/index :: content");
        return "erp/index";
    }

    @GetMapping("/employee/register")
    public String register(Model model) {
        model.addAttribute("loginStatus", "register");
        return "erp/employee/register";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
/*        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        EmployeeDto employeeDto = employeeService.searchEmployee(username)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        model.addAttribute("employee", employeeDto);
        model.addAttribute("loginStatus", "authenticated");*/
        return "erp/homepage/profile";
    }
}