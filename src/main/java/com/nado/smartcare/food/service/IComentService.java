package com.nado.smartcare.food.service;

import java.util.List;

import com.nado.smartcare.food.dto.ComentDTO;


public interface IComentService {
	
	List<ComentDTO> getComentsByFoodPlace(Long foodPlaceId);
	
	ComentDTO addComent(Long foodPlaceId, ComentDTO comentDTO);
	
	double calculateAverageRating(Long foodPlaceId);
}
