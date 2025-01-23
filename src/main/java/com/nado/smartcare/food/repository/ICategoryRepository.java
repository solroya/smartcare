package com.nado.smartcare.food.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nado.smartcare.food.dto.CategoryDTO;
import com.nado.smartcare.food.entity.Category;

@Repository
public interface ICategoryRepository extends JpaRepository<Category, Long>{
	boolean existsByCategoryName(String categoryName);
	
	Optional<Category> findByCategoryName(String categoryName);
}
