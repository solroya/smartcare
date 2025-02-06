package com.nado.smartcare.bus.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nado.smartcare.food.dto.FoodPlaceDTO;
import com.nado.smartcare.food.service.IFoodPlaceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
@RequestMapping("/bus")
@RequiredArgsConstructor
public class BusController {
	
	private final IFoodPlaceService iFoodPlaceService;
	
	@Value("${kakao.scriptApi.key}")
    private String scriptKey;
    
    @GetMapping("/map")
    public String showBusMap(
    		@RequestParam("fno") Long fno,
            @RequestParam("startLat") double startLat,
            @RequestParam("startLng") double startLng,
            @RequestParam("foodLat") double foodLat,
            @RequestParam("foodLng") double foodLng,
            Model model) {
    	log.info("BusController - showBusList 호출: fno={}, startLat={}, startLng={}, foodLat={}, foodLng={}",
                fno, startLat, startLng, foodLat, foodLng);
        
    	FoodPlaceDTO foodPlace = iFoodPlaceService.getFoodPlaceById(fno);
    	model.addAttribute("foodPlace", foodPlace);
        model.addAttribute("scriptKey", scriptKey);
        model.addAttribute("startLat", startLat);
        model.addAttribute("startLng", startLng);
        model.addAttribute("foodLat", foodLat);
        model.addAttribute("foodLng", foodLng);
        return "bus/list";
    }
}
