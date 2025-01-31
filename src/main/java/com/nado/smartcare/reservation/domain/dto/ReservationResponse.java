package com.nado.smartcare.reservation.domain.dto;

import com.nado.smartcare.reservation.domain.Reservation;
import com.nado.smartcare.reservation.domain.type.TimeSlot;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ReservationResponse {
    private Long reservationNo;         // 예약 번호
    private  LocalDate reservationDate;  // 예약 날짜
    private  TimeSlot timeSlot;          // 예약 시간대
    private  Long memberNo;              // 환자 번호
    private  Long employeeNo;            // 의사 번호
    private  String memberName;          // 환자 이름
    private  String employeeName;        // 의사 이름
    private  String departmentName;      // 진료과명

    public ReservationResponse(Long reservationNo, LocalDate reservationDate, TimeSlot timeSlot, Long memberNo, Long employeeNo, String memberName, String employeeName, String departmentName) {
        this.reservationNo = reservationNo;
        this.reservationDate = reservationDate;
        this.timeSlot = timeSlot;
        this.memberNo = memberNo;
        this.employeeNo = employeeNo;
        this.memberName = memberName;
        this.employeeName = employeeName;
        this.departmentName = departmentName;

    }

    // 엔티티와 추가 정보를 받아 DTO 생성
    public static ReservationResponse of(
            Reservation reservation,
            String memberName,
            String employeeName,
            String departmentName) {
        return new ReservationResponse(
                reservation.getReservationNo(),
                reservation.getReservationDate(),
                reservation.getTimeSlot(),
                reservation.getMember().getMemberNo(),
                reservation.getEmployee().getEmployeeNo(),
                memberName,
                employeeName,
                departmentName
        );
    }
}
