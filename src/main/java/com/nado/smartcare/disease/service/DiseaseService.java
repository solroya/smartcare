package com.nado.smartcare.disease.service;

import com.nado.smartcare.disease.domain.DiseaseList;
import com.nado.smartcare.disease.domain.dto.DiseaseCategoryDto;
import com.nado.smartcare.disease.domain.dto.DiseaseListDto;

import java.util.List;
import java.util.Optional;

public interface DiseaseService {

    List<DiseaseCategoryDto> getAllDiseaseCategories();

    Optional<DiseaseCategoryDto> getDiseaseCategoryById(Long categoryNo);

    List<DiseaseListDto> findByCategoryId(Long categoryId);

    Optional<DiseaseListDto> getDiseaseById(Long diseaseListNo);
}
