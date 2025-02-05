package com.nado.smartcare.bus.service;

import java.util.List;

import com.nado.smartcare.bus.dto.BusArrivalDTO;

public interface IBusArrivalService {
	List<BusArrivalDTO> getArrivalInfoByBusStopId(int busStopId);
}
