package com.nado.smartcare.food.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nado.smartcare.food.dto.FoodPlaceDTO;
import com.nado.smartcare.food.service.IFoodPlaceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/food")
public class FoodPlaceController {
	
	private final IFoodPlaceService iFoodPlaceService;
	
	@GetMapping("/list/{category}")
	public String getFoodPlaceList(@PathVariable("category") String category, Model model) {
		log.info("FoodPlace컨트롤러에 들어왔나?");
		List<FoodPlaceDTO> foodPlaces = iFoodPlaceService.getFoodPlaceByCategory(category);
		model.addAttribute("foodPlaces", foodPlaces);
		model.addAttribute("currentCategory", category);
		log.info("{}관련 음식점 리스트는? ==> {}", category, foodPlaces);
		return "food/list";
	}
	
}
