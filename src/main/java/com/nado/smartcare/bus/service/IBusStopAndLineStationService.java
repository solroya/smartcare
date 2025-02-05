package com.nado.smartcare.bus.service;

import com.nado.smartcare.bus.dto.BusStopAndLineStationDTO;
import com.nado.smartcare.bus.entity.BusStop;
import com.nado.smartcare.bus.entity.BusStopAndLineStation;
import com.nado.smartcare.bus.entity.LineStation;

public interface IBusStopAndLineStationService {
	
	void saveBusStopAndLineStations();
	
	default BusStopAndLineStation dtoToEntity(BusStopAndLineStationDTO busStopAndLineStationDTO, LineStation lineStation, BusStop busStop) {
        return BusStopAndLineStation.builder()
        		.id(busStopAndLineStationDTO.getId())
        		.lineStation(lineStation)
                .busStop(busStop)
                .build();
    }
	
	default BusStopAndLineStationDTO entityDto(BusStopAndLineStation busStopAndLineStation) {
		return BusStopAndLineStationDTO.builder()
				.id(busStopAndLineStation.getId())
				.lineId(busStopAndLineStation.getLineStation().getLineId())
				.lineName(busStopAndLineStation.getLineStation().getLineName())
				.busStopId(busStopAndLineStation.getBusStop().getBusStopId())
				.busStopName(busStopAndLineStation.getBusStop().getBusStopName())
				.build();
	}
	
}
