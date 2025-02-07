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
    public String success(@RequestParam("reservationNo") Long reservationNo, Model model) {
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
}
