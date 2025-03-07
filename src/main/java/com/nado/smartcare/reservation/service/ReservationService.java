package com.nado.smartcare.reservation.service;

import com.nado.smartcare.employee.domain.type.WorkingStatus;
import com.nado.smartcare.reservation.domain.Reservation;
import com.nado.smartcare.reservation.domain.dto.ReservationDto;
import com.nado.smartcare.reservation.domain.dto.ReservationScheduleDto;
import com.nado.smartcare.reservation.domain.type.ReservationStatus;
import com.nado.smartcare.reservation.domain.type.TimeSlot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationService {

    Reservation createReservation(Long memberNo, LocalDate localDate, TimeSlot timeSlot, Long employeeNo, Long patientRecordCardNo, WorkingStatus status, ReservationStatus reservationStatus);

    List<LocalDate> getAvailableDates(Long employeeNo, TimeSlot timeSlot);

    List<LocalDate> findNonCancelledReservationsDates(Long employeeNo, TimeSlot timeSlot);

    List<LocalDate> getAvailableDates(Long employeeNo, TimeSlot timeSlot, LocalDate now, LocalDate localDate);

    Reservation getReservation(Long reservationNo);

    Page<ReservationDto> findAllWithPagination(Pageable pageable, ReservationStatus reservationStatus);

    Optional<ReservationDto> findReservationById(Long reservationNo);

    List<ReservationScheduleDto> getThisWeeksReservations();

    List<ReservationDto> findAllReservations();

    List<ReservationDto> getReservationsByMemberId(Long memberNo);

    void cancelReservation(Long reservationNo);

    void updateReservationStatus(Long reservationNo);

    List<ReservationDto> findReservationsStatusNot();
}
