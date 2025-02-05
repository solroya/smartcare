package com.nado.smartcare.reservation.repository;

import com.nado.smartcare.reservation.domain.Reservation;
import com.nado.smartcare.reservation.domain.dto.ReservationDto;
import com.nado.smartcare.reservation.domain.type.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByEmployee_EmployeeNoAndReservationDateAndTimeSlot(Long employeeNo, LocalDate reservationDate, TimeSlot timeSlot);

    @Query("SELECT r.reservationDate FROM Reservation r WHERE r.employee.employeeNo = :employeeNo AND r.timeSlot = :timeSlot")
    List<LocalDate> findReservationByEmployeeNoAndTimeSlot(Long employeeNo, TimeSlot timeSlot);

    // JOIN FETCH를 사용하여 연관 엔티티를 함께 조회
    @Query("SELECT DISTINCT r FROM Reservation r " +
           "JOIN FETCH r.employee e " +
           "WHERE r.reservationDate BETWEEN :startOfWeek AND :endOfWeek")
    List<Reservation> findReservationsForCurrentWeek(
            @Param("startOfWeek") LocalDate startOfWeek,
            @Param("endOfWeek") LocalDate endOfWeek
    );

    List<Reservation> findByMember_MemberNo(Long memberMemberNo);
}
