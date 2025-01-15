package com.nado.smartcare.reservation.repository;

import com.nado.smartcare.reservation.domain.Reservation;
import com.nado.smartcare.reservation.domain.type.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    boolean existsByEmployeeNoAndReservationDateAndTimeSlot(Long employeeNo, LocalDate reservationDate, TimeSlot timeSlot);

    @Query("SELECT r.reservationDate FROM Reservation r WHERE r.employeeNo = :employeeNo AND r.timeSlot = :timeSlot")
    List<LocalDate> findReservationByEmployeeNoAndTimeSlot(Long employeeNo, TimeSlot timeSlot);
}
