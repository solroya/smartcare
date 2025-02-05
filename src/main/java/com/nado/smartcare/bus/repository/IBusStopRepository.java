package com.nado.smartcare.bus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nado.smartcare.bus.entity.BusStop;

@Repository
public interface IBusStopRepository extends JpaRepository<BusStop, Long> {
	
}
