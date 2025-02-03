package com.nado.smartcare.patient.controller;

import com.nado.smartcare.disease.domain.dto.DiseaseCategoryDto;
import com.nado.smartcare.disease.domain.dto.DiseaseListDto;
import com.nado.smartcare.disease.service.DiseaseService;
import com.nado.smartcare.employee.domain.Department;
import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.employee.domain.dto.DepartmentDto;
import com.nado.smartcare.employee.domain.dto.EmployeeDto;
import com.nado.smartcare.employee.repository.DepartmentRepository;
import com.nado.smartcare.employee.service.DepartmentService;
import com.nado.smartcare.employee.service.EmployeeService;
import com.nado.smartcare.member.domain.Member;
import com.nado.smartcare.member.domain.dto.MemberDto;
import com.nado.smartcare.member.service.MemberService;
import com.nado.smartcare.patient.domain.PatientRecordCard;
import com.nado.smartcare.patient.domain.dto.PatientRecordCardDto;
import com.nado.smartcare.patient.domain.dto.ReceptionDto;
import com.nado.smartcare.patient.service.PatientRecordCardService;
import com.nado.smartcare.patient.service.ReceptionService;
import com.nado.smartcare.reservation.domain.Reservation;
import com.nado.smartcare.reservation.domain.dto.ReservationDto;
import com.nado.smartcare.reservation.domain.type.TimeSlot;
import com.nado.smartcare.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/erp/record")
public class PatientRecordCardController {

    private final PatientRecordCardService patientRecordCardService;

    private final DiseaseService diseaseService;

    private final ReceptionService receptionService;
    private final ReservationService reservationService;
    private final MemberService memberService;
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final DepartmentRepository departmentRepository;

    @GetMapping("/list")
    public String listOfPatientRecords( @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        Model model) {

        Page<ReservationDto> reservations = reservationService.findAllWithPagination(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "reservationDate"))
        );
        log.info("reservations: {}", reservations);

        model.addAttribute("reservations", reservations);
        model.addAttribute("page", reservations.getNumber());
        model.addAttribute("size", reservations.getSize());
        model.addAttribute("totalElements", reservations.getTotalElements());

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

    @GetMapping("/{id}/edit")
    public String editRecord(@PathVariable Long id, Model model) {
        // 현재 진료 기록 조회
        PatientRecordCardDto record = patientRecordCardService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid record ID: " + id));

        // 질병 카테고리 목록 조회
        List<DiseaseCategoryDto> categories = diseaseService.getAllDiseaseCategories();

        // 선택된 카테고리의 질병 목록 조회
        List<DiseaseListDto> diseases = diseaseService.findByCategoryId(record.diseaseCategory().getDiseaseCategoryNo());

        model.addAttribute("record", record);
        model.addAttribute("categories", categories);
        model.addAttribute("diseases", diseases);

        return "erp/record/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateRecord(@PathVariable Long id,
                               @ModelAttribute PatientRecordCardDto updatedRecord,
                               RedirectAttributes redirectAttributes) {
        try {
            patientRecordCardService.updateRecord(id, updatedRecord);
            redirectAttributes.addFlashAttribute("successMessage", "진료 기록이 성공적으로 수정되었습니다.");
            return "redirect:/erp/record/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "진료 기록 수정 중 오류가 발생했습니다.");
            return "redirect:/erp/record/" + id + "/edit";
        }
    }


    // 진료 예약 목록의 환자 접수
    @GetMapping("/{reservationNo}/register")
    public String registerPatientRecord(@PathVariable Long reservationNo, Model model) {
        log.info("접수 등록 페이지 요청: reservationNo={}", reservationNo);

        Reservation reservation = reservationService.getReservation(reservationNo);

        EmployeeDto employee = employeeService.findById(reservation.getEmployee().getEmployeeNo())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: "));
        MemberDto member = memberService.searchMemberNo(reservation.getMember().getMemberNo())
                .orElseThrow(() -> new IllegalArgumentException("Member not found: "));

        Department department = departmentRepository.findById(employee.departmentId().getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException(""));


        // DiseaseCategory 정보 로드
        List<DiseaseCategoryDto> categories = diseaseService.getAllDiseaseCategories();
        if (categories.isEmpty()) {
            log.warn("No categories found.");
        } else {
            log.info("Loaded {} categories.", categories.size());
        }

        model.addAttribute("reservation", reservation);
        model.addAttribute("employee", employee);
        model.addAttribute("member", member);
        model.addAttribute("department", department);
        model.addAttribute("categories", categories);

        return "erp/record/reservation/register";
    }


}
