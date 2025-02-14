package com.nado.smartcare.bus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nado.smartcare.bus.entity.LineStation;

@Repository
public interface ILineStationRepository extends JpaRepository<LineStation, Long> {
	List<LineStation> findByLineIdOrderByIdAsc(int lineId);
}
