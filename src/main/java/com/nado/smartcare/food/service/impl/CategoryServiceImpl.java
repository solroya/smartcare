package com.nado.smartcare.food.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nado.smartcare.food.dto.CategoryDTO;
import com.nado.smartcare.food.entity.Category;
import com.nado.smartcare.food.entity.SampleDiagnosisType;
import com.nado.smartcare.food.repository.ICategoryRepository;
import com.nado.smartcare.food.service.ICategoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class CategoryServiceImpl implements ICategoryService {
	
	private final ICategoryRepository iCategoryRepository;
	
	@Override
	public List<CategoryDTO> getSampleDiagnosisWithCategories() {
		log.info("CategoryService에 들어왔나?");
		
		List<Category> allCategories = iCategoryRepository.findAll();
		log.info("카테고리 다 뽑아왔나? ==> {}", allCategories);
		
		Map<String, List<String>> diagnosisToCategories = new HashMap<>();
		diagnosisToCategories.put(SampleDiagnosisType.SMAPLE_INTERNAL_MEDICINE.getSampleName(), List.of("한식", "죽", "샐러드", "디저트"));
		diagnosisToCategories.put(SampleDiagnosisType.SMAPLE_SURGERY.getSampleName(), List.of("한식", "죽", "샐러드"));
		diagnosisToCategories.put(SampleDiagnosisType.SMAPLE_PSYCHIATRY.getSampleName(), List.of("샐러드", "디저트"));
		
		List<String> userDiagnosis = List.of(
			SampleDiagnosisType.SMAPLE_INTERNAL_MEDICINE.getSampleName()
		);
		
		Set<String> uniqueCategoryNames = new HashSet<>();
		for (String diagnosis : userDiagnosis) {
			List<String> categories = diagnosisToCategories.getOrDefault(diagnosis, new ArrayList<>());
			uniqueCategoryNames.addAll(categories);
		}
		
		List<CategoryDTO> result = uniqueCategoryNames.stream()
			.map(categoryName -> allCategories.stream()
				.filter(category -> category.getCategoryName().equals(categoryName))
				.findFirst()
				.map(this::entityToDTO)
				.orElse(null))
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
		
		log.info("최종 결과 카테고리: {}", result);
		
		return result;
	}

	@Override
	public CategoryDTO getCategoryByName(String categoryName) {
		return iCategoryRepository.findByCategoryName(categoryName)
			.map(category -> new CategoryDTO(
				category.getCategoryNo(),
				category.getCategoryName(),
				category.getCategoryImage()))
			.orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
	}
	
}
