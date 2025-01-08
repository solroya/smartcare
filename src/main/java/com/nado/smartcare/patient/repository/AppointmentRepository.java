package com.nado.smartcare.patient.repository;

import com.nado.smartcare.patient.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

}
