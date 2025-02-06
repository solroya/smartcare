package com.nado.smartcare.member.controller;

import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.employee.domain.dto.DepartmentDto;
import com.nado.smartcare.employee.domain.type.WorkingStatus;
import com.nado.smartcare.employee.repository.EmployeeRepository;
import com.nado.smartcare.employee.service.DepartmentService;
import com.nado.smartcare.member.domain.Member;
import com.nado.smartcare.member.repository.MemberRepository;
import com.nado.smartcare.patient.domain.PatientRecordCard;
import com.nado.smartcare.patient.repository.PatientRecordCardRepository;
import com.nado.smartcare.reservation.domain.Reservation;
import com.nado.smartcare.reservation.domain.dto.ReservationRequest;
import com.nado.smartcare.reservation.domain.type.ReservationStatus;
import com.nado.smartcare.reservation.domain.type.TimeSlot;
import com.nado.smartcare.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Log4j2
@Controller
@RequestMapping("/member/reservation")
@RequiredArgsConstructor
public class MemberReservationController {
    private final MemberRepository memberRepository;
    private final EmployeeRepository employeeRepository;
    private final ReservationService reservationService;
    private final PatientRecordCardRepository patientRecordCardRepository;

    private final DepartmentService departmentService;

    // 예약 페이지 진입점
    @GetMapping("/new")
    public String createReservation(@RequestParam(required = false) Long employeeNo,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    Model model) {
        // 현재 로그인한 멤버 정보 조회
        Member currentMember = memberRepository.findByMemberId(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 페이지 렌더링을 위한 기본값 설정
        model.addAttribute("preSelected", false);
        model.addAttribute("selectedDoctor", null);

        // 의사 정보가 URL 파라미터로 전달된 경우
        if (employeeNo != null) {
            Employee selectedDoctor = employeeRepository.findByEmployeeNo(employeeNo)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
            // 선택된 의사와 진료과 정보를 모델에 추가
            model.addAttribute("selectedDoctor", selectedDoctor);
            model.addAttribute("selectedDepartment", selectedDoctor.getDepartment().getDepartmentName());
            model.addAttribute("preSelected", true); // 미리 선택되었음을 표시
        }

        model.addAttribute("member", currentMember);
        model.addAttribute("timeSlots", TimeSlot.values());

        return "member/reservation/new";
    }

    @GetMapping("/new?{employeeNo}&{departmentId}")
    public String createReservationWithReservationData(@PathVariable Long employeeNo,
                                    @PathVariable Long departmentId,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    Model model) {
        // 현재 로그인한 멤버 정보 조회
        Member currentMember = memberRepository.findByMemberId(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 의사 정보가 URL 파라미터로 전달된 경우
        if (employeeNo != null) {
            Employee selectedDoctor = employeeRepository.findByEmployeeNo(employeeNo)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
            // 선택된 의사와 진료과 정보를 모델에 추가
            model.addAttribute("selectedDoctor", selectedDoctor);
            model.addAttribute("selectedDepartment", selectedDoctor.getDepartment().getDepartmentName());
            model.addAttribute("preSelected", true); // 미리 선택되었음을 표시
        }

        model.addAttribute("member", currentMember);
        model.addAttribute("timeSlots", TimeSlot.values());

        return "member/reservation/new";
    }

    @PostMapping("/create")
    public String processReservation(@AuthenticationPrincipal UserDetails userDetails,
                                     @Valid @ModelAttribute ReservationRequest request,
                                     Model model) {
        try {
            Member currentMember = memberRepository.findByMemberId(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Member not found"));

            // 예약 생성
            Reservation reservation = reservationService.createReservation(
                    currentMember.getMemberNo(),
                    request.getReservationDate(),
                    request.getTimeSlot(),
                    request.getEmployeeNo(),
                    request.getPatientRecordCardNo(),
                    WorkingStatus.WORKING,
                    ReservationStatus.CONFIRMED
            );

            return "redirect:/member/reservation/success/" + reservation.getReservationNo();


        } catch (IllegalArgumentException e) {
            log.error("예약확정 중 예러 발생" , e);
            Employee selectedDoctor = employeeRepository.findById(request.getEmployeeNo())
                    .orElse(null);

            model.addAttribute("error", e.getMessage());
            model.addAttribute("selectedDoctor", selectedDoctor);
            model.addAttribute("timeSlots", TimeSlot.values());
            model.addAttribute("error", e.getMessage());
            return "member/reservation/new";
        }
    }

    @GetMapping("/success/{reservationNo}")
    public String showReservationSuccess(@PathVariable Long reservationNo,
                                         Model model,
                                         @AuthenticationPrincipal UserDetails userDetails,
                                         RedirectAttributes redirectAttributes) {

        log.info("예약 성공 리다이렉트: reservationNo=" + reservationNo);

        try {
            Member currentMember = memberRepository.findByMemberId(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Member not found"));

            Reservation reservation = reservationService.getReservation(reservationNo);

            if (!reservation.getMember().getMemberNo().equals(currentMember.getMemberNo())) {
                throw new AccessDeniedException("Invalid reservation access");
            }

            Employee doctor = employeeRepository.findByEmployeeNo(reservation.getEmployee().getEmployeeNo())
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

            model.addAttribute("member", currentMember);
            model.addAttribute("reservation", reservation);
            model.addAttribute("employeeName", doctor.getEmployeeName());
            model.addAttribute("reservationDate", reservation.getReservationDate());
            model.addAttribute("timeSlot", reservation.getTimeSlot());

            return "member/reservation/success";

        } catch (Exception e) {
            // 에러 발생 시 Flash Attribute 사용
            redirectAttributes.addFlashAttribute("errorMessage", "예약 정보를 불러올 수 없습니다.");
            return "redirect:/member/reservation/new";
        }
    }

    @GetMapping("/available-dates")
    @ResponseBody
    public ResponseEntity<List<LocalDate>> getAvailableDates(
            @RequestParam("employeeNo") Long employeeNo,
            @RequestParam("timeSlot") TimeSlot timeSlot) {


        List<LocalDate> availableDates = reservationService.findNonCancelledReservationsDates(employeeNo, timeSlot);
        return ResponseEntity.ok(availableDates);
    }

    @GetMapping("/departments")
    @ResponseBody
    public ResponseEntity<List<DepartmentDto>> getDepartmentsWithDoctors() {
        return ResponseEntity.ok(departmentService.getAllDepartmentsWithDoctors());
    }

    @PostMapping("/cancel/{reservationNo}")
    @ResponseBody
    public ResponseEntity<?> cancelReservation(
            @PathVariable Long reservationNo,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            // 현재 로그인한 사용자 확인
            Member currentMember = memberRepository.findByMemberId(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Member not found"));

            // 예약 정보 조회 및 취소 권한 확인(예약자인 본인만 가능함)
            Reservation reservation = reservationService.getReservation(reservationNo);
            if (!reservation.getMember().getMemberNo().equals(currentMember.getMemberNo())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "예약 취소 권한이 없습니다."));
            }

            // 예약 날짜 확인 (당일 예약은 취소 불가)
            LocalDate today = LocalDate.now();
            if (reservation.getReservationDate().equals(today)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "당일 예약은 취소가 불가능합니다. 병원으로 직접 연락 부탁드립니다."));
            }
            // 예약 취소 처리
            reservationService.cancelReservation(reservationNo);

            return ResponseEntity.ok()
                    .body(Map.of(
                            "success", true,
                            "message", "예약이 취소되었습니다."
                    ));

        } catch (Exception e) {
            log.error("예약 취소 중 오류 발생", e);
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", "예약 취소 중 오류가 발생했습니다: " + e.getMessage()
                    ));
        }
    }


}
