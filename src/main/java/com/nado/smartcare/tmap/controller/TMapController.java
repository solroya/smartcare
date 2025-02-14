package com.nado.smartcare.tmap.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nado.smartcare.tmap.service.ITMapService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/routes")
@RequiredArgsConstructor
@Log4j2
public class TMapController {
	
	private final ITMapService itMapService;
	
	@GetMapping("/recommend")
    public String recommendRoutes(
            @RequestParam("startX") String startX,
            @RequestParam("startY") String startY,
            @RequestParam("endX") String endX,
            @RequestParam("endY") String endY) {
		log.info("recommendRoutes에 들어왔나??");
        log.info("추천경로 요청: start=({}, {}), end=({}, {})", startX, startY, endX, endY);
        String routes = itMapService.getTransitRoutes(startX, startY, endX, endY);
        return routes;
	}
	
}
