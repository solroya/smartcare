package com.nado.smartcare.reservation.domain.dto;

import com.nado.smartcare.reservation.domain.Reservation;
import com.nado.smartcare.reservation.domain.type.TimeSlot;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReservationDto(
        Long reservationNo,
        LocalDate reservationDate,
        TimeSlot timeSlot,
        Long memberNo,
        Long employeeNo,
        Long patientRecordCardNo,

        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static ReservationDto from(Reservation reservation) {
        return new ReservationDto(
                reservation.getReservationNo(),
                reservation.getReservationDate(),
                reservation.getTimeSlot(),
                reservation.getMemberNo(),
                reservation.getEmployeeNo(),
                reservation.getPatientRecordCardNo(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
        );
    }
}
