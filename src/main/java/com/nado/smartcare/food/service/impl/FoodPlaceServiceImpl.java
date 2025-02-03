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

	@Override
	public boolean toggleLike(Long fno) {
		log.info("toggleLike에 들어왔나?");
		FoodPlace foodPlace = iFoodPlaceRepository.findById(fno)
				.orElseThrow(() -> new IllegalArgumentException("음식점을 찾을 수 없습니다."));
		
		boolean isLiked = foodPlace.getLikes() > 0;
		if (isLiked) {
			foodPlace.setLikes(foodPlace.getLikes() - 1);
		} else {
			foodPlace.setLikes(foodPlace.getLikes() + 1);
		}
		
		iFoodPlaceRepository.save(foodPlace);
		
		return !isLiked;
	}

	@Override
	public int getLikesCount(Long fno) {
		log.info("getLikesCount에 들어왔나?");
		log.info("좋아요 개수 확인 메서드 실행 - 음식점 ID : {}", fno);
		FoodPlace foodPlace = iFoodPlaceRepository.findById(fno)
				.orElseThrow(() -> new IllegalArgumentException("음식점을 찾을 수 없습니다."));
		
		return foodPlace.getLikes();
	}

	@Override
	public FoodPlaceDTO getFoodPlaceById(Long fno) {
		return iFoodPlaceRepository.findById(fno)
			.map(this::EntityToDto)
			.orElseThrow(() -> new IllegalArgumentException("해당 음식점을 찾을 수 없습니다."));
	}

	@Override
	public void incrementViews(Long fno) {
		log.info("조회수 증가 요청 음식점 ID: {}", fno);
		FoodPlace foodPlace = iFoodPlaceRepository.findById(fno)
			.orElseThrow(() -> new IllegalArgumentException("음식점을 찾울 수 없습니다."));
		foodPlace.setViews(foodPlace.getViews() + 1);
		iFoodPlaceRepository.save(foodPlace);
	}
	
}
