package com.nado.smartcare.tmap.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nado.smartcare.food.dto.FoodPlaceDTO;
import com.nado.smartcare.food.service.IFoodPlaceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


@Controller
@Log4j2
@RequiredArgsConstructor
public class TMapPageController {
	
	private final IFoodPlaceService iFoodPlaceService;
	
	@Value("${kakao.scriptApi.key}")
    private String kakaoScriptApiKey;
	
	@GetMapping("/list")
	public String showBusRoutesPage(
		@RequestParam("fno") Long fno,
		@RequestParam("startLat") double startLat,
        @RequestParam("startLng") double startLng,
        @RequestParam("foodLat") double foodLat,
        @RequestParam("foodLng") double foodLng,
        Model model
	) {
		FoodPlaceDTO foodPlace = iFoodPlaceService.getFoodPlaceById(fno);
		
		if (foodPlace == null) {
	        throw new RuntimeException("해당 음식점을 찾을 수 없습니다: fno=" + fno);
	    }
		
		model.addAttribute("startLat", startLat);
        model.addAttribute("startLng", startLng);
        model.addAttribute("foodLat", foodLat);
        model.addAttribute("foodLng", foodLng);
        model.addAttribute("foodPlace", foodPlace);
        model.addAttribute("scriptKey", kakaoScriptApiKey);
        
        return "bus/list";
	}
	
	@GetMapping("/walking")
    public String showWalkingRoutesPage(
    	@RequestParam("fno") Long fno,
        @RequestParam("startLat") double startLat,
        @RequestParam("startLng") double startLng,
        @RequestParam("foodLat") double foodLat,
        @RequestParam("foodLng") double foodLng,
        Model model
    ) {
		FoodPlaceDTO foodPlace = iFoodPlaceService.getFoodPlaceById(fno);
		
		if (foodPlace == null) {
	        throw new RuntimeException("해당 음식점을 찾을 수 없습니다: fno=" + fno);
	    }
		
		model.addAttribute("fno", fno);
        model.addAttribute("startLat", startLat);
        model.addAttribute("startLng", startLng);
        model.addAttribute("foodLat", foodLat);
        model.addAttribute("foodLng", foodLng);
        model.addAttribute("foodPlace", foodPlace);
        model.addAttribute("scriptKey", kakaoScriptApiKey);
        return "bus/walking";
    }
	
}
