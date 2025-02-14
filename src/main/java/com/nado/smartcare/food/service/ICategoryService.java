package com.nado.smartcare.food.service;


import java.util.List;
import java.util.Map;

import com.nado.smartcare.food.dto.CategoryDTO;
import com.nado.smartcare.food.entity.Category;

public interface ICategoryService {
	
	
	List<CategoryDTO> getCategoriesForMember(Long memberNo);
	
	CategoryDTO getCategoryByName(String categoryName);
	
	default Category dtoToEntity(CategoryDTO categoryDTO) {
		return Category.builder()
				.categoryNo(categoryDTO.getCategoryNo())
				.categoryName(categoryDTO.getCategoryName())
				.categoryImage(categoryDTO.getCategoryImage())
				.build();
	}
	
	default CategoryDTO entityToDTO(Category category) {
		return CategoryDTO.builder()
				.categoryNo(category.getCategoryNo())
				.categoryName(category.getCategoryName())
				.categoryImage(category.getCategoryImage())
				.build();
	}
	
}
