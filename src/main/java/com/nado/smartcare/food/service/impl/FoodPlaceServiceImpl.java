package com.nado.smartcare.food.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nado.smartcare.food.dto.FoodPlaceDTO;
import com.nado.smartcare.food.entity.FoodPlace;
import com.nado.smartcare.food.repository.IFoodPlaceRepository;
import com.nado.smartcare.food.service.IFoodPlaceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class FoodPlaceServiceImpl implements IFoodPlaceService {
	
	private final IFoodPlaceRepository iFoodPlaceRepository;
	
	@Override
	public List<FoodPlaceDTO> getFoodPlaceByCategory(String category) {
		log.info("FoodPlace서비스쪽에 들어갔나?");
		return iFoodPlaceRepository.findByCategory(category).stream()
			.map(this::EntityToDto)
			.collect(Collectors.toList());
	}
	
	private FoodPlaceDTO EntityToDto(FoodPlace foodPlace) {
		return FoodPlaceDTO.builder()
			.fno(foodPlace.getFno())
			.foodName(foodPlace.getFoodName())
			.foodAddressRoad(foodPlace.getFoodAddressRoad())
			.foodAddressJibun(foodPlace.getFoodAddressJibun())
			.foodBusinessHours(foodPlace.getFoodbusinessHours())
			.foodPhoneNumber(foodPlace.getFoodPhoneNumber())
			.latitude(foodPlace.getLatitude())
			.longitude(foodPlace.getLongitude())
			.likes(foodPlace.getLikes())
			.views(foodPlace.getViews())
			.category(foodPlace.getCategory())
			.imageUrl(foodPlace.getImageUrl())
			.build();
	}
	
	
}
