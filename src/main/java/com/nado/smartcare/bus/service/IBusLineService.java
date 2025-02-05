package com.nado.smartcare.bus.service;

import java.util.List;

import com.nado.smartcare.bus.dto.BusLineDTO;
import com.nado.smartcare.bus.entity.BusLine;

public interface IBusLineService {
	void saveBusLine(List<BusLine> busLines);
	
	void saveBusLinesFromApi();
	
	default BusLine dtoToEntity(BusLineDTO busLineDTO) {
		return BusLine.builder()
				.lineId(busLineDTO.getLineId())
                .lineName(busLineDTO.getLineName())
                .dirUpName(busLineDTO.getDirUpName())
                .dirDownName(busLineDTO.getDirDownName())
                .firstRunTime(busLineDTO.getFirstRunTime())
                .lastRunTime(busLineDTO.getLastRunTime())
                .runInterval(busLineDTO.getRunInterval())
                .lineKind(busLineDTO.getLineKind())
                .build();
	}
	
	default BusLineDTO entityToDto(BusLine busLine) {
		return BusLineDTO.builder()
				.lineId(busLine.getLineId())
                .lineName(busLine.getLineName())
                .dirUpName(busLine.getDirUpName())
                .dirDownName(busLine.getDirDownName())
                .firstRunTime(busLine.getFirstRunTime())
                .lastRunTime(busLine.getLastRunTime())
                .runInterval(busLine.getRunInterval())
                .lineKind(busLine.getLineKind())
                .build();
	}
}
