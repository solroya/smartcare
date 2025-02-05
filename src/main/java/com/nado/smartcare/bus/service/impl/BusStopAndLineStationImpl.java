package com.nado.smartcare.bus.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nado.smartcare.bus.entity.BusStop;
import com.nado.smartcare.bus.entity.BusStopAndLineStation;
import com.nado.smartcare.bus.entity.LineStation;
import com.nado.smartcare.bus.repository.IBusStopAndLineStationRepository;
import com.nado.smartcare.bus.repository.IBusStopRepository;
import com.nado.smartcare.bus.repository.ILineStationRepository;
import com.nado.smartcare.bus.service.IBusStopAndLineStationService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class BusStopAndLineStationImpl implements IBusStopAndLineStationService {
	
	@Autowired
	private final IBusStopAndLineStationRepository iBusStopAndLineStationRepository;
	
	@Autowired
	private final IBusStopRepository iBusStopRepository;
	
	@Autowired
	private final ILineStationRepository iLineStationRepository;
	
//	@PostConstruct
//	public void init() {
//		saveBusStopAndLineStations();
//	}
	
	@Override
	public void saveBusStopAndLineStations() {
		List<LineStation> lineStations = iLineStationRepository.findAll();
        List<BusStop> busStops = iBusStopRepository.findAll();
        List<BusStopAndLineStation> busStopAndLineStations = new ArrayList<>();
        
        Set<String> existingMappings = new HashSet<>();
        for (LineStation lineStation : lineStations) {
            for (BusStop busStop : busStops) {
                if (lineStation.getBusStopId() == busStop.getBusStopId()) {
                    String mappingKey = lineStation.getLineId() + "-" + lineStation.getBusStopId();
                    if (!existingMappings.contains(mappingKey)) {
                        BusStopAndLineStation busStopAndLineStation = BusStopAndLineStation.builder()
                                .lineStation(lineStation)
                                .busStop(busStop)
                                .build();
                        busStopAndLineStations.add(busStopAndLineStation);
                        existingMappings.add(mappingKey);
                    } else {
                        log.warn("중복 매핑이 발생했습니다. LineStation ID: {}, BusStop ID: {}", lineStation.getLineId(), busStop.getBusStopId());
                    }
                }
            }
        }
        
        if (busStopAndLineStations.isEmpty()) {
			log.warn("저장할 매핑 데이터가 없습니다.");
		} else {
			iBusStopAndLineStationRepository.saveAll(busStopAndLineStations);
			log.info("총 {}개의 매핑 데이터가 저장되었습니다.", busStopAndLineStations.size());
		}
		
	}

}
