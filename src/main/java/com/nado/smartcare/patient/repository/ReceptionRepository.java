package com.nado.smartcare.patient.repository;

import com.nado.smartcare.patient.domain.Reception;
import com.nado.smartcare.patient.domain.type.ReceptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReceptionRepository extends JpaRepository<Reception, Long> {

    // DashBoard 접수인원
    long count();

    Optional<Reception> findByMember_MemberNoAndEmployee_EmployeeNo(Long memberNo, Long employeeNo);

    Optional<Reception> findByReservation_ReservationNo(Long reservationNo);

    // 중복 처리 에러 관련
    // 날짜순으로 정렬하여 조회
    List<Reception> findByMember_MemberNoAndEmployee_EmployeeNoOrderByCreatedAtDesc(
            Long memberNo,
            Long employeeNo
    );

    Page<Reception> findByStatusNot(ReceptionStatus receptionStatus, Pageable pageable);

    List<Reception> findByStatusNot(ReceptionStatus receptionStatus);
}
