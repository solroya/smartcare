package com.nado.smartcare.patient.repository;

import com.nado.smartcare.patient.domain.Reception;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceptionRepository extends JpaRepository<Reception, Long> {
}
