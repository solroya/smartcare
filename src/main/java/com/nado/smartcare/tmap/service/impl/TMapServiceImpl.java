package com.nado.smartcare.tmap.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nado.smartcare.bus.entity.BusStop;
import com.nado.smartcare.bus.entity.LineStation;
import com.nado.smartcare.bus.repository.ILineStationRepository;
import com.nado.smartcare.tmap.dto.RouteDTO;
import com.nado.smartcare.tmap.service.ITMapService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class TMapServiceImpl implements ITMapService {
	
	private final RestTemplate restTemplate;
	private final ILineStationRepository iLineStationRepository;
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Value("${tmap.api.key}")
	private String tmapApiKey;
	
	private static final int MAX_WALK_TIME = 60;
	private static final int MAX_BUS_TIME = 120;
	private static final int MAX_TRANSFER = 2;
	
	
	@Override
	public List<RouteDTO> getWalkingRoutes(double startLat, double startLng, double endLat, double endLng) {
	    log.info("getWalkingRoutes 호출됨");
	    if (!isValidCoordinate(startLat, startLng, endLat, endLng)) {
	        log.warn("유효하지 않은 좌표 입력됨");
	        return new ArrayList<>();
	    }
	    
	    String url = "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1";
	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Accept", "application/json");
	    headers.set("appKey", tmapApiKey);
	    headers.set("Content-Type", "application/json");
	    
	    Map<String, Object> requestBody = new HashMap<>();
	    requestBody.put("startX", String.format("%.7f", startLng));
	    requestBody.put("startY", String.format("%.7f", startLat));
	    requestBody.put("endX", String.format("%.7f", endLng));
	    requestBody.put("endY", String.format("%.7f", endLat));
	    requestBody.put("reqCoordType", "WGS84GEO");
	    requestBody.put("resCoordType", "WGS84GEO");
	    requestBody.put("searchOption", "0");
	    requestBody.put("count", "3");
	    requestBody.put("format", "json");
	    requestBody.put("lang", "0");
	    requestBody.put("startName", "출발지");
	    requestBody.put("endName", "도착지");
	    
	    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
	    log.info("TMap 도보 API 요청 : {}", requestBody);
	    
	    try {
	        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
	        List<RouteDTO> routes = parseWalkingRoutes(response.getBody());
	        return routes.stream()
	                .filter(route -> route.getDistance() > 0 && route.getTime() > 0)
	                .filter(route -> route.getDistance() <= 7000)
	                .filter(route -> route.getTime() <= MAX_WALK_TIME)
	                .collect(Collectors.toList());
	    } catch (HttpClientErrorException e) {
	        log.error("도보 경로 요청 오류 : {}", e.getResponseBodyAsString());
	        return new ArrayList<>();
	    }
	}

	@Override
	public List<RouteDTO> getBusRoutes(double startLat, double startLng, double endLat, double endLng) {
	    log.info("getBusRoutes 호출됨");
	    if (!isValidCoordinate(startLat, startLng, endLat, endLng)) {
	        log.warn("유효하지 않은 좌표 입력됨");
	        return new ArrayList<>();
	    }
	    
	    String url = "https://apis.openapi.sk.com/transit/routes";
	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Accept", "application/json");
	    headers.set("appKey", tmapApiKey);
	    headers.set("Content-Type", "application/json");
	    
	    Map<String, Object> requestBody = new HashMap<>();
	    requestBody.put("startX", String.format("%.7f", startLng));
	    requestBody.put("startY", String.format("%.7f", startLat));
	    requestBody.put("endX", String.format("%.7f", endLng));
	    requestBody.put("endY", String.format("%.7f", endLat));
	    requestBody.put("reqCoordType", "WGS84GEO");
	    requestBody.put("resCoordType", "WGS84GEO");
	    requestBody.put("count", "3");
	    requestBody.put("format", "json");
	    requestBody.put("lang", "0");
	    requestBody.put("startName", "출발지");
	    requestBody.put("endName", "도착지");
	    requestBody.put("searchOption", "0");
	    requestBody.put("searchDttm", "202502041200");
	    
	    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
	    log.info("TMap 버스 API POST 요청 : {}", requestBody);
	    
	    try {
	        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
	        log.info("TMap 버스 API 응답 : {}", response.getBody());
	        return parseBusRoutes(response.getBody());
	    } catch (Exception e) {
	        log.error("TMap 버스 API 호출 실패", e);
	        return new ArrayList<>();
	    }
	}
	
	private List<RouteDTO> parseWalkingRoutes(String responseBody) {
		List<RouteDTO> routes = new ArrayList<>();
		try {
			JsonNode features = objectMapper.readTree(responseBody).path("features");
			double totalDistance = 0;
			int totalApiTime = 0;
			List<List<Double>> allCoords = new ArrayList<>();
			
			for (JsonNode feature : features) {
	            JsonNode properties = feature.path("properties");
	            JsonNode geometry = feature.path("geometry");

	            double distance = properties.path("distance").asDouble();
	            int apiTime = properties.path("time").asInt();
	            if(apiTime > 0) {
	                apiTime = apiTime / 60;
	                if(apiTime == 0) {
	                    apiTime = 1;
	                }
	            } else {
	                apiTime = 1;
	            }
	            totalDistance += distance;
	            totalApiTime += apiTime;
				
				JsonNode coordsNode = geometry.path("coordinates");
				List<List<Double>> coords = new ArrayList<>();
				if (coordsNode.size() > 0 && coordsNode.get(0).isNumber()) {
					List<Double> single = new ArrayList<>();
					for (JsonNode num : coordsNode) {
						single.add(num.asDouble());
					}
					coords.add(single);
				} else {
					for (JsonNode pair : coordsNode) {
						List<Double> pairList = new ArrayList<>();
						for (JsonNode num : pair) {
							pairList.add(num.asDouble());
						}
						coords.add(pairList);
					}
				}
				
				if (!allCoords.isEmpty() && !coords.isEmpty()) {
					List<Double> last = allCoords.get(allCoords.size() - 1);
					List<Double> first = coords.get(0);
					if (last.get(0).equals(first.get(0)) && last.get(1).equals(first.get(1))) {
						coords.remove(0);
					}
				}
				allCoords.addAll(coords);
			}
			
			int estimatedTime = estimateWalkingTime(totalDistance);
			int walkingTime = Math.max(totalApiTime, estimatedTime);
			
			routes.add(RouteDTO.builder()
					.type("도보")
					.description("총 도보 경로")
					.distance(totalDistance)
					.time(walkingTime)
					.coordinates(objectMapper.writeValueAsString(allCoords))
					.build());
			
			log.info("도보 경로 통합 완료 : 총 거리 = {}m, API 시간 = {}분, 보정된 시간 = {}분", totalDistance, totalApiTime, walkingTime);
		} catch (Exception e) {
			log.error("TMap 도보 응답 파싱 실패", e);
			throw new RuntimeException("TMap 도보 API 응답 처리 실패", e);
		}
		return routes;
	}
	
	private int estimateWalkingTime(double distanceInMeters) {
		double avgSpeed = 1.4;
		int estimatedSeconds = (int) (distanceInMeters / avgSpeed);
		int estimatedMinutes = estimatedSeconds / 60;
		return Math.max(estimatedMinutes, 5);
	}
	
	private List<RouteDTO> parseBusRoutes(String responseBody) {
	    List<RouteDTO> routes = new ArrayList<>();
	    try {
	        JsonNode root = objectMapper.readTree(responseBody);
	        JsonNode itineraries = root.path("metaData").path("plan").path("itineraries");
	        for (JsonNode itinerary : itineraries) {
	            int totalTimeSec = itinerary.path("totalTime").asInt();
	            int totalTime = totalTimeSec / 60;
	            int transferCount = itinerary.path("transferCount").asInt();
	            double totalDistance = itinerary.path("totalDistance").asDouble(0.0);
	            
	            if (totalTime > MAX_BUS_TIME || transferCount > MAX_TRANSFER) {
	                continue;
	            }
	            
	            String coords = itinerary.path("passShape").path("linestring").asText(null);
	            if (coords == null || coords.trim().isEmpty()) {
	                int routeId = itinerary.path("routeId").asInt();
	                coords = getBusRouteCoordinatesFromDB(routeId);
	            }
	            
	            routes.add(RouteDTO.builder()
	                    .type("버스")
	                    .description("총 소요 시간 : " + totalTime + "분, 환승: " + transferCount + "회, 거리: " + totalDistance + "m")
	                    .time(totalTime)
	                    .distance(totalDistance)
	                    .coordinates(coords)
	                    .build());
	            log.info("버스 경로 추가됨 : {}분, 환승 {}회, 거리 {}m", totalTime, transferCount, totalDistance);
	        }
	        return routes;
	    } catch (Exception e) {
	        log.error("버스 경로 파싱 실패", e);
	        return new ArrayList<>();
	    }
	}
	
	private boolean isValidCoordinate(double sLat, double sLng, double eLat, double eLng) {
		return sLat != 0 && sLng != 0 && eLat != 0 && eLng != 0;
	}
	
	@Override
	public int getWalkingTime(double sLat, double sLng, double eLat, double eLng) {
		log.info("getWalkingTime들어왔나?");
		if (!isValidCoordinate(sLat, sLng, eLat, eLng)) {
			log.warn("유효하지 않은 좌표 입력됨");
			return -1;
		}
		String url = "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&appKey=" + tmapApiKey
                + "&startX=" + sLng + "&startY=" + sLat
                + "&endX=" + eLat + "&endY=" + eLat
                + "&reqCoordType=WGS84GEO&resCoordType=WGS84GEO&format=json";
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
			JsonNode features = objectMapper.readTree(response.getBody()).get("features");
			int totalTime = 0;
			if (features != null && features.isArray()) {
				for (JsonNode feature : features) {
					totalTime = feature.get("properties").get("totalTime").asInt() / 60;
					break;
				}
			}
			return (totalTime > 0 && totalTime <= MAX_WALK_TIME) ? totalTime : -1;
		} catch (Exception e) {
			log.error("TMap 도보 경로 API 호출 오류", e);
			return -1;
		}
	}
	
	private String getBusRouteCoordinatesFromDB(int routeId) {
	    List<LineStation> stations = iLineStationRepository.findByLineIdOrderByIdAsc(routeId);
	    List<List<Double>> coords = new ArrayList<>();
	    
	    for (LineStation station : stations) {
	        if (station.getBusStopAndLineStations() != null && !station.getBusStopAndLineStations().isEmpty()) {
	            BusStop busStop = station.getBusStopAndLineStations().get(0).getBusStop();
	            if (busStop != null) {
	                List<Double> point = new ArrayList<>();
	                point.add(busStop.getLongitude());
	                point.add(busStop.getLatitude());
	                coords.add(point);
	            }
	        }
	    }
	    
	    try {
	        return objectMapper.writeValueAsString(coords);
	    } catch (Exception e) {
	        log.error("DB 좌표 직렬화 오류: ", e);
	        return null;
	    }
	}
    
	
}