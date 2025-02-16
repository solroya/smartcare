package com.nado.smartcare.patient.service;

import com.nado.smartcare.page.PageResponse;
import com.nado.smartcare.patient.domain.dto.ReceptionDto;
import com.nado.smartcare.patient.domain.type.ReceptionStatus;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReceptionService {

    void registerReception(Long memberNo, Long employeeNo, LocalDateTime appointmentTime);

    PageResponse<ReceptionDto> getAllReceptions(Pageable pageable);

    PageResponse<ReceptionDto> getActiveReceptions(Pageable pageable);

    // 대시 보드 조회용
    List<ReceptionDto> getActiveReceptionsForDashBoard();
    Optional<ReceptionDto> getReceptionById(Long receptionId);

    void updateReceptionStatus(ReceptionDto receptionDto, ReceptionStatus receptionStatus);

    void deleteReceptionById(Long receptionId);

    // DashBoard 접수인원
    long getTotalReceptionCount();

    // 진료 번호 조회 및 부여
    ReceptionDto getOrCreateReception(Long memberNo, Long employeeNo);

    ReceptionDto createReceptionFromReservation(Long reservationNo);

}
