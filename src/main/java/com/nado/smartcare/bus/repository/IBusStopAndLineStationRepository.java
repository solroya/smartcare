package com.nado.smartcare.bus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nado.smartcare.bus.entity.BusStopAndLineStation;

@Repository
public interface IBusStopAndLineStationRepository extends JpaRepository<BusStopAndLineStation, Long> {
	List<BusStopAndLineStation> findAllByLineStation_LineIdOrderByLineStation_IdAsc(int lineId);
}
