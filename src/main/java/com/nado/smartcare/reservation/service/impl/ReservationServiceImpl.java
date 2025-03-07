package com.nado.smartcare.reservation.service.impl;

import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.employee.domain.dto.EmployeeDto;
import com.nado.smartcare.employee.domain.type.WorkingStatus;
import com.nado.smartcare.employee.repository.EmployeeRepository;
import com.nado.smartcare.employee.service.EmployeeService;
import com.nado.smartcare.member.domain.Member;
import com.nado.smartcare.member.domain.dto.MemberDto;
import com.nado.smartcare.member.repository.MemberRepository;
import com.nado.smartcare.member.service.MemberService;
import com.nado.smartcare.patient.domain.PatientRecordCard;
import com.nado.smartcare.patient.domain.dto.PatientRecordCardDto;
import com.nado.smartcare.patient.repository.PatientRecordCardRepository;
import com.nado.smartcare.patient.service.PatientRecordCardService;
import com.nado.smartcare.reservation.domain.Reservation;
import com.nado.smartcare.reservation.domain.dto.ReservationDto;
import com.nado.smartcare.reservation.domain.dto.ReservationScheduleDto;
import com.nado.smartcare.reservation.domain.type.ReservationStatus;
import com.nado.smartcare.reservation.domain.type.TimeSlot;
import com.nado.smartcare.reservation.repository.ReservationRepository;
import com.nado.smartcare.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final EmployeeRepository employeeRepository;
    private final MemberRepository memberRepository;
    private final PatientRecordCardRepository patientRecordCardRepository;

    @Override
    public Reservation createReservation(Long memberNo, LocalDate localDate, TimeSlot timeSlot, Long employeeNo, Long patientRecordCardNo, WorkingStatus status, ReservationStatus reservationStatus) {

        // 동일 시간대 예약 여부 확인
        if (reservationRepository.existsByEmployee_EmployeeNoAndReservationDateAndTimeSlotAndReservationStatus(employeeNo, localDate, timeSlot, ReservationStatus.CONFIRMED)) {
            throw new IllegalArgumentException("이미 예약이 되어 있습니다.");
        }

        // DTO를 사용하면 JPA가 관리하는 영속 객체로 인식하지 못해서 계속 무결성 제약 위반 오류를 발생함
        // DTO를 활용해야 하는 경우: 신규 등록일 경우 활용하는것이 올바름

        // 1) memberNo -> Member 영속 객체
        Member member = memberRepository.findById(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        // 2) employeeNo -> Employee 영속 객체
        Employee employee = employeeRepository.findById(employeeNo)
                .orElseThrow(() -> new IllegalArgumentException("직원 정보를 찾을 수 없습니다."));

        // 3) patientRecordCardNo -> PatientRecordCard 영속 객체(있다면)
        PatientRecordCard patientRecordCard = null;
        if (patientRecordCardNo != null) {
            patientRecordCard = patientRecordCardRepository.findById(patientRecordCardNo)
                    .orElseThrow(() -> new IllegalArgumentException("해당 환자카드를 찾을 수 없습니다."));
        }

        // 예약 엔티티 생성
        Reservation reservation = Reservation.of(
                localDate,
                member,
                timeSlot,
                employee,
                status,
                patientRecordCard,
                reservationStatus
        );
        return reservationRepository.save(reservation);

    }

    @Override
    public List<LocalDate> getAvailableDates(Long employeeNo, TimeSlot timeSlot) {
        List<LocalDate> reservationByEmployeeNoAndTimeSlot = reservationRepository.findReservationByEmployeeNoAndTimeSlot(employeeNo, timeSlot);

        // 당월 날짜 조회
        LocalDate today = LocalDate.now();
        LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        List<LocalDate> availableDates = new ArrayList<>();
        for (LocalDate date = today; !date.isAfter(lastDayOfMonth); date = date.plusDays(1)) {
            if (!reservationByEmployeeNoAndTimeSlot.contains(date)) {
                log.info("예약가능 날짜로 되는 날짜: {}", date);
                availableDates.add(date);
            }
        }
        return availableDates;
    }

    @Override
    public List<LocalDate> findNonCancelledReservationsDates(Long employeeNo, TimeSlot timeSlot) {
        List<LocalDate> reservationByEmployeeNoAndTimeSlot = reservationRepository.findNonCancelledReservationDates(employeeNo, timeSlot);

        // 당월 날짜 조회
        LocalDate today = LocalDate.now();
        LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        List<LocalDate> availableDates = new ArrayList<>();
        for (LocalDate date = today; !date.isAfter(lastDayOfMonth); date = date.plusDays(1)) {
            if (!reservationByEmployeeNoAndTimeSlot.contains(date)) {
                log.info("예약가능 날짜로 되는 날짜(취소예약 내역 포함): {}", date);
                availableDates.add(date);
            }
        }
        return availableDates;
    }

    @Override
    public List<LocalDate> getAvailableDates(Long employeeNo, TimeSlot timeSlot, LocalDate now, LocalDate localDate) {
        return List.of();
    }

    @Override
    public Reservation getReservation(Long reservationNo) {
        return reservationRepository.findById(reservationNo)
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public Page<ReservationDto> findAllWithPagination(Pageable pageable, ReservationStatus reservationStatus) {
        Page<Reservation> confirmedReservations = reservationRepository.findAllByReservationStatus(
                ReservationStatus.CONFIRMED, pageable
        );

        return confirmedReservations.map(ReservationDto::from);
    }

    @Override
    public Optional<ReservationDto> findReservationById(Long reservationNo) {
        return reservationRepository.findById(reservationNo)
                .map(ReservationDto::from);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationScheduleDto> getThisWeeksReservations() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        log.info("조회 기간: {} ~ {}", startOfWeek, endOfWeek);

        List<Reservation> reservations = reservationRepository
                .findReservationsForCurrentWeek(startOfWeek, endOfWeek);
        log.info("조회된 예약 수: {}", reservations.size());

        // 상세 로깅 추가
        reservations.forEach(r -> log.info("예약 정보: 날짜={}, 시간대={}, 의사번호={}",
                r.getReservationDate(),
                r.getTimeSlot(),
                r.getEmployee().getEmployeeNo()));

        // 예약 취소 기능 추가에 따른 상세 내역 필터 기능 추가
        List<ReservationScheduleDto> confirmedReservations = reservations.stream()
                .filter(r -> r.getReservationStatus() == ReservationStatus.CONFIRMED)
                .map(ReservationScheduleDto::from)
                .collect(Collectors.toList());

        return confirmedReservations;
    }

    @Override
    public List<ReservationDto> findAllReservations() {
        return reservationRepository.findAll().stream()
                .map(ReservationDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDto> getReservationsByMemberId(Long memberNo) {
        return reservationRepository.findByMember_MemberNo(memberNo).stream()
                .map(ReservationDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public void cancelReservation(Long reservationNo) {
        // 예약 정보 조회
        Reservation reservation = reservationRepository.findById(reservationNo)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));

        // 이미 취소된 예약인지 확인 (예약 상태 enum이 있다고 가정)
        if (reservation.getReservationStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 예약입니다.");
        }

        // 예약 상태를 취소로 변경
        reservation.cancel();

        reservationRepository.save(reservation);
    }

    @Override
    public void updateReservationStatus(Long reservationNo) {
        // 예약 정보 조회
        Reservation reservation = reservationRepository.findById(reservationNo)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));

        // 예약 상태값 변경
        reservation.updateReservation();

        reservationRepository.save(reservation);
    }

    @Override
    public List<ReservationDto> findReservationsStatusNot() {
        List<Reservation> reservations = reservationRepository.findByReservationStatus(ReservationStatus.CONFIRMED);

        return reservations.stream().map(ReservationDto::from).collect(Collectors.toList());
    }

}
