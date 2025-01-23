package com.nado.smartcare.food.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nado.smartcare.food.dto.CategoryDTO;
import com.nado.smartcare.food.dto.FoodPlaceDTO;
import com.nado.smartcare.food.service.ICategoryService;
import com.nado.smartcare.food.service.IFoodPlaceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/food")
public class FoodPlaceController {
	
	private final IFoodPlaceService iFoodPlaceService;
	private final ICategoryService iCategoryService;
	
	@GetMapping("/list/{category}")
	public String getFoodPlaceList(@PathVariable("category") String category, Model model) {
		log.info("FoodPlace컨트롤러에 들어왔나?");
		List<FoodPlaceDTO> foodPlaces = iFoodPlaceService.getFoodPlaceByCategory(category);
		
		CategoryDTO selectedCategory = iCategoryService.getCategoryByName(category);
		
		model.addAttribute("foodPlaces", foodPlaces);
		model.addAttribute("currentCategory", category);
		model.addAttribute("selectedCategory", selectedCategory);
		
		log.info("{}관련 음식점 리스트는? ==> {}", category, foodPlaces);
		log.info("선택된 카테고리 정보는? ==> {}", selectedCategory);
		
		return "food/list";
	}
	
	@PostMapping("/like/{fno}")
	@ResponseBody
	public ResponseEntity<?> likeFoodPlace(@PathVariable("fno") Long fno) {
		log.info("좋아요 요청 음식점 FNO는? ==> {}", fno);
		boolean newLikeStatus = iFoodPlaceService.toggleLike(fno);
		return ResponseEntity.ok(newLikeStatus);
	}
	
	
	@GetMapping("/detail/{fno}")
	public String getFoodPlaceDetail(@PathVariable("fno") Long fno, Model model) {
		log.info("FoodPlaceDetail에 들어왔나?");
		
		iFoodPlaceService.incrementViews(fno);
		FoodPlaceDTO foodPlace = iFoodPlaceService.getFoodPlaceById(fno);
		model.addAttribute("foodPlace", foodPlace);
		
		log.info("상세 정보 : {}", foodPlace);
		
		return "fragments/detail :: detailContent";
	}
	
	@PostMapping("/views/{fno}")
	@ResponseBody
	public ResponseEntity<Void> incrementViews(@PathVariable("fno") Long fno) {
		iFoodPlaceService.incrementViews(fno);
		return ResponseEntity.ok().build();
	}
	
}
