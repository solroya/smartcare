package com.nado.smartcare.food.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nado.smartcare.food.entity.Category;

@Repository
public interface ICategoryRepository extends JpaRepository<Category, Long>{
	boolean existsByCategoryName(String categoryName);
}
