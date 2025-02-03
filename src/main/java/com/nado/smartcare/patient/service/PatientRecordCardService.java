package com.nado.smartcare.patient.service;

import com.nado.smartcare.patient.domain.dto.PatientRecordCardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PatientRecordCardService {

    void registerPatientRecordCard(PatientRecordCardDto patientRecordCardDto);

    List<PatientRecordCardDto> findAll();

    Optional<PatientRecordCardDto> findById(Long id);

    Map<LocalDate, Map<String, Boolean>> findWeeklyReservationsByEmployee(Long employeeNo);

    Page<PatientRecordCardDto> findAllWithPagination(Pageable pageable);

    void updateRecord(Long id, PatientRecordCardDto updatedRecord);

    Page<PatientRecordCardDto> findByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);
}
