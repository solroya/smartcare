package com.nado.smartcare.food.service.impl;

import java.util.ArrayList;
import java.util.Collections;
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
import com.nado.smartcare.patient.domain.PatientRecordCard;
import com.nado.smartcare.patient.repository.PatientRecordCardRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class CategoryServiceImpl implements ICategoryService {
	
	private final ICategoryRepository iCategoryRepository;
	private final PatientRecordCardRepository patientRecordCardRepository;
	
	@Override
	public List<CategoryDTO> getCategoriesForMember(Long memberNo) {
		log.info("getCategoriesForMember에 들어왔나?");
		List<PatientRecordCard> records = patientRecordCardRepository.findByMember_MemberNo(memberNo);
		if (records.isEmpty()) {
			log.info("회원 {}의 진료 기록이 없습니다.", memberNo);
			return Collections.emptyList();
		}
		
		Set<String> userDiagnosis = records.stream()
	            .map(record -> record.getEmployee().getDepartment().getDepartmentName())
	            .collect(Collectors.toSet());
		
		Map<String, List<String>> diagnosisToCategories = new HashMap<>();
		diagnosisToCategories.put(SampleDiagnosisType.SMAPLE_INTERNAL_MEDICINE.getSampleName(), List.of("한식", "죽", "샐러드", "디저트"));
		diagnosisToCategories.put(SampleDiagnosisType.SMAPLE_SURGERY.getSampleName(), List.of("한식", "죽", "샐러드"));
		diagnosisToCategories.put(SampleDiagnosisType.SMAPLE_PSYCHIATRY.getSampleName(), List.of("샐러드", "디저트"));
		
		Set<String> recommendedCategoryNames = new HashSet<>();
		for (String diag : userDiagnosis) {
			List<String> cats = diagnosisToCategories.get(diag);
			if (cats != null) {
				recommendedCategoryNames.addAll(cats);
			}
		}
		
		List<Category> allCategories = iCategoryRepository.findAll();
		List<CategoryDTO> result = recommendedCategoryNames.stream()
				.map(categoryName -> allCategories.stream()
						.filter(category -> category.getCategoryName().equals(categoryName))
						.findFirst()
						.map(this::entityToDTO)
						.orElse(null))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		
		log.info("회원 {}에게 추천된 음식 카테고리 : {}", memberNo, result);
		
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
