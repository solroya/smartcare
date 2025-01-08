package com.nado.smartcare.patient.service;

import com.nado.smartcare.patient.dto.AppointmentDto;
import com.nado.smartcare.patient.dto.type.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentService {

    List<AppointmentDto> getAllAppointments();

    AppointmentDto createAppointment(Long memberNo, Long employeeNo, LocalDateTime appointmentTime);

    void updateAppointmentStatus(Long appointmentId, AppointmentStatus status);

    void deleteAppointment(Long appointmentId);

    Optional<AppointmentDto> findById(Long appointmentId);
}
