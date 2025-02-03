package com.nado.smartcare.reservation.domain.dto;

import com.nado.smartcare.reservation.domain.Reservation;
import com.nado.smartcare.reservation.domain.type.TimeSlot;

import java.time.LocalDate;

public record ReservationScheduleDto(
        Long reservationNo,
        LocalDate reservationDate,
        TimeSlot timeSlot,
        Long employeeNo
) {
    public static ReservationScheduleDto from(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        if (reservation.getEmployee() == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }

        return new ReservationScheduleDto(
                reservation.getReservationNo(),
                reservation.getReservationDate(),
                reservation.getTimeSlot(),
                reservation.getEmployee().getEmployeeNo()
        );
    }
}
