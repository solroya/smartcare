package com.nado.smartcare.patient.service.impl;

import com.nado.smartcare.patient.domain.PatientRecordCard;
import com.nado.smartcare.patient.domain.dto.PatientRecordCardDto;
import com.nado.smartcare.patient.domain.dto.ReceptionDto;
import com.nado.smartcare.patient.domain.type.ReceptionStatus;
import com.nado.smartcare.patient.repository.PatientRecordCardRepository;
import com.nado.smartcare.patient.service.PatientRecordCardService;
import com.nado.smartcare.patient.service.ReceptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
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
                patientRecordCardDto.diseaseList(),
                patientRecordCardDto.reservation()
        ));

        ReceptionDto receptionDto = ReceptionDto.from(patientRecordCardDto.receptionNo());
        receptionService.updateReceptionStatus(receptionDto, ReceptionStatus.COMPLETED);
    }

    @Override
    public List<PatientRecordCardDto> findAll() {
        List<PatientRecordCardDto> collect = patientRecordCardRepository.findAll().stream()
                .map(PatientRecordCardDto::from).collect(Collectors.toList());
        return collect;
    }

    @Override
    public Optional<PatientRecordCardDto> findById(Long id) {
        Optional<PatientRecordCardDto> patientRecordCardDto = patientRecordCardRepository.findById(id).map(PatientRecordCardDto::from);
        return patientRecordCardDto;
    }

    @Override
    public Map<LocalDate, Map<String, Boolean>> findWeeklyReservationsByEmployee(Long employeeNo) {

        log.info("서비스 구현체의 employee no: {}", employeeNo);

        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)); // 이번 주 일요일
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)); // 이번 주 토요일

        log.info("today: {}", today);
        log.info("startOfWeek: {}", startOfWeek);
        log.info("endOfWeek: {}", endOfWeek);

        // 진료 기록 조회
        List<PatientRecordCard> records = patientRecordCardRepository.findByEmployee_EmployeeNoAndClinicDateBetween(employeeNo, startOfWeek, endOfWeek);
        log.info("조회된 진료 기록: {}", records.size());
        records.forEach(record -> log.info("Record: {}", record));

        return patientRecordCardRepository.findByEmployee_EmployeeNoAndClinicDateBetween(employeeNo, startOfWeek, endOfWeek)
                .stream()
                .collect(Collectors.groupingBy(
                        PatientRecordCard::getClinicDate, // 날짜별로 그룹화
                        Collectors.partitioningBy(record -> record.getClinicReservationDate().getHour() < 12) // 오전/오후
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> Map.of(
                                "morning", !entry.getValue().get(true).isEmpty(), // 오전 예약 여부
                                "afternoon", !entry.getValue().get(false).isEmpty() // 오후 예약 여부
                        )
                ));
    }

    @Override
    public Page<PatientRecordCardDto> findAllWithPagination(Pageable pageable) {
        return patientRecordCardRepository.findAll(pageable)
                .map(PatientRecordCardDto::from);
    }

    @Transactional
    @Override
    public void updateRecord(Long id, PatientRecordCardDto updatedRecord) {
        PatientRecordCard existingRecord = patientRecordCardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("진료 기록을 찾을 수 없습니다. ID: " + id));

        existingRecord.updateRecord(
                // 새로운 값이 없으면 기존 값을 유지
                updatedRecord.clinicName() != null ? updatedRecord.clinicName() : existingRecord.getClinicName(),
                updatedRecord.clinicManifestation() != null ? updatedRecord.clinicManifestation() : existingRecord.getClinicManifestation(),
                updatedRecord.clinicStatus() != null ? updatedRecord.clinicStatus() : existingRecord.getClinicStatus(),
                updatedRecord.diseaseCategory() != null ? updatedRecord.diseaseCategory() : existingRecord.getDiseaseCategory(),
                updatedRecord.diseaseList() != null ? updatedRecord.diseaseList() : existingRecord.getDiseaseList(),
                updatedRecord.reservation() != null ? updatedRecord.reservation() : existingRecord.getReservation()
        );

    }

    @Override
    public Page<PatientRecordCardDto> findByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return patientRecordCardRepository.findByClinicDateBetween(startDate, endDate, pageable)
                .map(PatientRecordCardDto::from);
    }

    @Override
    public Page<PatientRecordCardDto> findByDateRangeAndSearchTerm(LocalDate startDate, LocalDate endDate, String searchTerm, Pageable pageable) {
        return patientRecordCardRepository.findByDateRangeAndSearchTerm(startDate, endDate, searchTerm, pageable)
                .map(PatientRecordCardDto::from);
    }

    @Override
    public List<PatientRecordCardDto> getPatientRecordsByMemberId(Long memberNo) {
        return patientRecordCardRepository.findByMember_MemberNo(memberNo)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PatientRecordCardDto> getPatientRecordsWithLimit(Long memberNo, int offset, int limit) {
        return patientRecordCardRepository
                .findByMemberNoOrderByClinicDateDesc(memberNo, offset, limit)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public long getTotalRecordCount(Long memberNo) {
        return patientRecordCardRepository.countByMemberNo(memberNo);
    }

    @Override
    public List<PatientRecordCardDto> getNextPatientRecords(Long memberNo, int offset, int limit) {
        List<PatientRecordCard> records = patientRecordCardRepository.findRecordsWithPagination(memberNo, offset, limit);
        return records.stream().map(PatientRecordCardDto::from).collect(Collectors.toList());
    }

    private PatientRecordCardDto convertToDto(PatientRecordCard record) {
        return PatientRecordCardDto.from(record);
    }
}
