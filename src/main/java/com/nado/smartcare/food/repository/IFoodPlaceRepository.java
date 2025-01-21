package com.nado.smartcare.food.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nado.smartcare.food.entity.FoodPlace;

@Repository
public interface IFoodPlaceRepository extends JpaRepository<FoodPlace, Long> {
	List<FoodPlace> findByCategory(String category);
}
