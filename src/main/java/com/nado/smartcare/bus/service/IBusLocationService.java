package com.nado.smartcare.bus.service;

import java.util.List;

public interface IBusLocationService {
	List<Integer> getBusLocations(int lineId);
}
