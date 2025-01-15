package com.nado.smartcare.reservation.service.impl;

import com.nado.smartcare.employee.domain.type.WorkingStatus;
import com.nado.smartcare.reservation.domain.Reservation;
import com.nado.smartcare.reservation.domain.type.TimeSlot;
import com.nado.smartcare.reservation.repository.ReservationRepository;
import com.nado.smartcare.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    @Override
    public void createReservation(Long memberNo, LocalDate localDate, TimeSlot timeSlot, Long employeeNo, Long patientRecordCardNo, WorkingStatus status) {

        if (reservationRepository.existsByEmployeeNoAndReservationDateAndTimeSlot(employeeNo, localDate, timeSlot)) {
            throw new IllegalArgumentException("이미 예약이 되어 있습니다.");
        }

        Reservation reservation = Reservation.of(
                localDate,
                memberNo,
                timeSlot,
                employeeNo,
                patientRecordCardNo,
                status
        );

        reservationRepository.save(reservation);

    }

    @Override
    public List<LocalDate> getAvailableDates(Long employeeNo, TimeSlot timeSlot) {
        List<LocalDate> reservationByEmployeeNoAndTimeSlot = reservationRepository.findReservationByEmployeeNoAndTimeSlot(employeeNo, timeSlot);

        // 당월 날짜 조회
        LocalDate today = LocalDate.now();
        LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        List<LocalDate> availableDates = new ArrayList<>();
        for (LocalDate date = today; !date.isAfter(lastDayOfMonth); date = date.plusDays(1)) {
            if (!reservationByEmployeeNoAndTimeSlot.contains(date)) {
                availableDates.add(date);
            }
        }
        return availableDates;
    }
}
