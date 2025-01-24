package com.nado.smartcare.reservation.domain.dto;

import com.nado.smartcare.employee.domain.type.WorkingStatus;
import com.nado.smartcare.reservation.domain.type.TimeSlot;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationRequest {
    private Long memberNo;                // 환자 번호
    private Long employeeNo;              // 의사 번호
    private Long patientRecordCardNo;     // 진료 기록 번호
    private LocalDate reservationDate;    // 예약 날짜
    private TimeSlot timeSlot;            // 예약 시간대
    private WorkingStatus status;         // 상태
}
