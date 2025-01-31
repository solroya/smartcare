package com.nado.smartcare.patient.repository;

import com.nado.smartcare.patient.domain.Reception;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface ReceptionRepository extends JpaRepository<Reception, Long> {

    // DashBoard 접수인원
    long count();

    Optional<Reception> findByMember_MemberNoAndEmployee_EmployeeNo(Long memberNo, Long employeeNo);

    Optional<Reception> findByReservation_ReservationNo(Long reservationNo);
}
