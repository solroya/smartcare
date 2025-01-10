package com.nado.smartcare.patient.service;

import com.nado.smartcare.patient.domain.dto.ReceptionDto;
import com.nado.smartcare.patient.domain.type.ReceptionStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReceptionService {

    void registerReception(Long memberNo, Long employeeNo, LocalDateTime appointmentTime);

    List<ReceptionDto> getAllReceptions();

    Optional<ReceptionDto> getReceptionById(Long receptionId);

    void updateReceptionStatus(ReceptionDto receptionDto, ReceptionStatus receptionStatus);

    void deleteReceptionById(Long receptionId);
}
