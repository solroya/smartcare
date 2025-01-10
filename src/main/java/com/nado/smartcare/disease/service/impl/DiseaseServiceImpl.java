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
import java.util.Optional;
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
    public Optional<DiseaseCategoryDto> getDiseaseCategoryById(Long categoryNo) {
        return diseaseCategoryRepository.findById(categoryNo)
                .map(DiseaseCategoryDto::to);
    }

    @Override
    public List<DiseaseListDto> findByCategoryId(Long categoryId) {
        return diseaseListRepository.findByDiseaseCategory_DiseaseCategoryNo(categoryId)
                .stream()
                .map((diseaseList -> new DiseaseListDto(diseaseList.getDiseaseListNo(), diseaseList.getDiseaseName())))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DiseaseListDto> getDiseaseById(Long diseaseListNo) {
        return diseaseListRepository.findById(diseaseListNo)
                .map(DiseaseListDto::from); // 엔티티를 DTO로 변환
    }
}
