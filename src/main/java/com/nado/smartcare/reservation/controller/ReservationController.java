package com.nado.smartcare.reservation.controller;

import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.employee.domain.type.WorkingStatus;
import com.nado.smartcare.employee.repository.DepartmentRepository;
import com.nado.smartcare.employee.repository.EmployeeRepository;
import com.nado.smartcare.employee.service.EmployeeService;
import com.nado.smartcare.member.repository.MemberRepository;
import com.nado.smartcare.member.service.MemberService;
import com.nado.smartcare.reservation.domain.dto.EmployeeResponse;
import com.nado.smartcare.reservation.domain.dto.ReservationRequest;
import com.nado.smartcare.reservation.domain.dto.ReservationResponse;
import com.nado.smartcare.reservation.domain.type.TimeSlot;
import com.nado.smartcare.reservation.domain.dto.AllowedUserResponse;
import com.nado.smartcare.reservation.service.ReservationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/reservation")
public class ReservationController {
    RestTemplate restTemplate = new RestTemplate();
    private final ReservationService reservationService;

    private final MemberService memberService;

    // TODO : 임시 조회 기능 -> 시큐리티 도입 후 수정
    private final MemberRepository memberRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    private final EmployeeService employeeService;

    @Value("${external.queue-service-url}")
    private String queueServiceUrl; // 외부 서비스 URL

    @GetMapping("/register")
    public String index(@RequestParam(name = "queue", defaultValue = "default") String queue,
                        @RequestParam(name = "user_id") Long userId,
                        HttpServletRequest request) {
        var cookies = request.getCookies();
        var cookieName = "user-queue-%s-token".formatted(queue);

        String token = "";
        if (cookies != null) {
            var cookie = Arrays.stream(cookies).filter(i -> i.getName().equalsIgnoreCase(cookieName)).findFirst();
            token = cookie.orElse(new Cookie(cookieName, "")).getValue();
        }

        var uri = UriComponentsBuilder
                .fromHttpUrl(queueServiceUrl)
                .path("/api/v1/queue/allowed")
                .queryParam("queue", queue)
                .queryParam("user_id", userId)
                .queryParam("token", token)
                .encode()
                .build()
                .toUri();

        ResponseEntity<AllowedUserResponse> response = restTemplate.getForEntity(uri, AllowedUserResponse.class);
        if (response.getBody() == null || !response.getBody().allowed()) {
            // 대기 웹페이지로 리다이렉트
            return "redirect:%s/waiting-room?user_id=%d&redirect_url=%s".formatted(
                    queueServiceUrl,
                    userId,
                    "http://127.0.0.1:8080/reservation/register?user_id=%d".formatted(userId)
            );
        }

        // 허용 상태라면 해당 페이지를 진입
        return "reservation/index";
    }


    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("reservationRequest", new ReservationRequest()); // 폼 객체 추가
        model.addAttribute("timeSlots", TimeSlot.values());
        model.addAttribute("statuses", WorkingStatus.values());
        model.addAttribute("members", memberRepository.findAll());
        model.addAttribute("employees", employeeRepository.findAll());
        return "reservation/register";
    }


    @PostMapping("/create")
    public String createReservation(@ModelAttribute ReservationRequest request, Model model) {
        log.info("Create reservation: {}", request);

        try {
            reservationService.createReservation(
                    request.getMemberNo(),
                    request.getReservationDate(),
                    request.getTimeSlot(),
                    request.getEmployeeNo(),
                    request.getPatientRecordCardNo(),
                    request.getStatus()
            );

            Employee employee = employeeRepository.findByEmployeeNo(request.getEmployeeNo())
                            .orElseThrow(() -> new RuntimeException("Employee not found"));

            model.addAttribute("employeeName", employee.getEmployeeName());
            model.addAttribute("reservationDate", request.getReservationDate());
            model.addAttribute("timeSlot", request.getTimeSlot().getDisplayName());

            return "reservation/success"; // 예약 성공 페이지
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/reservation/register";
        }
    }

    @GetMapping("/available-dates")
    public String availableDates(@RequestParam("employeeNo") Long employeeNo,
                                 @RequestParam("timeSlot") TimeSlot timeSlot,
                                 @RequestParam("memberNo") Long memberNo,
                                 @RequestParam("status") WorkingStatus status,
                                 Model model) {
        List<LocalDate> availableDates = reservationService.getAvailableDates(employeeNo, timeSlot);
        model.addAttribute("availableDates", availableDates);
        model.addAttribute("selectedEmployeeNo", employeeNo);
        model.addAttribute("selectedTimeSlot", timeSlot);
        model.addAttribute("selectedMemberNo", memberNo);
        model.addAttribute("status", status);


        return "reservation/available-dates";
    }


}
