package com.nado.smartcare.disease.service.impl;

import com.nado.smartcare.disease.domain.DiseaseCategory;
import com.nado.smartcare.disease.domain.DiseaseList;
import com.nado.smartcare.disease.domain.dto.DiseaseCategoryDto;
import com.nado.smartcare.disease.domain.dto.DiseaseListDto;
import com.nado.smartcare.disease.repository.DiseaseCategoryRepository;
import com.nado.smartcare.disease.repository.DiseaseListRepository;
import com.nado.smartcare.disease.service.DiseaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class DiseaseServiceImpl implements DiseaseService {

    private final DiseaseCategoryRepository diseaseCategoryRepository;

    private final DiseaseListRepository diseaseListRepository;

    @Override
    public List<DiseaseCategoryDto> getAllDiseaseCategories() {
        List<DiseaseCategory> categories = diseaseCategoryRepository.findAll();
        if (categories.isEmpty()) {
            log.warn("No disease categories found in the database.");
        } else {
            categories.forEach(category -> log.info(category.toString()));
        }
        return categories.stream()
                .map(DiseaseCategoryDto::to)
                .toList();
    }

    @Override
    public List<DiseaseListDto> findByCategoryId(Long categoryId) {
        List<DiseaseList> diseaseLists = diseaseListRepository.findByDiseaseCategory_DiseaseCategoryNo(categoryId);
        log.info("Fetched disease lists: {}", diseaseLists); // 디버깅 로그
        return diseaseLists.stream()
                .map(diseaseList -> new DiseaseListDto(diseaseList.getDiseaseListNo(), diseaseList.getDiseaseName()))
                .collect(Collectors.toList());
    }

}
