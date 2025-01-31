package com.nado.smartcare.patient.repository;

import com.nado.smartcare.patient.domain.PatientRecordCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PatientRecordCardRepository extends JpaRepository<PatientRecordCard, Long> {
    List<PatientRecordCard> findByEmployee_EmployeeNoAndClinicDateBetween(Long employeeNo, LocalDate startDate, LocalDate endDate);
    Page<PatientRecordCard> findByClinicDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
}
