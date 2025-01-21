package com.nado.smartcare.food.DataInitializer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.nado.smartcare.food.entity.Category;
import com.nado.smartcare.food.repository.ICategoryRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner{
	
	private final ICategoryRepository iCategoryRepository;

	@Override
	public void run(String... args) throws Exception {
		if (!iCategoryRepository.existsByCategoryName("한식")) {
            iCategoryRepository.save(Category.builder().categoryName("한식").categoryImage("/img/health/category01.png").build());
        }
        if (!iCategoryRepository.existsByCategoryName("죽")) {
            iCategoryRepository.save(Category.builder().categoryName("죽").categoryImage("/img/health/category02.png").build());
        }
        if (!iCategoryRepository.existsByCategoryName("샐러드")) {
            iCategoryRepository.save(Category.builder().categoryName("샐러드").categoryImage("/img/health/category03.png").build());
        }
        if (!iCategoryRepository.existsByCategoryName("디저트")) {
            iCategoryRepository.save(Category.builder().categoryName("디저트").categoryImage("/img/health/category04.png").build());
        }
	}
	
}
 