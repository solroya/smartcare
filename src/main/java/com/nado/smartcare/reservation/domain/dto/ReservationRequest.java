package com.nado.smartcare.reservation.domain.dto;

import com.nado.smartcare.employee.domain.type.WorkingStatus;
import com.nado.smartcare.reservation.domain.type.TimeSlot;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationRequest {
    private Long memberNo;
    private Long employeeNo;
    private Long patientRecordCardNo;
    private LocalDate reservationDate;
    private TimeSlot timeSlot;
    private WorkingStatus status;
}
