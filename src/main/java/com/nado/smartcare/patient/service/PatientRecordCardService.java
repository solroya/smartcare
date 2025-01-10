package com.nado.smartcare.patient.service;

import com.nado.smartcare.patient.domain.dto.PatientRecordCardDto;

import java.time.LocalDateTime;

public interface PatientRecordCardService {

    void registerPatientRecordCard(PatientRecordCardDto patientRecordCardDto);

}
