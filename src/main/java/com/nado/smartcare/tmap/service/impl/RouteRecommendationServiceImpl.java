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
        log.info("추천 경로 서비스 시작");

        List<RouteDTO> directWalkingRoutes = itMapService.getWalkingRoutes(startLat, startLng, endLat, endLng);
        directWalkingRoutes = directWalkingRoutes.stream()
                .filter(route -> route.getDistance() > 0 && route.getTime() >= 5)
                .collect(Collectors.toList());

        double directDistance = getDistance(startLat, startLng, endLat, endLng);
        if (directDistance <= 1000 && !directWalkingRoutes.isEmpty()) {
            log.info("출발지와 도착지의 거리가 {}m 이므로 도보만 추천", directDistance);
            return convertToRecommendedRoutes(directWalkingRoutes, "도보", "출발지 → 도착지");
        }

        BusStop nearestStartBusStop = iBusStopService.findNearestBusStop(startLat, startLng);
        BusStop nearestEndBusStop = iBusStopService.findNearestBusStop(endLat, endLng);

        if (nearestStartBusStop == null || nearestEndBusStop == null) {
            log.warn("가까운 버스 정류장을 찾을 수 없음, 도보 경로만 추천");
            return !directWalkingRoutes.isEmpty()
                    ? convertToRecommendedRoutes(directWalkingRoutes, "도보", "출발지 → 도착지")
                    : List.of();
        }

        log.info("출발지 정류장: {}, 목적지 정류장: {}",
                 nearestStartBusStop.getBusStopName(), nearestEndBusStop.getBusStopName());

        List<RouteDTO> walkingToBusStop = itMapService.getWalkingRoutes(
                startLat, startLng, nearestStartBusStop.getLatitude(), nearestStartBusStop.getLongitude()
        );
        List<RouteDTO> busRoutes = itMapService.getBusRoutes(
                nearestStartBusStop.getLatitude(), nearestStartBusStop.getLongitude(),
                nearestEndBusStop.getLatitude(), nearestEndBusStop.getLongitude()
        );
        List<RouteDTO> walkingToDestination = itMapService.getWalkingRoutes(
                nearestEndBusStop.getLatitude(), nearestEndBusStop.getLongitude(), endLat, endLng
        );

        if (!directWalkingRoutes.isEmpty()) {
            int walkingTime = directWalkingRoutes.get(0).getTime();
            int busTotalTime = getTotalBusTime(busRoutes, walkingToBusStop, walkingToDestination);
            if (walkingTime <= busTotalTime - 10 && walkingTime < 20) {
                log.info("도보가 더 빠름: 도보 추천");
                return convertToRecommendedRoutes(directWalkingRoutes, "도보", "출발지 → 도착지");
            }
        }

        log.info("버스 + 도보 이동 추천");
        List<RecommendedRouteDTO> allRoutes = new ArrayList<>();
        allRoutes.addAll(convertToRecommendedRoutes(walkingToBusStop, "도보", "출발지 → 버스 정류장"));
        allRoutes.addAll(convertToRecommendedRoutes(busRoutes, "버스", "버스 탑승"));
        allRoutes.addAll(convertToRecommendedRoutes(walkingToDestination, "도보", "버스 정류장 → 도착지"));

        return allRoutes.stream()
                .sorted(Comparator.comparingInt(RecommendedRouteDTO::getTime))
                .limit(3)
                .collect(Collectors.toList());
    }
    
    private List<RecommendedRouteDTO> convertToRecommendedRoutes(List<RouteDTO> routes, String type, String description) {
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

    private int getTotalBusTime(List<RouteDTO> busRoutes, List<RouteDTO> walkingToBusStop, List<RouteDTO> walkingToDestination) {
        int totalBusTime = busRoutes.isEmpty() ? Integer.MAX_VALUE : busRoutes.get(0).getTime();
        int totalWalkingTime =
            (walkingToBusStop.isEmpty() ? 0 : walkingToBusStop.get(0).getTime()) +
            (walkingToDestination.isEmpty() ? 0 : walkingToDestination.get(0).getTime());
        return totalBusTime + totalWalkingTime;
    }

    private double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double R = 6371e3;
        double latRad1 = Math.toRadians(lat1);
        double latRad2 = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLng = Math.toRadians(lng2 - lng1);
        
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(latRad1) * Math.cos(latRad2)
                * Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }

}
