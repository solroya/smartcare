package com.nado.smartcare.patient.controller;

import com.nado.smartcare.disease.domain.dto.DiseaseCategoryDto;
import com.nado.smartcare.disease.domain.dto.DiseaseListDto;
import com.nado.smartcare.disease.service.DiseaseService;
import com.nado.smartcare.employee.domain.dto.DepartmentDto;
import com.nado.smartcare.employee.domain.dto.EmployeeDto;
import com.nado.smartcare.employee.service.DepartmentService;
import com.nado.smartcare.employee.service.EmployeeService;
import com.nado.smartcare.member.domain.dto.MemberDto;
import com.nado.smartcare.member.service.MemberService;
import com.nado.smartcare.patient.domain.PatientRecordCard;
import com.nado.smartcare.patient.domain.dto.PatientRecordCardDto;
import com.nado.smartcare.patient.domain.dto.ReceptionDto;
import com.nado.smartcare.patient.service.PatientRecordCardService;
import com.nado.smartcare.patient.service.ReceptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/erp/patients")
public class PatientController {

    private final MemberService memberService;

    private final EmployeeService employeeService;

    private final ReceptionService receptionService;

    private final DiseaseService diseaseService;

    private final PatientRecordCardService patientRecordCardService;

    private final DepartmentService departmentService;


    @GetMapping("/register")
    public String patientRegister(Model model) {
        return "erp/patients/register";
    }

    @PostMapping("/register")
    public String patientRegister(@RequestParam("memberNo") Long memberNo,
                                  @RequestParam("employeeNo") Long employeeNo) {
        MemberDto member = memberService.searchMemberNo(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with memberNo: " + memberNo));
        EmployeeDto doctor = employeeService.findById(employeeNo)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        log.info("Creating appointment for member: {}, employeeNo: {} ({})", memberNo, employeeNo);

        receptionService.registerReception(member.memberNo(), doctor.employeeNo(), LocalDateTime.now());

        return "redirect:/erp/patients/list";
    }

    @GetMapping("/search")
    public String searchPatients(@RequestParam("memberName") String memberName,
                                 @RequestParam("memberBirthday") String memberBirthday,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 Model model) {
        log.info("Searching patients with name: {} and birthday: {}", memberName, memberBirthday);
        List<MemberDto> members = memberService.findByNameAndMemberBirthday(memberName, LocalDate.parse(memberBirthday));
        Page<MemberDto> memberPage = memberService.getAllMembers(PageRequest.of(page, size));

        model.addAttribute("members", members);
        model.addAttribute("page", memberPage.getNumber());
        model.addAttribute("size", memberPage.getSize());
        model.addAttribute("totalElements", memberPage.getTotalElements());

        return "erp/patients/search-results";
    }

    @GetMapping("/all")
    public String patientAll(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             Model model) {
        Page<MemberDto> memberPage = memberService.getAllMembers(PageRequest.of(page, size));
        model.addAttribute("members", memberPage.getContent());
        model.addAttribute("page", memberPage.getNumber());
        model.addAttribute("size", memberPage.getSize());
        model.addAttribute("totalElements", memberPage.getTotalElements());
        return "erp/patients/search-results";
    }
    @GetMapping("/departments")
    @ResponseBody
    public ResponseEntity<List<DepartmentDto>> getDepartmentsWithDoctors() {
        return ResponseEntity.ok(departmentService.getAllDepartmentsWithDoctors());
    }

    @GetMapping("/doctors")
    @ResponseBody
    public List<EmployeeDto> getDoctorsByDepartment(@RequestParam("department") String departmentName) {
        log.info("Fetching doctors for department: " + departmentName);
        return employeeService.findDoctorsByDepartment(departmentName);
    }

    @GetMapping("/diseases/list")
    @ResponseBody
    public List<DiseaseListDto> getDiseaseListByCategory(@RequestParam("categoryNo") Long categoryNo) {
        log.info("Fetching disease list for categoryNo: {}", categoryNo);
        List<DiseaseListDto> diseaseList = diseaseService.findByCategoryId(categoryNo);
        log.info("Disease list fetched: {}", diseaseList);
        return diseaseList;
    }

    // 진료 접수 환자 목록
    @GetMapping("/list")
    public String patientList(Model model) {
        List<ReceptionDto> allReceptions = receptionService.getAllReceptions();
        model.addAttribute("receptions", allReceptions);
        return "erp/patients/reception/list";
    }

    // 진료 접수 환자 진료 결과 등록
    @GetMapping("/{receptionNo}/register")
    public String registerRecord(@PathVariable Long receptionNo, Model model) {
        log.info("receptionNo: {}", receptionNo);


        ReceptionDto receptionDto = receptionService.getReceptionById(receptionNo)
                .orElseThrow(() -> new IllegalArgumentException("Reception not found"));

        // DiseaseCategory 정보 로드
        List<DiseaseCategoryDto> categories = diseaseService.getAllDiseaseCategories();
        if (categories.isEmpty()) {
            log.warn("No categories found.");
        } else {
            log.info("Loaded {} categories.", categories.size());
        }

        model.addAttribute("reception", receptionDto);
        model.addAttribute("categories", categories);

        return "erp/patients/reception/register";
    }

    @PostMapping("/{receptionNo}/cancel")
    public String cancelReception(@PathVariable Long receptionNo, RedirectAttributes redirectAttributes) {
        try {
            receptionService.deleteReceptionById(receptionNo);
            redirectAttributes.addFlashAttribute("successMessage", "접수가 성공적으로 취소되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "접수 취소 중 오류가 발생했습니다.");
        }
        return "redirect:/erp/patients/list";
    }

    // 진료기록카드 저장 & 접수 리스트 삭제
    @Transactional
    @PostMapping("/{receptionNo}/record")
    public String registerPatientRecordCard(PatientRecordCardDto patientRecordCardDto) {
        log.info("Registering patientRecordCard: {}", patientRecordCardDto);

        patientRecordCardService.registerPatientRecordCard(patientRecordCardDto);

        return "redirect:/erp/patients/list";
    }



}
