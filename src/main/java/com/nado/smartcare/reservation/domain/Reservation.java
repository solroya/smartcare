package com.nado.smartcare.reservation.domain;

import com.nado.smartcare.config.BaseEntity;
import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.employee.domain.type.WorkingStatus;
import com.nado.smartcare.member.domain.Member;
import com.nado.smartcare.patient.domain.PatientRecordCard;
import com.nado.smartcare.reservation.domain.dto.ReservationDto;
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

    @ManyToOne
    @JoinColumn(name = "member_no")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_no")
    private Employee employee;

    @Enumerated(EnumType.STRING)
    private WorkingStatus status; // 진료, 휴가, 수술

    @ManyToOne
    @JoinColumn(name = "patient_record_card_no", nullable = true)
    private PatientRecordCard patientRecordCard;

    public Reservation() {
    }

    public Reservation(LocalDate reservationDate, TimeSlot timeSlot, Member member, Employee employee, WorkingStatus status, PatientRecordCard patientRecordCard) {
        this.reservationDate = reservationDate;
        this.timeSlot = timeSlot;
        this.member = member;
        this.employee = employee;
        this.status = status;
        this.patientRecordCard = patientRecordCard;
    }

    public static Reservation of(LocalDate reservationDate, Member member, TimeSlot timeSlot, Employee employee, WorkingStatus status, PatientRecordCard patientRecordCard) {
        return new Reservation(reservationDate, timeSlot, member, employee, status, patientRecordCard);
    }

    public static Reservation toEntity(ReservationDto reservationDto) {
        return new Reservation(
                reservationDto.reservationDate(),
                reservationDto.timeSlot(),
                reservationDto.memberNo(),
                reservationDto.employeeNo(),
                reservationDto.employeeNo().getWorkingStatus(),
                reservationDto.patientRecordCard()
        );
    }
}
