package com.nado.smartcare.patient.controller;

import com.nado.smartcare.employee.dto.EmployeeDto;
import com.nado.smartcare.employee.service.EmployeeService;
import com.nado.smartcare.member.dto.MemberDto;
import com.nado.smartcare.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/erp/patients")
public class PatientController {

    private final MemberService memberService;

    private final EmployeeService employeeService;

    @GetMapping("/register")
    public String patientRegister(Model model) {
        return "patients/register";
    }

    @GetMapping("/search")
    public String searchPatients(@RequestParam("memberName") String memberName, @RequestParam("memberBirthday") String memberBirthday, Model model) {
        log.info("search patients " + memberName + " " + memberBirthday);
        List<MemberDto> members = memberService.findByNameAndMemberBirthday(memberName, LocalDate.parse(memberBirthday));
        model.addAttribute("members", members);
        return "patients/search-results";
    }

    @GetMapping("/doctors")
    @ResponseBody
    public List<EmployeeDto> getDoctorsByDepartment(@RequestParam("department") String departmentName) {
        log.info("Fetching doctors for department: " + departmentName);
        return employeeService.findDoctorsByDepartment(departmentName);
    }
}
