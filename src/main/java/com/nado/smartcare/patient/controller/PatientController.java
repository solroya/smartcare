package com.nado.smartcare.patient.controller;

import com.nado.smartcare.disease.domain.dto.DiseaseCategoryDto;
import com.nado.smartcare.disease.domain.dto.DiseaseListDto;
import com.nado.smartcare.disease.service.DiseaseService;
import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.employee.domain.dto.DepartmentDto;
import com.nado.smartcare.employee.domain.dto.EmployeeDto;
import com.nado.smartcare.employee.service.DepartmentService;
import com.nado.smartcare.employee.service.EmployeeService;
import com.nado.smartcare.member.domain.dto.MemberDto;
import com.nado.smartcare.member.domain.dto.SearchMemberDto;
import com.nado.smartcare.member.service.MemberService;
import com.nado.smartcare.page.PageResponse;
import com.nado.smartcare.patient.domain.PatientRecordCard;
import com.nado.smartcare.patient.domain.Reception;
import com.nado.smartcare.patient.domain.dto.PatientRecordCardDto;
import com.nado.smartcare.patient.domain.dto.ReceptionDto;
import com.nado.smartcare.patient.domain.dto.type.ClinicStatus;
import com.nado.smartcare.patient.repository.ReceptionRepository;
import com.nado.smartcare.patient.service.PatientRecordCardService;
import com.nado.smartcare.patient.service.ReceptionService;
import com.nado.smartcare.reservation.domain.Reservation;
import com.nado.smartcare.reservation.domain.dto.ReservationDto;
import com.nado.smartcare.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
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
    private final ReservationService reservationService;
    private final ReceptionRepository receptionRepository;


    @GetMapping("/register")
    public String patientRegister(Model model) {
        model.addAttribute("selectedPatient", null);
        return "erp/patients/register";
    }

    // 환자 기록 카드 저장
    @PostMapping("/register")
    public String patientRegister(@RequestParam("memberNo") Long memberNo,
                                  @RequestParam("employeeNo") Long employeeNo) {
        MemberDto member = memberService.searchMemberNo(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with memberNo: " + memberNo));
        EmployeeDto doctor = employeeService.findById(employeeNo)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        log.info("Creating appointment for member: {}, employeeNo: {}", memberNo, employeeNo);

        receptionService.registerReception(member.memberNo(), doctor.employeeNo(), LocalDateTime.now());

        return "redirect:/erp/patients/list";
    }

    @PostMapping("/searchRegister")
    public String patientSearchRegister(Model model, @RequestBody MemberDto memberDto) {
        log.info("환자 정보 수신: {}", memberDto);
        model.addAttribute("selectedPatient", null);
        model.addAttribute("selectedPatient", memberDto);
        return "erp/patients/register";
    }

    @GetMapping("/search")
    public String searchPatients(@RequestParam("memberName") String memberName,
                                 @RequestParam("memberBirthday") String memberBirthday,
                                 @PageableDefault(size = 10) Pageable pageable,
                                 Model model) {
        log.info("Searching patients with name: {} and birthday: {}", memberName, memberBirthday);

        // 생년월일 파싱 (null 처리 포함)
        LocalDate birthday = null;
        if (StringUtils.hasText(memberBirthday)) {
            try {
                birthday = LocalDate.parse(memberBirthday);
            } catch (DateTimeParseException e) {
                log.warn("Invalid date format: {}", memberBirthday);
            }
        }
        log.info("파싱 후 생년월일: {}", birthday);

        PageResponse<SearchMemberDto> members = memberService.findByNameAndMemberBirthday(memberName, birthday, pageable);
        log.info("검색된 회원 정보:  {}", members.toString());

        // 모델에 필요한 데이터 추가
        model.addAttribute("members", members.getContent());
        log.info("검색결과 member: {}", members.getContent());
        model.addAttribute("currentPage", members.getPageNumber());
        log.info("currentPage: {}", members.getPageNumber());
        model.addAttribute("totalPages", members.getTotalPages());
        log.info("totalPages: {}", members.getTotalPages());
        model.addAttribute("totalElements", members.getTotalElements());
        log.info("totalElements: {}", members.getTotalElements());
        model.addAttribute("memberName", memberName);
        model.addAttribute("memberBirthday", memberBirthday);
        return "erp/patients/search-results";
    }

    @GetMapping("/searchByName")
    public String searchPatientsByName(@RequestParam("memberName") String memberName,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       Model model) {
        log.info("Searching patients with name: {}", memberName);
        List<MemberDto> members = memberService.searchByName(memberName);
//        Page<MemberDto> memberPage = memberService.getAllMembers(PageRequest.of(page, size));

        model.addAttribute("members", members);
//        model.addAttribute("page", memberPage.getNumber());
//        model.addAttribute("size", memberPage.getSize());
//        model.addAttribute("totalElements", memberPage.getTotalElements());

        return "erp/dashboard/search-results";
    }

    @GetMapping("/all")
    public String patientAll(@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                             Model model) {
        PageResponse<SearchMemberDto> members = memberService.getAllMembers(pageable);
        model.addAttribute("members", members.getContent());
        log.info("검색결과 member: {}", members.getContent());
        model.addAttribute("currentPage", members.getPageNumber());
        log.info("currentPage: {}", members.getPageNumber());
        model.addAttribute("totalPages", members.getTotalPages());
        log.info("totalPages: {}", members.getTotalPages());
        model.addAttribute("totalElements", members.getTotalElements());
        log.info("totalElements: {}", members.getTotalElements());
        return "erp/patients/all-search-results";
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

    // 활성화(CONFIRMED) 된 환자만 조회
    @GetMapping("/list")
    public String patientList(@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                              Model model) {
        PageResponse<ReceptionDto> allReceptions = receptionService.getActiveReceptions(pageable);
        model.addAttribute("receptions", allReceptions);
        log.info("receptions 뷰 전달 데이터: {}", allReceptions.getContent());
        model.addAttribute("currentPage", allReceptions.getPageNumber());
        model.addAttribute("totalPages", allReceptions.getTotalPages());
        model.addAttribute("totalElements", allReceptions.getTotalElements());
        return "erp/patients/reception/list";
    }

    // 진료 접수 환자 진료 결과 등록
    // Reception을 통합 접수
    @GetMapping("/{receptionNo}/register")
    public String registerRecord(@PathVariable Long receptionNo, Model model) {
        log.info("접수 등록 페이지 요청: receptionNo={}", receptionNo);

        ReceptionDto findReceptionDto = receptionService.getReceptionById(receptionNo)
                .orElseThrow(() -> new IllegalArgumentException("Reception not found"));

        ReceptionDto receptionDto = receptionService.getOrCreateReception(findReceptionDto.member().getMemberNo(), findReceptionDto.employee().getEmployeeNo());
        log.info("receptionDto: {}", receptionDto);

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

    // Reservation을 통합 접수
    @GetMapping("/reservation/{reservationNo}/register")
    public String registerRecordByReservation(@PathVariable Long reservationNo, Model model) {
        log.info("접수 등록 페이지 요청: reservationNo={}", reservationNo);

/*        Reservation reservation = reservationService.getReservation(reservationNo);
        log.info("생성된 객체 reservation: {}", reservation);

        ReceptionDto reception = receptionService.getOrCreateReception(
                reservation.getMember().getMemberNo(), reservation.getEmployee().getEmployeeNo());
        log.info("생성된 Reception 객체: {}", reception);*/

        // 예약 -> Reception 생성/조회
        ReceptionDto receptionDto = receptionService.createReceptionFromReservation(reservationNo);

        // DiseaseCategory 정보 로드
        List<DiseaseCategoryDto> categories = diseaseService.getAllDiseaseCategories();
        if (categories.isEmpty()) {
            log.warn("No categories found.");
        } else {
            log.info("Loaded {} categories.", categories.size());
        }

        model.addAttribute("reception", receptionDto);
        model.addAttribute("categories", categories);
        model.addAttribute("reservationNo", reservationNo);

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
    public String registerPatientRecordCard(
            @RequestParam("receptionNo") Long receptionNo,
            @RequestParam(value = "reservationNo", required = false) Long reservationNo,
            PatientRecordCardDto patientRecordCardDto) {
        log.info("받은 receptionNo: {}", receptionNo);
        log.info("넘겨받은 환자 기록 카드 정보: {}", patientRecordCardDto);
        log.info("받은 reservationNo: {}", reservationNo);

        // DB에서 Reception 조회
        Reception reception = receptionRepository.findById(receptionNo)
                .orElseThrow(() -> new IllegalArgumentException("Reception not found with id=" + receptionNo));
        log.info("DB에서 조회된 reception: {}", reception);

        // reservationNo가 있는 경우에만 예약 상태 업데이트
        if (reservationNo != null) {
            reservationService.updateReservationStatus(reservationNo);
        }

        // 새로운 PatientRecordCard 생성
        patientRecordCardService.registerPatientRecordCard(patientRecordCardDto);

        return "redirect:/erp/patients/result";
    }

    @GetMapping("/result")
    public String listOfPatientRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) String searchTerm,
            Model model) {

        log.info("Start Date: {}, End Date: {}", startDate, endDate);

        // 정렬 방향 처리
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, "clinicDate"));

        // 날짜 범위 유효성 검사
        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) {
                log.warn("Start date is after end date. Start Date: {}, End Date: {}", startDate, endDate);
                model.addAttribute("errorMessage", "조회 시작 날짜는 종료 날짜보다 이전이어야 합니다.");
                model.addAttribute("records", Page.empty()); // 빈 결과를 전달
                model.addAttribute("page", page);
                model.addAttribute("size", size);
                model.addAttribute("totalElements", 0);
                model.addAttribute("startDate", startDate);
                model.addAttribute("endDate", endDate);
                model.addAttribute("sortDirection", sortDirection); // 현재 정렬 방향 유지
                return "erp/patients/result";
            }
        } else {
            log.warn("Either startDate or endDate is null. Defaulting to recent 7 days.");
            endDate = LocalDate.now();
            startDate = endDate.minusDays(7);
        }

        // 기본 날짜 필터 설정
        if (startDate == null || endDate == null) {
            endDate = LocalDate.now();
            startDate = endDate.minusDays(7); // 최근 7일의 날짜를 조회
        }

        Page<PatientRecordCardDto> records;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            // 검색어가 있을 경우: 검색어를 포함한 조회
            records = patientRecordCardService.findByDateRangeAndSearchTerm(startDate, endDate, searchTerm, pageRequest);
        } else {
            records = patientRecordCardService.findByDateRange(startDate, endDate, pageRequest);
        }

        model.addAttribute("records", records);
        model.addAttribute("page", records.getNumber());
        model.addAttribute("size", records.getSize());
        model.addAttribute("totalElements", records.getTotalElements());
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("sortDirection", sortDirection); // 현재 정렬 방향 유지

        return "erp/patients/result";
    }

}
