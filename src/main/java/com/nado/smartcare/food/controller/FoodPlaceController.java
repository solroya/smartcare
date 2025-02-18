package com.nado.smartcare.food.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nado.smartcare.food.dto.CategoryDTO;
import com.nado.smartcare.food.dto.ComentDTO;
import com.nado.smartcare.food.dto.FoodPlaceDTO;
import com.nado.smartcare.food.service.ICategoryService;
import com.nado.smartcare.food.service.IComentService;
import com.nado.smartcare.food.service.IFoodPlaceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/food")
public class FoodPlaceController {
	
	@Value("${kakao.scriptApi.key}")
	private String kakaoScriptApiKey;
	
	private final IFoodPlaceService iFoodPlaceService;
	private final ICategoryService iCategoryService;
	private final IComentService iComentService;
	
	
	@GetMapping("/list/{category}")
	public String getFoodPlaceList(@PathVariable("category") String category, Model model) {
		log.info("FoodPlace컨트롤러에 들어왔나?");
		List<FoodPlaceDTO> foodPlaces = iFoodPlaceService.getFoodPlaceByCategory(category);
		
		CategoryDTO selectedCategory = iCategoryService.getCategoryByName(category);
		
		model.addAttribute("foodPlaces", foodPlaces);
		model.addAttribute("currentCategory", category);
		model.addAttribute("selectedCategory", selectedCategory);
		model.addAttribute("scriptKey", kakaoScriptApiKey);
		
		log.info("선택된 카테고리 정보는? ==> {}", selectedCategory);
		
		return "food/list";
	}
	
	@GetMapping("/location")
	@ResponseBody
	public List<FoodPlaceDTO> getAllFoodPlaces() {
	    log.info("음식점 위치 정보를 JSON으로 반환합니다.");
	    
	    List<FoodPlaceDTO> foodPlaces = iFoodPlaceService.getAllFoodPlaces();
	    foodPlaces.forEach(food -> log.info("📌 음식점 데이터: {}, {}, {}", food.getFoodName(), food.getLatitude(), food.getLongitude()));

	    return foodPlaces;
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
		log.info("FoodPlaceDetail에 들어왔나? fno={}", fno);
		
//		iFoodPlaceService.incrementViews(fno);
		log.info("조회수 증가 메서드 실행.....");

		FoodPlaceDTO foodPlace = iFoodPlaceService.getFoodPlaceById(fno);
		List<ComentDTO> coments = iComentService.getComentsByFoodPlace(fno);
		double averageRating = iComentService.calculateAverageRating(fno);
		
		model.addAttribute("foodPlace", foodPlace);
		model.addAttribute("coments", coments);
		model.addAttribute("averageRating", averageRating);
		
		log.info("상세 정보, 댓글 정보, 평점 정보, ==> {}, {}, {}", foodPlace, coments, averageRating);
		
		return "fragments/detail :: detailContent";
	}
	
	@PostMapping("/views/{fno}")
	@ResponseBody
	public ResponseEntity<Void> incrementViews(@PathVariable("fno") Long fno) {
		log.info("조회수 증가 API 컨트롤러 호출 - fno: {}", fno);
		iFoodPlaceService.incrementViews(fno);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/coment/{fno}")
	@ResponseBody
	public ResponseEntity<?> addComent(@PathVariable("fno") Long fno, @RequestBody ComentDTO comentDTO) {
		log.info("음식점 ID {}에 댓글 추가 : {}", fno, comentDTO);
		ComentDTO savedComent = iComentService.addComent(fno, comentDTO);
		double averageRating = iComentService.calculateAverageRating(fno);
		
		Map<String, Object> response = new HashMap<>();
		response.put("savedComent", savedComent);
		response.put("averageRating", averageRating);
		
		return ResponseEntity.ok(response);
	}
	
}
