package com.nado.smartcare.tmap.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nado.smartcare.tmap.dto.RecommendedRouteDTO;
import com.nado.smartcare.tmap.service.IRouteRecommendationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/routes")
@RequiredArgsConstructor
@Log4j2
public class RouteRecommendationController {
	
	private final IRouteRecommendationService iRouteRecommendationService;
	
	@GetMapping("/recommend")
    public ResponseEntity<List<RecommendedRouteDTO>> recommendRoutes(
            @RequestParam("startLat") double startLat,
            @RequestParam("startLng") double startLng,
            @RequestParam("endLat") double endLat,
            @RequestParam("endLng") double endLng) {
		log.info("recommendRoutes에 들어왔나??");
        log.info("추천경로 요청: start=({}, {}), end=({}, {})", startLat, startLng, endLat, endLng);
        List<RecommendedRouteDTO> routes = iRouteRecommendationService.recommendRoutes(startLat, startLng, endLat, endLng);
        return ResponseEntity.ok(routes);
    }
	
}
