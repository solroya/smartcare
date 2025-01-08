package com.nado.smartcare.patient.controller;

import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.employee.dto.EmployeeDto;
import com.nado.smartcare.employee.service.EmployeeService;
import com.nado.smartcare.member.dto.MemberDto;
import com.nado.smartcare.member.service.MemberService;
import com.nado.smartcare.patient.dto.AppointmentDto;
import com.nado.smartcare.patient.dto.type.AppointmentStatus;
import com.nado.smartcare.patient.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/erp/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    private final MemberService memberService;

    private final EmployeeService employeeService;

    // 모든 진료 접수 목록 조회
    @GetMapping
    public String getAllAppointments(Model model) {
        List<AppointmentDto> appointments = appointmentService.getAllAppointments();
        model.addAttribute("appointments", appointments);
        return "appointments/list"; // appointments/list.html
    }

    // 진료 접수 생성 페이지
    @GetMapping("/new")
    public String createAppointmentForm(@RequestParam("memberNo") Long memberNo,
                                        Model model) {
        MemberDto member = memberService.searchMemberNo(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with memberNo: " + memberNo));
        model.addAttribute("member", member);

        return "appointments/new"; // appointments/new.html
    }

    // 진료 접수 생성 처리
    @PostMapping("/new")
    public String createAppointment(@RequestParam("memberNo") Long memberNo,
                                    @RequestParam("employeeNo") Long employeeNo) {
        MemberDto member = memberService.searchMemberNo(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with memberNo: " + memberNo));
        EmployeeDto doctor = employeeService.findById(employeeNo)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        log.info("Creating appointment for member: {}, employeeNo: {} ({})", memberNo, employeeNo);

        appointmentService.createAppointment(member.memberNo(), doctor.employeeNo(), LocalDateTime.now());

        return "redirect:/erp/appointments"; // 접수 목록 페이지로 리다이렉트
    }

    // 진료 상태 업데이트 (CANCEL 상태에서 삭제)
    @PostMapping("/{appointmentId}/status")
    public String updateAppointmentStatus(@PathVariable("appointmentId") Long appointmentId, @RequestParam AppointmentStatus status) {
        if (status == AppointmentStatus.CANCELLED) {
            appointmentService.deleteAppointment(appointmentId); // CANCELLED 상태면 삭제
        } else {
            appointmentService.updateAppointmentStatus(appointmentId, status); // 상태 업데이트
        }
        return "redirect:/erp/appointments";
    }

    @GetMapping("/{appointmentId}/record")
    public String createPatientRecordForm(@PathVariable Long appointmentId, Model model) {
        log.info("appointmentId: {}", appointmentId);
        AppointmentDto appointmentDto = appointmentService.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + appointmentId));

        MemberDto memberDto = memberService.searchMemberNo(appointmentDto.memberNo())
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + appointmentDto.memberNo()));

        model.addAttribute("appointmentId", appointmentId);
        model.addAttribute("patientInfo", memberDto);

        return "appointments/record";
    }
}
