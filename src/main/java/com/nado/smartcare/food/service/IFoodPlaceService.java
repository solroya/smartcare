package com.nado.smartcare.food.service;

import java.util.List;

import com.nado.smartcare.food.dto.FoodPlaceDTO;

public interface IFoodPlaceService {
	List<FoodPlaceDTO> getFoodPlaceByCategory(String category);
	
	// 리스트 좋아요 기능
	boolean toggleLike(Long fno);
	int getLikesCount(Long fno);
	
	// 상세페이지 기능
	FoodPlaceDTO getFoodPlaceById(Long fno);
	
	// 조회수
	void incrementViews(Long fno);
	
	List<FoodPlaceDTO> getAllFoodPlaces();
}
