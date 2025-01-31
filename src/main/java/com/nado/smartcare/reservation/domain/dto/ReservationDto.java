package com.nado.smartcare.reservation.domain.dto;

import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.member.domain.Member;
import com.nado.smartcare.patient.domain.PatientRecordCard;
import com.nado.smartcare.patient.domain.Reception;
import com.nado.smartcare.reservation.domain.Reservation;
import com.nado.smartcare.reservation.domain.type.TimeSlot;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReservationDto(
        Long reservationNo,
        LocalDate reservationDate,
        TimeSlot timeSlot,
        Member memberNo,
        Employee employeeNo,
        PatientRecordCard patientRecordCard,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static ReservationDto from(Reservation reservation) {
        return new ReservationDto(
                reservation.getReservationNo(),
                reservation.getReservationDate(),
                reservation.getTimeSlot(),
                reservation.getMember(),
                reservation.getEmployee(),
                reservation.getPatientRecordCard(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
        );
    }
}
