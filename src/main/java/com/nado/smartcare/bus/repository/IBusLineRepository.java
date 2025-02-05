package com.nado.smartcare.bus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nado.smartcare.bus.entity.BusLine;

@Repository
public interface IBusLineRepository extends JpaRepository<BusLine, Long> {
	List<BusLine> findByLineName(String busNumber);
}
