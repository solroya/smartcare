package com.nado.smartcare.bus.service;

import java.util.List;

import com.nado.smartcare.bus.dto.BusStopDTO;
import com.nado.smartcare.bus.entity.BusStop;

public interface IBusStopService {
	
	void saveBusStop(List<BusStop> busStops);
	
	void saveBusStopFromApi();
	
	BusStop findNearestBusStop(double lat, double lng);
	
	default BusStop dtoToEntity(BusStopDTO busStopDTO) {
		return BusStop.builder()
				.id(busStopDTO.getId())
				.busStopId(busStopDTO.getBusStopId())
				.busStopName(busStopDTO.getBusStopName())
				.longitude(busStopDTO.getLongitude())
				.latitude(busStopDTO.getLatitude())
				.nextBusStop(busStopDTO.getNextBusStop())
				.build();
	}
	
	default BusStopDTO entityToDto(BusStop busStop) {
		return BusStopDTO.builder()
				.id(busStop.getId())
				.busStopId(busStop.getBusStopId())
				.busStopName(busStop.getBusStopName())
				.longitude(busStop.getLongitude())
				.latitude(busStop.getLatitude())
				.nextBusStop(busStop.getNextBusStop())
				.build();
	}
}
