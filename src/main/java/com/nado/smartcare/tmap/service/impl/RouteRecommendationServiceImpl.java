package com.nado.smartcare.tmap.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.nado.smartcare.bus.entity.BusStop;
import com.nado.smartcare.bus.service.IBusArrivalService;
import com.nado.smartcare.bus.service.IBusStopService;
import com.nado.smartcare.tmap.dto.RecommendedRouteDTO;
import com.nado.smartcare.tmap.dto.RouteDTO;
import com.nado.smartcare.tmap.service.IRouteRecommendationService;
import com.nado.smartcare.tmap.service.ITMapService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class RouteRecommendationServiceImpl implements IRouteRecommendationService {
	
	private final ITMapService itMapService;
	private final IBusStopService iBusStopService;
	
	@Override
	public List<RecommendedRouteDTO> recommendRoutes(double startLat, double startLng, double endLat, double endLng) {
		log.info("recommendRoutes들어왔나?");
		
		List<RouteDTO> directRoutes = itMapService.getWalkingRoutes(startLat, startLng, endLat, endLng)
				.stream()
				.filter(route -> route.getDistance() > 0 && route.getTime() >= 5)
				.collect(Collectors.toList());
		
		double directDistance = getDistance(startLat, startLng, endLat, endLng);
		if (directDistance <= 1000 && !directRoutes.isEmpty()) {
			log.info("출발지와 도착자의 거리가 {}m 이므로 도보만 추천", directDistance);
			return convertRoutes(directRoutes, "도보", "출발지 -> 도착지");
		}
		
		BusStop startStop = iBusStopService.findNearestBusStop(startLat, startLng);
		BusStop endStop = iBusStopService.findNearestBusStop(endLat, endLng);
		
		if (startStop == null || endStop == null) {
			log.warn("가까운 버스 정류장을 찾을 수 없음, 도보 경로만 추천");
			return convertRoutes(directRoutes, "도보", "출발지 -> 도착지");
		}
		
		log.info("출발지 정류장 : {}, 목적지 정류장 : {}", startStop.getBusStopName(), endStop.getBusStopName());
		
		List<RouteDTO> walkToBus = itMapService.getWalkingRoutes(startLat, startLng, startStop.getLatitude(), startStop.getLongitude());
		List<RouteDTO> busRoutes = itMapService.getBusRoutes(startStop.getLatitude(), startStop.getLongitude(), endStop.getLatitude(), endStop.getLongitude());
		List<RouteDTO> walkFromBus = itMapService.getWalkingRoutes(endStop.getLatitude(), endStop.getLongitude(), endLat, endLng);
		
		if (!directRoutes.isEmpty()) {
			int directTime = directRoutes.get(0).getTime();
			int busCombinedTime = getTotalBusTime(busRoutes, walkToBus, walkFromBus);
			if (directTime <= busCombinedTime - 10 && directTime < 20) {
				log.info("도보가 더 빠름 : 도보 추천");
				return convertRoutes(directRoutes, "도보", "출발지 -> 도착지");
			}
		}
		
		log.info("버스 + 도보 이동 추천");
		List<RecommendedRouteDTO> combinedRoutes = new ArrayList<>();
		combinedRoutes.addAll(convertRoutes(walkToBus, "도보", "출발지 -> 버스 정류장"));
		combinedRoutes.addAll(convertRoutes(busRoutes, "버스", "버스 탑승"));
		combinedRoutes.addAll(convertRoutes(walkFromBus, "도보", "버스 정류장 -> 도착지"));
		
		return combinedRoutes.stream()
				.sorted(Comparator.comparingInt(RecommendedRouteDTO::getTime))
				.limit(3)
				.collect(Collectors.toList());
		
	}
	
	private List<RecommendedRouteDTO> convertRoutes(List<RouteDTO> routes, String type, String description) {
		return routes.stream()
				.map(route -> RecommendedRouteDTO.builder()
						.type(type)
						.description(description + ": " + route.getDescription())
						.distance(route.getDistance())
						.time(route.getTime())
						.coordinates(route.getCoordinates())
						.build())
				.collect(Collectors.toList());
	}
	
	private int getTotalBusTime(List<RouteDTO> busRoutes, List<RouteDTO> walkToBus, List<RouteDTO> walkFromBus) {
		int busTime = busRoutes.isEmpty() ? Integer.MAX_VALUE : busRoutes.get(0).getTime();
		int walkTime = (walkToBus.isEmpty() ? 0 : walkToBus.get(0).getTime()) + (walkFromBus.isEmpty() ? 0 : walkFromBus.get(0).getTime());
		return busTime + walkTime;
	}
	
	private double getDistance(double lat1, double lng1, double lat2, double lng2) {
		double R = 6371e3;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
				   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
				   Math.sin(dLng/2) * Math.sin(dLng/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		return R * c;
	}

}
