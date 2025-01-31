package com.nado.smartcare.patient.service;

import com.nado.smartcare.patient.domain.dto.PatientRecordCardDto;
import com.nado.smartcare.patient.domain.dto.ReceptionDto;
import com.nado.smartcare.patient.domain.dto.type.ClinicStatus;
import com.nado.smartcare.patient.domain.type.ReceptionStatus;
import org.hibernate.query.Page;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReceptionService {

    void registerReception(Long memberNo, Long employeeNo, LocalDateTime appointmentTime);

    List<ReceptionDto> getAllReceptions();

    Optional<ReceptionDto> getReceptionById(Long receptionId);

    void updateReceptionStatus(ReceptionDto receptionDto, ReceptionStatus receptionStatus);

    void deleteReceptionById(Long receptionId);

    // DashBoard 접수인원
    long getTotalReceptionCount();

    // 진료 번호 조회 및 부여
    ReceptionDto getOrCreateReception(Long memberNo, Long employeeNo);

    ReceptionDto findByReceptionId(Long receptionId);

    ReceptionDto createReceptionFromReservation(Long reservationNo);

}
