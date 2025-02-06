package com.nado.smartcare.reservation.domain;

import com.nado.smartcare.config.BaseEntity;
import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.employee.domain.type.WorkingStatus;
import com.nado.smartcare.member.domain.Member;
import com.nado.smartcare.patient.domain.PatientRecordCard;
import com.nado.smartcare.reservation.domain.dto.ReservationDto;
import com.nado.smartcare.reservation.domain.type.ReservationStatus;
import com.nado.smartcare.reservation.domain.type.TimeSlot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Getter
@ToString
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
    @ToString.Exclude
    private Employee employee;

    @Enumerated(EnumType.STRING)
    private WorkingStatus status; // 진료, 휴가, 수술

    @ManyToOne
    @JoinColumn(name = "patient_record_card_no", nullable = true)
    private PatientRecordCard patientRecordCard;

    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus = ReservationStatus.CONFIRMED;  // 기본값을 설정함

    // 취소 기능을 위한 메서드
    public void cancel() {
        this.reservationStatus = ReservationStatus.CANCELLED;
    }

    // 상태 업데이트
    public void updateReservation(){
        this.reservationStatus = ReservationStatus.COMPLETED;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Reservation() {
    }

    public Reservation(LocalDate reservationDate, TimeSlot timeSlot, Member member, Employee employee, WorkingStatus status, PatientRecordCard patientRecordCard, ReservationStatus reservationStatus) {
        this.reservationDate = reservationDate;
        this.timeSlot = timeSlot;
        this.member = member;
        this.employee = employee;
        this.status = status;
        this.patientRecordCard = patientRecordCard;
        this.reservationStatus = reservationStatus;
    }

    public static Reservation of(LocalDate reservationDate, Member member, TimeSlot timeSlot, Employee employee, WorkingStatus status, PatientRecordCard patientRecordCard, ReservationStatus reservationStatus) {
        return new Reservation(reservationDate, timeSlot, member, employee, status, patientRecordCard, reservationStatus);
    }

    public static Reservation toEntity(ReservationDto reservationDto) {
        return new Reservation(
                reservationDto.reservationDate(),
                reservationDto.timeSlot(),
                reservationDto.memberNo(),
                reservationDto.employeeNo(),
                reservationDto.employeeNo().getWorkingStatus(),
                reservationDto.patientRecordCard(),
                reservationDto.reservationStatus()
        );
    }
}
