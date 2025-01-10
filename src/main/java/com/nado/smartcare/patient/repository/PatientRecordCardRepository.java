package com.nado.smartcare.patient.repository;

import com.nado.smartcare.patient.domain.PatientRecordCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRecordCardRepository extends JpaRepository<PatientRecordCard, Long> {
}
