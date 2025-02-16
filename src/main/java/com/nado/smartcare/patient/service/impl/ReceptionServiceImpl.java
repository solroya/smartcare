package com.nado.smartcare.patient.service.impl;

import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.employee.repository.EmployeeRepository;
import com.nado.smartcare.member.domain.Member;
import com.nado.smartcare.member.repository.MemberRepository;
import com.nado.smartcare.page.PageResponse;
import com.nado.smartcare.patient.domain.Reception;
import com.nado.smartcare.patient.domain.type.ReceptionStatus;
import com.nado.smartcare.patient.domain.dto.ReceptionDto;
import com.nado.smartcare.patient.repository.ReceptionRepository;
import com.nado.smartcare.patient.service.ReceptionService;
import com.nado.smartcare.reservation.domain.Reservation;
import com.nado.smartcare.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceptionServiceImpl implements ReceptionService {

    private final MemberRepository memberRepository;

    private final EmployeeRepository employeeRepository;

    private final ReceptionRepository receptionRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public void registerReception(Long memberNo, Long employeeNo, LocalDateTime appointmentTime) {
        Member member = memberRepository.findById(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with memberNo: " + memberNo));

        Employee employee = employeeRepository.findByEmployeeNo(employeeNo)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with employeeNo: " + employeeNo));

        ReceptionDto receptionDto = new ReceptionDto(
                null, // receptionNo는 생성될 때 자동으로 설정
                member,
                employee,
                null, // patientRecordCard는 null로 설정
                appointmentTime,
                ReceptionStatus.PENDING, // 기본 상태를 PENDING으로 설정
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        Reception reception = Reception.of(receptionDto, member, employee);

        receptionRepository.save(reception);
    }

    @Override
    public PageResponse<ReceptionDto> getAllReceptions(Pageable pageable) {
        Page<Reception> receptionPage = receptionRepository.findAll(pageable);

        Page<ReceptionDto> receptionDtoPage = receptionPage.map(ReceptionDto::from);

        return PageResponse.from(receptionDtoPage);
    }

    @Override
    public PageResponse<ReceptionDto> getActiveReceptions(Pageable pageable) {

        Page<Reception> receptionPage = receptionRepository.findByStatusNot(ReceptionStatus.COMPLETED, pageable);

        return PageResponse.from(receptionPage.map(ReceptionDto::from));
    }

    @Override
    public List<ReceptionDto> getActiveReceptionsForDashBoard() {
        List<Reception> reception = receptionRepository.findByStatusNot(ReceptionStatus.COMPLETED);

        return reception.stream().map(ReceptionDto::from).collect(Collectors.toList());
    }

    @Override
    public Optional<ReceptionDto> getReceptionById(Long receptionId) {
        Reception reception = receptionRepository.findById(receptionId)
                .orElseThrow(() -> new IllegalArgumentException("Reception Not found with receptionNo"));

        ReceptionDto dto = ReceptionDto.from(reception);
        return Optional.of(dto);
    }

    @Override
    public void updateReceptionStatus(ReceptionDto receptionDto, ReceptionStatus receptionStatus) {
        Reception reception = receptionRepository.findById(receptionDto.receptionNo())
                .orElseThrow(() -> new IllegalArgumentException("Reception Not found with receptionNo"));

        reception.setStatus(receptionStatus);
        receptionRepository.save(reception);
    }

    @Override
    public void deleteReceptionById(Long receptionId) {
        receptionRepository.deleteById(receptionId);
    }

    @Override
    public long getTotalReceptionCount() {
        return receptionRepository.count();
    }

    // 기존 Reception 가져오기 또는 생성
    // 예약없이 현장 접수 되는 경우 사용
    @Override
    public ReceptionDto getOrCreateReception(Long memberNo, Long employeeNo) {

        // 가장 최근의 접수 기록을 찾도록 수정(중복 접수 오류 처리)
        List<Reception> existingReceptions = receptionRepository
                .findByMember_MemberNoAndEmployee_EmployeeNoOrderByCreatedAtDesc(memberNo, employeeNo);

        if (!existingReceptions.isEmpty()) {
            // 가장 최근 접수 기록 반환
            return ReceptionDto.from(existingReceptions.get(0));
        }


        // 해당 환자와 의사의 기존 접수가 있는지 확인
        return receptionRepository.findByMember_MemberNoAndEmployee_EmployeeNo(memberNo, employeeNo)
                .map(ReceptionDto::from)
                .orElseGet(() -> {

                    // JPA 영속성 관리(JPA가 save 조건을 걸어서 불필요한 save 방지)
                    Member member = memberRepository.findByMemberNo(memberNo)
                            .orElseThrow(() -> new IllegalArgumentException("Member not found with memberNo: " + memberNo));
                    Employee employee = employeeRepository.findByEmployeeNo(employeeNo)
                            .orElseThrow(() -> new IllegalArgumentException("Employee not found with employeeNo: " + employeeNo));

                    Reception newReception = new Reception();
                    newReception.setStatus(ReceptionStatus.PENDING);
                    newReception.setMember(member);
                    newReception.setEmployee(employee);
                    newReception.setReceptionTime(LocalDateTime.now());
                    newReception.setStatus(ReceptionStatus.PENDING);
                    receptionRepository.save(newReception);
                    return ReceptionDto.from(newReception);
                });
    }

    // 예약 시점에는 Reservation만 저장하고, 실제 방문(접수) 시에 Reception을 생성하는 컨셉
    @Transactional
    @Override
    public ReceptionDto createReceptionFromReservation(Long reservationNo) {
        Reservation reservation = reservationRepository.findById(reservationNo)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));

        // 기존에 이 예약을 기반으로 한 접수가 있는지 확인
        Optional<Reception> existing = receptionRepository.findByReservation_ReservationNo(reservationNo);
        if (existing.isPresent()) {
            return ReceptionDto.from(existing.get());
        }

        // 새 Reception 생성
        Reception newReception = new Reception();
        newReception.setReservation(reservation);
        newReception.setMember(reservation.getMember());
        newReception.setEmployee(reservation.getEmployee());
        newReception.setReceptionTime(LocalDateTime.now());
        newReception.setStatus(ReceptionStatus.PENDING);

        Reception savedReception = receptionRepository.save(newReception);
        return ReceptionDto.from(savedReception);
    }




}
