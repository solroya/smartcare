package com.nado.smartcare.food.service;

import java.util.List;

import com.nado.smartcare.food.dto.FoodPlaceDTO;

public interface IFoodPlaceService {
	List<FoodPlaceDTO> getFoodPlaceByCategory(String category);
}
