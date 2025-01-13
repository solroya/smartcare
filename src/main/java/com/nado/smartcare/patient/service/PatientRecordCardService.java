package com.nado.smartcare.patient.service;

import com.nado.smartcare.patient.domain.dto.PatientRecordCardDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PatientRecordCardService {

    void registerPatientRecordCard(PatientRecordCardDto patientRecordCardDto);

    List<PatientRecordCardDto> findAll();

    Optional<PatientRecordCardDto> findById(Long id);

    Map<LocalDate, Map<String, Boolean>> findWeeklyReservationsByEmployee(Long employeeNo);

}
