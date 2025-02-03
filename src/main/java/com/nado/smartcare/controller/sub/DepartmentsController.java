package com.nado.smartcare.controller.sub;

import com.nado.smartcare.employee.domain.dto.EmployeeDto;
import com.nado.smartcare.employee.service.EmployeeService;
import com.nado.smartcare.reservation.domain.dto.ReservationScheduleDto;
import com.nado.smartcare.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentsController {
    // TODO: 의사 필드 중에 진료 과목을 담을 필드 필요함

    private final String SURGERY = "SURGERY";
    private final String INTERNAL_MEDICINE = "INTERNAL_MEDICINE";
    private final String PSYCHIATRY = "PSYCHIATRY";

    private final EmployeeService employeeService;
    private final ReservationService reservationService;

    @GetMapping("/surgery")
    public String surgery(Model model) {
        /*
        * - 인간은 망각의 동물 -
        * 예약키 형식: employeeNo + "-" + dayOfWeek + "-" + timeSlot
        * ex: 의사번호 151, 예약날짜 7(일요일), 시간대 MORNING
        * 키값: 151-7-MORNING
        * */

        // 외과 의사 목록 조회
        List<EmployeeDto> surgeryDoctors = employeeService.findDoctorsByDepartment(SURGERY);
        model.addAttribute("surgeryDoctors", surgeryDoctors);

        // 이번 주 예약 정보 조회
        List<ReservationScheduleDto> thisWeeksReservations = reservationService.getThisWeeksReservations();

        // 예약 키 목록 생성
        List<String> scheduleKeys = new ArrayList<>();
        for (ReservationScheduleDto reservation : thisWeeksReservations) {
            String key = String.format("%d-%d-%s",
                    reservation.employeeNo(),
                    reservation.reservationDate().getDayOfWeek().getValue(),
                    reservation.timeSlot().name());
            scheduleKeys.add(key);
        }
        log.info("Schedule Keys: {}", scheduleKeys);
        model.addAttribute("scheduleKeys", scheduleKeys);

        return "departments/surgery";
    }

    @GetMapping("/internalMedicine")
    public String internalMedicine(Model model) {
        /*
         * - 인간은 망각의 동물 -
         * 예약키 형식: employeeNo + "-" + dayOfWeek + "-" + timeSlot
         * ex: 의사번호 151, 예약날짜 7(일요일), 시간대 MORNING
         * 키값: 151-7-MORNING
         * */

        // 외과 의사 목록 조회
        List<EmployeeDto> surgeryDoctors = employeeService.findDoctorsByDepartment(INTERNAL_MEDICINE);
        model.addAttribute("surgeryDoctors", surgeryDoctors);

        // 이번 주 예약 정보 조회
        List<ReservationScheduleDto> thisWeeksReservations = reservationService.getThisWeeksReservations();

        // 예약 키 목록 생성
        List<String> scheduleKeys = new ArrayList<>();
        for (ReservationScheduleDto reservation : thisWeeksReservations) {
            String key = String.format("%d-%d-%s",
                    reservation.employeeNo(),
                    reservation.reservationDate().getDayOfWeek().getValue(),
                    reservation.timeSlot().name());
            scheduleKeys.add(key);
        }
        log.info("Schedule Keys: {}", scheduleKeys);
        model.addAttribute("scheduleKeys", scheduleKeys);

        return "departments/internalMedicine";
    }

    @GetMapping("/psychiatry")
    public String psychiatry(Model model) {
        /*
         * - 인간은 망각의 동물 -
         * 예약키 형식: employeeNo + "-" + dayOfWeek + "-" + timeSlot
         * ex: 의사번호 151, 예약날짜 7(일요일), 시간대 MORNING
         * 키값: 151-7-MORNING
         * */

        // 외과 의사 목록 조회
        List<EmployeeDto> surgeryDoctors = employeeService.findDoctorsByDepartment(PSYCHIATRY);
        model.addAttribute("surgeryDoctors", surgeryDoctors);

        // 이번 주 예약 정보 조회
        List<ReservationScheduleDto> thisWeeksReservations = reservationService.getThisWeeksReservations();

        // 예약 키 목록 생성
        List<String> scheduleKeys = new ArrayList<>();
        for (ReservationScheduleDto reservation : thisWeeksReservations) {
            String key = String.format("%d-%d-%s",
                    reservation.employeeNo(),
                    reservation.reservationDate().getDayOfWeek().getValue(),
                    reservation.timeSlot().name());
            scheduleKeys.add(key);
        }
        log.info("Schedule Keys: {}", scheduleKeys);
        model.addAttribute("scheduleKeys", scheduleKeys);

        return "departments/psychiatry";
    }
}
