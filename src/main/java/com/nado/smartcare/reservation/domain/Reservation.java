package com.nado.smartcare.reservation.domain;

import com.nado.smartcare.config.BaseEntity;
import com.nado.smartcare.employee.domain.type.WorkingStatus;
import com.nado.smartcare.reservation.domain.type.TimeSlot;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "reservation_seq")
    private Long reservationNo;

    private LocalDate reservationDate;

    @Enumerated(EnumType.STRING)
    private TimeSlot timeSlot; // 오전, 오후

    private Long memberNo;

    private Long employeeNo;

    private Long patientRecordCardNo;

    @Enumerated(EnumType.STRING)
    private WorkingStatus status; // 진료, 휴가, 수술

    public Reservation() {
    }

    public Reservation(LocalDate reservationDate, TimeSlot timeSlot, Long memberNo, Long employeeNo, Long patientRecordCardNo, WorkingStatus status) {
        this.reservationDate = reservationDate;
        this.timeSlot = timeSlot;
        this.memberNo = memberNo;
        this.employeeNo = employeeNo;
        this.patientRecordCardNo = patientRecordCardNo;
        this.status = status;
    }

    public static Reservation of(LocalDate reservationDate, Long memberNo, TimeSlot timeSlot, Long employeeNo, Long patientRecordCardNo, WorkingStatus status) {
        return new Reservation(reservationDate, timeSlot, memberNo, employeeNo, patientRecordCardNo, status);
    }
}
