package com.nado.smartcare.reservation.service;

import com.nado.smartcare.employee.domain.type.WorkingStatus;
import com.nado.smartcare.reservation.domain.type.TimeSlot;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService {

    void createReservation(Long memberNo, LocalDate localDate, TimeSlot timeSlot, Long employeeNo, Long patientRecordCardNo, WorkingStatus status);

    List<LocalDate> getAvailableDates(Long employeeNo, TimeSlot timeSlot);
}
