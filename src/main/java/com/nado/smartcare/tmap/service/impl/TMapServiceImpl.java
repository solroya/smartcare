package com.nado.smartcare.tmap.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.nado.smartcare.tmap.dto.RouteDTO;
import com.nado.smartcare.tmap.service.ITMapService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class TMapServiceImpl implements ITMapService {
    
	private final RestTemplate restTemplate;

    @Value("${tmap.api.key}")
    private String tmapApiKey;

    private static final int MAX_WALK_TIME = 60;
    private static final int MAX_BUS_TIME = 120;
    private static final int MAX_TRANSFER = 2;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<RouteDTO> getWalkingRoutes(double startLat, double startLng, double endLat, double endLng) {
        log.info("getWalkingRoutes 호출: 출발({}, {}), 도착({}, {})", startLat, startLng, endLat, endLng);

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

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        log.info("TMap 도보 API 요청 : {}", requestBody);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            List<RouteDTO> routes = parseRoutes(responseEntity.getBody());

            routes = routes.stream()
                    .filter(route -> route.getDistance() > 0 && route.getTime() > 0)
                    .filter(route -> route.getDistance() <= 2000)
                    .filter(route -> route.getTime() <= MAX_WALK_TIME)
                    .collect(Collectors.toList());

            return routes;
        } catch (HttpClientErrorException e) {
            log.error("도보 경로 요청 오류 : {}", e.getResponseBodyAsString());
            return new ArrayList<>();
        }
    }

    @Override
    public List<RouteDTO> getBusRoutes(double startLat, double startLng, double endLat, double endLng) {
        log.info("getBusRoutes 호출: 출발({}, {}), 도착({}, {})", startLat, startLng, endLat, endLng);

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

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        log.info("TMap 버스 API POST 요청 : {}", requestBody);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            String response = responseEntity.getBody();
            log.info("TMap 버스 API 응답: {}", response);
            return parseBusRoutes(response);
        } catch (Exception e) {
            log.error("TMap 버스 API 호출 실패", e);
            return new ArrayList<>();
        }
    }

    private List<RouteDTO> parseRoutes(String responseBody) {
        List<RouteDTO> routes = new ArrayList<>();
        try {
            JsonNode features = objectMapper.readTree(responseBody).path("features");
            double totalDistance = 0;
            int totalApiTime = 0;
            List<List<Double>> allCoordinates = new ArrayList<>();

            for (JsonNode feature : features) {
                JsonNode properties = feature.path("properties");
                JsonNode geometry = feature.path("geometry");

                double distance = properties.path("distance").asDouble();
                int apiTime = properties.path("time").asInt();
                if (apiTime == 0) {
                    apiTime = 1;
                }
                totalDistance += distance;
                totalApiTime += apiTime;

                JsonNode coordsNode = geometry.path("coordinates");
                if (!coordsNode.isArray() || coordsNode.isNull()) {
                    log.warn("좌표값이 배열이 아님 (feature 건너뜀): {}", coordsNode);
                    continue;
                }

                List<List<Double>> coords = new ArrayList<>();
                if (coordsNode.size() > 0 && coordsNode.get(0).isNumber()) {
                    List<Double> singleCoord = new ArrayList<>();
                    for (JsonNode numNode : coordsNode) {
                        singleCoord.add(numNode.asDouble());
                    }
                    coords.add(singleCoord);
                } else {
                    for (JsonNode pairNode : coordsNode) {
                        if (!pairNode.isArray()) {
                            log.warn("좌표 쌍이 배열이 아님 (건너뜀): {}", pairNode);
                            continue;
                        }
                        List<Double> pair = new ArrayList<>();
                        for (JsonNode numNode : pairNode) {
                            pair.add(numNode.asDouble());
                        }
                        coords.add(pair);
                    }
                }
                
                if (!allCoordinates.isEmpty() && !coords.isEmpty()) {
                    List<Double> lastCoord = allCoordinates.get(allCoordinates.size() - 1);
                    List<Double> firstCoord = coords.get(0);
                    if (lastCoord.get(0).equals(firstCoord.get(0)) && lastCoord.get(1).equals(firstCoord.get(1))) {
                        coords.remove(0);
                    }
                }
                allCoordinates.addAll(coords);
            }

            int estimatedTime = estimateWalkingTime(totalDistance);
            int walkingTime = Math.max(totalApiTime, estimatedTime);

            routes.add(RouteDTO.builder()
                    .type("도보")
                    .description("총 도보 경로")
                    .distance(totalDistance)
                    .time(walkingTime)
                    .coordinates(objectMapper.writeValueAsString(allCoordinates))
                    .build());

            log.info("도보 경로 통합 완료: 총 거리 = {}m, API 시간 = {}분, 보정된 시간 = {}분",
                    totalDistance, totalApiTime, walkingTime);
        } catch (Exception e) {
            log.error("TMap 도보 응답 파싱 실패", e);
            throw new RuntimeException("TMap 도보 API 응답 처리 실패", e);
        }
        return routes;
    }

    private int estimateWalkingTime(double distanceInMeters) {
        double averageSpeed = 1.4; // m/s
        int estimatedSeconds = (int) (distanceInMeters / averageSpeed);
        int estimatedMinutes = estimatedSeconds / 60;
        return Math.max(estimatedMinutes, 5);
    }

    private List<RouteDTO> parseBusRoutes(String responseBody) {
        List<RouteDTO> busRoutes = new ArrayList<>();
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode itineraries = rootNode.path("metaData").path("plan").path("itineraries");

            for (JsonNode itinerary : itineraries) {
                int totalTimeSeconds = itinerary.path("totalTime").asInt();
                int totalTime = totalTimeSeconds / 60;
                int transferCount = itinerary.path("transferCount").asInt();

                if (totalTime > MAX_BUS_TIME || transferCount > MAX_TRANSFER) {
                    continue;
                }

                busRoutes.add(RouteDTO.builder()
                        .type("버스")
                        .description("총 소요 시간: " + totalTime + "분, 환승: " + transferCount + "회")
                        .time(totalTime)
                        .build());

                log.info("버스 경로 추가됨: {}분, 환승 {}회", totalTime, transferCount);
            }

            return busRoutes;
        } catch (Exception e) {
            log.error("버스 경로 파싱 실패", e);
            return new ArrayList<>();
        }
    }

    private boolean isValidCoordinate(double startLat, double startLng, double endLat, double endLng) {
        return startLat != 0 && startLng != 0 && endLat != 0 && endLng != 0;
    }

    @Override
    public int getWalkingTime(double startLat, double startLng, double endLat, double endLng) {
        log.info("getWalkingTime 호출");

        if (!isValidCoordinate(startLat, startLng, endLat, endLng)) {
            log.warn("유효하지 않은 좌표 입력됨");
            return -1;
        }

        String url = "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&appKey=" + tmapApiKey
                + "&startX=" + startLng
                + "&startY=" + startLat
                + "&endX=" + endLng
                + "&endY=" + endLat
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
}