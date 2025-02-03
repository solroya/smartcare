package com.nado.smartcare.reservation.service;

import com.nado.smartcare.employee.domain.type.WorkingStatus;
import com.nado.smartcare.patient.domain.dto.PatientRecordCardDto;
import com.nado.smartcare.reservation.domain.Reservation;
import com.nado.smartcare.reservation.domain.dto.ReservationDto;
import com.nado.smartcare.reservation.domain.dto.ReservationScheduleDto;
import com.nado.smartcare.reservation.domain.type.TimeSlot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationService {

    Reservation createReservation(Long memberNo, LocalDate localDate, TimeSlot timeSlot, Long employeeNo, Long patientRecordCardNo, WorkingStatus status);

    List<LocalDate> getAvailableDates(Long employeeNo, TimeSlot timeSlot);

    List<LocalDate> getAvailableDates(Long employeeNo, TimeSlot timeSlot, LocalDate now, LocalDate localDate);

    Reservation getReservation(Long reservationNo);

    Page<ReservationDto> findAllWithPagination(Pageable pageable);

    Optional<ReservationDto> findReservationById(Long reservationNo);

    List<ReservationScheduleDto> getThisWeeksReservations();

    List<ReservationDto> findAllReservations();
}
