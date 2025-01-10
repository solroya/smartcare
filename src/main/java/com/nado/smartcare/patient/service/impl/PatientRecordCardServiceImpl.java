package com.nado.smartcare.patient.service.impl;

import com.nado.smartcare.patient.domain.PatientRecordCard;
import com.nado.smartcare.patient.domain.dto.PatientRecordCardDto;
import com.nado.smartcare.patient.domain.dto.ReceptionDto;
import com.nado.smartcare.patient.domain.type.ReceptionStatus;
import com.nado.smartcare.patient.repository.PatientRecordCardRepository;
import com.nado.smartcare.patient.service.PatientRecordCardService;
import com.nado.smartcare.patient.service.ReceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientRecordCardServiceImpl implements PatientRecordCardService {

    private final PatientRecordCardRepository patientRecordCardRepository;

    private final ReceptionService receptionService;

    @Transactional
    @Override
    public void registerPatientRecordCard(PatientRecordCardDto patientRecordCardDto) {
        // 1. 진료 기록 카드 저장
        patientRecordCardRepository.save(PatientRecordCard.of(
                patientRecordCardDto.clinicName(),
                patientRecordCardDto.clinicDate(),
                patientRecordCardDto.clinicReservationDate(),
                patientRecordCardDto.clinicManifestation(),
                patientRecordCardDto.clinicStatus(),
                patientRecordCardDto.receptionNo(),
                patientRecordCardDto.member(),
                patientRecordCardDto.employee(),
                patientRecordCardDto.diseaseCategory(),
                patientRecordCardDto.diseaseList()
        ));

        ReceptionDto receptionDto = ReceptionDto.from(patientRecordCardDto.receptionNo());
        receptionService.updateReceptionStatus(receptionDto, ReceptionStatus.COMPLETED);
    }
}
