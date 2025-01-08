package com.nado.smartcare.patient.dto;

import com.nado.smartcare.patient.dto.type.AppointmentStatus;
import com.nado.smartcare.patient.entity.Appointment;

import java.time.LocalDateTime;

public record AppointmentDto(
        Long appointmentId,
        Long memberNo,
        String memberName,
        LocalDateTime appointmentTime,
        AppointmentStatus status // 접수 상태 ("Pending", "Completed")
) {
    public static AppointmentDto from(Appointment appointment) {
        return new AppointmentDto(
                appointment.getAppointmentId(),
                appointment.getMember().getMemberNo(),
                appointment.getMember().getMemberName(),
                appointment.getAppointmentTime(),
                appointment.getStatus()
        );
    }
}