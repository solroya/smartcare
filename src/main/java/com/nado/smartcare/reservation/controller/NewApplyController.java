package com.nado.smartcare.reservation.controller;

import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.employee.domain.type.WorkingStatus;
import com.nado.smartcare.employee.repository.DepartmentRepository;
import com.nado.smartcare.employee.repository.EmployeeRepository;
import com.nado.smartcare.member.repository.MemberRepository;
import com.nado.smartcare.reservation.domain.Reservation;
import com.nado.smartcare.reservation.domain.dto.ReservationRequest;
import com.nado.smartcare.reservation.domain.type.ReservationStatus;
import com.nado.smartcare.reservation.domain.type.TimeSlot;
import com.nado.smartcare.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/apply")
@RequiredArgsConstructor
public class NewApplyController {

    private final MemberRepository memberRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    private final ReservationService reservationService;

    @GetMapping("/list")
    public String reservation(Model model) {
        return "erp/record/reservation";
    }

    @GetMapping("/apply")
    public String apply(Model model){
        // 기본 데이터 로드
        model.addAttribute("reservationRequest", new ReservationRequest());
        model.addAttribute("departments", departmentRepository.findAll()); // 진료과 목록
        model.addAttribute("employees", employeeRepository.findAll());
        model.addAttribute("members", memberRepository.findAll());
        model.addAttribute("timeSlots", TimeSlot.values());
        return "reservation/apply";
    }

    // 예약 페이지 초기 로드
    @GetMapping("/register")
    public String create1(Model model) {
        // 기본 데이터 로드
        model.addAttribute("reservationRequest", new ReservationRequest());
        model.addAttribute("departments", departmentRepository.findAll()); // 진료과 목록
        model.addAttribute("members", memberRepository.findAll());
        model.addAttribute("timeSlots", TimeSlot.values());
        return "reservation/register";
    }

    @GetMapping("/available-dates")
    @ResponseBody
    public ResponseEntity<List<LocalDate>> getAvailableDates(
            @RequestParam("employeeNo") Long employeeNo,
            @RequestParam("timeSlot") TimeSlot timeSlot) {
        List<LocalDate> availableDates = reservationService.getAvailableDates(employeeNo, timeSlot);
        return ResponseEntity.ok(availableDates);
    }

    // 예약 생성 엔드포인트 추가
    @PostMapping("/create")
    public String createReservation(@ModelAttribute ReservationRequest request, Model model) {
        log.info("Create reservation: {}", request);
        try {
            // 예약 생성 로직
            Reservation reservation = reservationService.createReservation(
                    request.getMemberNo(),
                    request.getReservationDate(),
                    request.getTimeSlot(),
                    request.getEmployeeNo(),
                    request.getPatientRecordCardNo(),
                    WorkingStatus.WORKING,
                    ReservationStatus.CONFIRMED
            );

            // 성공 시 예약 완료 페이지로 리다이렉트
            return "redirect:/apply/success?reservationNo=" + reservation.getReservationNo();
        } catch (IllegalArgumentException e) {
            // 실패 시 에러 메시지와 함께 예약 페이지로 돌아가기
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("reservationRequest", request);
            model.addAttribute("departments", departmentRepository.findAll());
            model.addAttribute("members", memberRepository.findAll());
            model.addAttribute("timeSlots", TimeSlot.values());
            return "reservation/apply";
        }
    }

    // 예약 완료 페이지
    @GetMapping("/success")
    public String success(@RequestParam Long reservationNo, Model model) {
        Reservation reservation = reservationService.getReservation(reservationNo);
        log.info("Reservation data: " + reservation);
        Optional<Employee> byEmployeeNo = employeeRepository.findByEmployeeNo(reservation.getEmployee().getEmployeeNo());

        if (byEmployeeNo.isPresent()) {
            model.addAttribute("employeeName", byEmployeeNo.get().getEmployeeName());
        } else {
            model.addAttribute("employeeName", "직원 오류");
        }
        model.addAttribute("reservationDate", reservation.getReservationDate());
        model.addAttribute("timeSlot", reservation.getTimeSlot());
        return "reservation/success";
    }

    // AJAX: 진료과별 의사 목록 조회
  /*  @GetMapping("/doctors")
    @ResponseBody
    public ResponseEntity<List<EmployeeResponse>> getDoctorsByDepartment(
            @RequestParam Long departmentId) {
        List<Employee> doctors = employeeService.findByDepartmentIdAndStatus(
                departmentId,
                WorkingStatus.WORKING
        );

        List<EmployeeResponse> response = doctors.stream()
                .map(EmployeeResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // AJAX: 의사별 예약 가능 날짜 조회
    @GetMapping("/available-dates")
    @ResponseBody
    public ResponseEntity<List<LocalDate>> getAvailableDates(
            @RequestParam Long employeeNo,
            @RequestParam TimeSlot timeSlot) {
        List<LocalDate> availableDates = reservationService.getAvailableDates(
                employeeNo,
                timeSlot,
                LocalDate.now(),
                LocalDate.now().plusMonths(1)  // 한 달 치 예약 가능 날짜 제공
        );
        return ResponseEntity.ok(availableDates);
    }*/
/*

    // AJAX: 특정 날짜의 예약 가능 시간대 조회
    @GetMapping("/available-times")
    @ResponseBody
    public ResponseEntity<List<TimeSlot>> getAvailableTimeSlots(
            @RequestParam Long employeeNo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<TimeSlot> availableSlots = reservationService.getAvailableTimeSlots(
                employeeNo,
                date
        );
        return ResponseEntity.ok(availableSlots);
    }

    // 예약 생성
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createReservation(@Valid @RequestBody ReservationRequest request) {
        try {
            ReservationResponse reservation = reservationService.createReservation(
                    request.getMemberNo(),
                    request.getReservationDate(),
                    request.getTimeSlot(),
                    request.getEmployeeNo(),
                    request.getPatientRecordCardNo(),
                    WorkingStatus.WORKING  // 기본 상태 설정
            );

            return ResponseEntity.ok(reservation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("예약 생성 실패", e.getMessage()));
        }
    }

    // 예약 완료 페이지
    @GetMapping("/success")
    public String success(@RequestParam Long reservationId, Model model) {
        ReservationResponse reservation = reservationService.getReservation(reservationId);
        model.addAttribute("reservation", reservation);
        return "reservation/success";
    }
*/
}
