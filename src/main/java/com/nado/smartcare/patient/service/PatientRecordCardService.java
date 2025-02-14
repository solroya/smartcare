package com.nado.smartcare.patient.service;

import com.nado.smartcare.patient.domain.PatientRecordCard;
import com.nado.smartcare.patient.domain.dto.PatientRecordCardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    Page<PatientRecordCardDto> findByDateRangeAndSearchTerm(LocalDate startDate, LocalDate endDate, String searchTerm, Pageable pageable);

    List<PatientRecordCardDto> getPatientRecordsByMemberId(Long memberNo);

    List<PatientRecordCardDto> getPatientRecordsWithLimit(Long memberNo, int offset, int limit);

    long getTotalRecordCount(Long memberNo);

    List<PatientRecordCardDto> getNextPatientRecords(Long memberNo, int offset, int limit);
}
