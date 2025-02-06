package com.nado.smartcare.tmap.service;

import java.util.List;

import com.nado.smartcare.tmap.dto.RouteDTO;

public interface ITMapService {
	List<RouteDTO> getWalkingRoutes(double startLat, double startLng, double endLat, double endLng);
	List<RouteDTO> getBusRoutes(double startLat, double startLng, double endLat, double endLng);
	int getWalkingTime(double sLat, double sLng, double eLat, double eLng);
}
