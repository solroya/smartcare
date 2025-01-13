package com.nado.smartcare.patient.controller;

import com.nado.smartcare.patient.domain.PatientRecordCard;
import com.nado.smartcare.patient.domain.dto.PatientRecordCardDto;
import com.nado.smartcare.patient.service.PatientRecordCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(("/erp/record"))
public class PatientRecordCardController {

    private final PatientRecordCardService patientRecordCardService;

    @GetMapping("/list")
    public String listOfPatientRecords(Model model) {
        List<PatientRecordCardDto> records = patientRecordCardService.findAll();
        model.addAttribute("records", records);
        return "erp/record/list";
    }

    @GetMapping("/{patientRecordNo}")
    public String getRecordDetails(@PathVariable Long patientRecordNo, Model model) {
        PatientRecordCardDto record = patientRecordCardService.findById(patientRecordNo)
                .orElseThrow(() -> new IllegalArgumentException("Record not found: " + patientRecordNo));
        model.addAttribute("record", record);
        return "erp/record/record-details"; // 상세보기 템플릿 경로
    }

    @GetMapping("/schedule")
    public String schedule(Model model) {
        return "erp/record/schedule";
    }

    @GetMapping("/weekly/schedule/{employeeNo}")
    @ResponseBody
    public Map<LocalDate, Map<String, Boolean>> getWeeklySchedule(@PathVariable Long employeeNo) {
        log.info("employeeNo: " + employeeNo);
        return patientRecordCardService.findWeeklyReservationsByEmployee(employeeNo);
    }
}
