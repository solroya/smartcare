package com.nado.smartcare.food.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nado.smartcare.food.dto.ComentDTO;
import com.nado.smartcare.food.entity.Coment;
import com.nado.smartcare.food.entity.FoodPlace;
import com.nado.smartcare.food.repository.IComentRepository;
import com.nado.smartcare.food.repository.IFoodPlaceRepository;
import com.nado.smartcare.food.service.IComentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class ComentServiceImpl implements IComentService {
	
	private final IComentRepository iComentRepository;
	private final IFoodPlaceRepository iFoodPlaceRepository;
	
	@Override
	public List<ComentDTO> getComentsByFoodPlace(Long foodPlaceId) {
		log.info("getComentsByFoodPlace서비스를 탔나?");
		
		List<Coment> coments = iComentRepository.findByFoodPlace_FnoOrderByCreatedAtDesc(foodPlaceId);
	    log.info("조회된 댓글: {}", coments);
		return iComentRepository.findByFoodPlace_FnoOrderByCreatedAtDesc(foodPlaceId)
					.stream()
					.map(coment -> ComentDTO.builder()
							.cno(coment.getCno())
							.content(coment.getContent())
							.author(coment.getAuthor())
							.rating(coment.getRating())
							.createdAt(coment.getCreatedAt())
							.updatedAt(coment.getUpdatedAt())
							.build())
					.collect(Collectors.toList());
	}

	@Override
	public ComentDTO addComent(Long foodPlaceId, ComentDTO comentDTO) {
		log.info("addComent서비스를 탔나?");
		FoodPlace foodPlace = iFoodPlaceRepository.findById(foodPlaceId)
				.orElseThrow(() -> new IllegalArgumentException("음식점을 찾을 수 없습니다."));
		log.info("foodPlaceId값은? ==> {}", foodPlaceId);
		
		Coment coment = Coment.builder()
				.foodPlace(foodPlace)
				.content(comentDTO.getContent())
				.author(comentDTO.getAuthor())
				.rating(comentDTO.getRating())
				.build();
		
		Coment savedComent = iComentRepository.save(coment);
		log.info("저장된 댓글 및 평점은? ==> {}", coment);
		
		return ComentDTO.builder()
				.cno(savedComent.getCno())
				.content(savedComent.getContent())
				.author(savedComent.getAuthor())
				.rating(savedComent.getRating())
				.createdAt(savedComent.getCreatedAt())
				.updatedAt(savedComent.getUpdatedAt())
				.build();
	}

	@Override
	public double calculateAverageRating(Long foodPlaceId) {
		log.info("음식점 ID {}의 평균 평점 계산", foodPlaceId);
		
		List<Coment> coments = iComentRepository.findByFoodPlace_FnoOrderByCreatedAtDesc(foodPlaceId);
		
		if (coments.isEmpty()) {
			return 0.0;
		}
		
		double totalRating = coments.stream()
				.mapToDouble(Coment::getRating)
				.sum();
		
		return totalRating / coments.size();
	}


}
