package com.nado.smartcare.bus.service;

import java.util.List;

import com.nado.smartcare.bus.dto.LineStationDTO;
import com.nado.smartcare.bus.entity.LineStation;

public interface ILineStationService {
	
	void saveLineStations(List<LineStation> lineStations);
	
	void saveLineStationFromApi();
	
	default LineStation dtoToEntity(LineStationDTO lineStationDTO) {
		return LineStation.builder()
				.id(lineStationDTO.getId())
				.lineId(lineStationDTO.getLineId())
				.lineName(lineStationDTO.getLineName())
				.busStopId(lineStationDTO.getBusStopId())
				.busStopName(lineStationDTO.getBusStopName())
				.returnFlag(lineStationDTO.getReturnFlag())
				.build();
	}
	
	default LineStationDTO entityToDto(LineStation lineStation) {
		return LineStationDTO.builder()
				.id(lineStation.getId())
				.lineId(lineStation.getLineId())
				.lineName(lineStation.getLineName())
				.busStopId(lineStation.getBusStopId())
				.busStopName(lineStation.getBusStopName())
				.returnFlag(lineStation.getReturnFlag())
				.build();
	}
	
}
