package com.nado.smartcare.tmap.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
	private final ObjectMapper objectMapper;
	
	private static final int MAX_WALK_TIME = 60;
	
	@Override
	public List<RecommendedRouteDTO> recommendRoutes(double startLat, double startLng, double endLat, double endLng) {
	    log.info("recommendRoutes 호출됨");

	    // 1. 도보 경로(출발지→도착지) 조회
	    List<RouteDTO> directRoutes = itMapService.getWalkingRoutes(startLat, startLng, endLat, endLng)
	            .stream()
	            .filter(route -> route.getDistance() > 0 && route.getTime() > 0)
	            .filter(route -> route.getDistance() <= 10000)
	            .filter(route -> route.getTime() <= MAX_WALK_TIME)
	            .collect(Collectors.toList());

	    double directDistance = getDistance(startLat, startLng, endLat, endLng);
	    log.info("직선 거리: {}m, 도보 경로 개수: {}", directDistance, directRoutes.size());

	    // 만약 출발지와 도착지 간 거리가 1000m 이하이면 도보 경로만 추천
	    if (directDistance <= 1000 && !directRoutes.isEmpty()) {
	        log.info("도보 경로 추천 ({}m)", directDistance);
	        return directRoutes.stream().limit(3)
	                .map(route -> RecommendedRouteDTO.builder()
	                        .type("도보")
	                        .description("출발지 -> 도착지: " + route.getDescription())
	                        .time(route.getTime())
	                        .distance(route.getDistance())
	                        .coordinates(route.getCoordinates())
	                        .detailedSteps(List.of(route))
	                        .build())
	                .collect(Collectors.toList());
	    }

	    // 2. 가장 가까운 버스 정류장 조회
	    BusStop startStop = iBusStopService.findNearestBusStop(startLat, startLng);
	    BusStop endStop = iBusStopService.findNearestBusStop(endLat, endLng);
	    if (startStop == null || endStop == null) {
	        log.warn("버스 정류장을 찾지 못함, 도보 경로만 추천");
	        return directRoutes.stream().limit(3)
	                .map(route -> RecommendedRouteDTO.builder()
	                        .type("도보")
	                        .description("출발지 -> 도착지: " + route.getDescription())
	                        .time(route.getTime())
	                        .distance(route.getDistance())
	                        .coordinates(route.getCoordinates())
	                        .detailedSteps(List.of(route))
	                        .build())
	                .collect(Collectors.toList());
	    }
	    log.info("출발 정류장: {}, 도착 정류장: {}", startStop.getBusStopName(), endStop.getBusStopName());

	    // 3. 각 구간의 경로 조회
	    List<RouteDTO> walkToBus = itMapService.getWalkingRoutes(startLat, startLng, startStop.getLatitude(), startStop.getLongitude());
	    List<RouteDTO> busRoutes = itMapService.getBusRoutes(startStop.getLatitude(), startStop.getLongitude(), endStop.getLatitude(), endStop.getLongitude());
	    List<RouteDTO> walkFromBus = itMapService.getWalkingRoutes(endStop.getLatitude(), endStop.getLongitude(), endLat, endLng);

	    log.info("walkToBus: {}, busRoutes: {}, walkFromBus: {}", 
	             walkToBus.size(), busRoutes.size(), walkFromBus.size());

	    // 4. 도보가 매우 빠른 경우 도보 경로 추천 (기존 로직)
	    if (!directRoutes.isEmpty()) {
	        int directTime = directRoutes.get(0).getTime();
	        int busCombinedTime = getTotalBusTime(busRoutes, walkToBus, walkFromBus);
	        log.info("directTime: {}분, busCombinedTime: {}분", directTime, busCombinedTime);
	        if (directTime <= busCombinedTime - 10 && directTime < 20) {
	            log.info("도보가 더 빠름: 도보 추천");
	            return directRoutes.stream().limit(3)
	                    .map(route -> RecommendedRouteDTO.builder()
	                            .type("도보")
	                            .description("출발지 -> 도착지: " + route.getDescription())
	                            .time(route.getTime())
	                            .distance(route.getDistance())
	                            .coordinates(route.getCoordinates())
	                            .detailedSteps(List.of(route))
	                            .build())
	                    .collect(Collectors.toList());
	        }
	    }

	    // 5. 복합 경로(버스 + 도보) : 각 구간의 모든 대안의 조합 생성
	    if (!walkToBus.isEmpty() && !busRoutes.isEmpty() && !walkFromBus.isEmpty()) {
	        List<RecommendedRouteDTO> combinedRoutes = new ArrayList<>();
	        // 삼중 for문을 이용하여 모든 조합을 생성합니다.
	        for (RouteDTO walkTo : walkToBus) {
	            for (RouteDTO bus : busRoutes) {
	                for (RouteDTO walkFrom : walkFromBus) {
	                    int totalTime = walkTo.getTime() + bus.getTime() + walkFrom.getTime();
	                    double totalDistance = walkTo.getDistance() + bus.getDistance() + walkFrom.getDistance();
	                    List<RouteDTO> detailedSteps = new ArrayList<>();
	                    detailedSteps.add(walkTo);
	                    detailedSteps.add(bus);
	                    detailedSteps.add(walkFrom);
	                    String combinedCoordinates = combineCoordinates(detailedSteps);
	                    
	                    RecommendedRouteDTO candidate = RecommendedRouteDTO.builder()
	                        .type("버스 + 도보")
	                        .description("출발지 -> 버스 정류장 -> 도착지: 버스 탑승")
	                        .time(totalTime)
	                        .distance(totalDistance)
	                        .coordinates(combinedCoordinates)
	                        .detailedSteps(detailedSteps)
	                        .build();
	                    combinedRoutes.add(candidate);
	                }
	            }
	        }
	        // 생성된 조합을 총 소요 시간(또는 다른 기준)으로 오름차순 정렬한 후 상위 3개 반환
	        combinedRoutes.sort(Comparator.comparingInt(RecommendedRouteDTO::getTime));
	        return combinedRoutes.stream().limit(3).collect(Collectors.toList());
	    }

	    // 6. fallback: 도보 경로 추천
	    return directRoutes.stream().limit(3)
	            .map(route -> RecommendedRouteDTO.builder()
	                    .type("도보")
	                    .description("출발지 -> 도착지: " + route.getDescription())
	                    .time(route.getTime())
	                    .distance(route.getDistance())
	                    .coordinates(route.getCoordinates())
	                    .detailedSteps(List.of(route))
	                    .build())
	            .collect(Collectors.toList());
	}

	private int getTotalBusTime(List<RouteDTO> busRoutes, List<RouteDTO> walkToBus, List<RouteDTO> walkFromBus) {
	    int busTime = busRoutes.isEmpty() ? Integer.MAX_VALUE : busRoutes.get(0).getTime();
	    int walkTime = (walkToBus.isEmpty() ? 0 : walkToBus.get(0).getTime())
	                 + (walkFromBus.isEmpty() ? 0 : walkFromBus.get(0).getTime());
	    return busTime + walkTime;
	}

	private double getDistance(double lat1, double lng1, double lat2, double lng2) {
	    double R = 6371e3;
	    double dLat = Math.toRadians(lat2 - lat1);
	    double dLng = Math.toRadians(lng2 - lng1);
	    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
	             + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
	             * Math.sin(dLng / 2) * Math.sin(dLng / 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    return R * c;
	}

	private String combineCoordinates(List<RouteDTO> steps) {
	    List<List<Double>> combined = new ArrayList<>();
	    for (RouteDTO step : steps) {
	        String coordsStr = step.getCoordinates();
	        if (coordsStr != null && coordsStr.trim().length() > 0) {
	            try {
	                List<List<Double>> coords = objectMapper.readValue(coordsStr, List.class);
	                combined.addAll(coords);
	            } catch (Exception e) {
	                log.error("좌표 파싱 오류: ", e);
	            }
	        }
	    }
	    try {
	        return objectMapper.writeValueAsString(combined);
	    } catch (Exception e) {
	        log.error("좌표 직렬화 오류: ", e);
	        return null;
	    }
	}

}
