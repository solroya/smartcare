package com.nado.smartcare.tmap.service;

import java.util.List;

import com.nado.smartcare.tmap.dto.RecommendedRouteDTO;

public interface IRouteRecommendationService {
	List<RecommendedRouteDTO> recommendRoutes(double startLat, double startLng, double endLat, double endLng);
}
