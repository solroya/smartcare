package com.nado.smartcare.disease.domain.dto;

import com.nado.smartcare.disease.domain.DiseaseCategory;

import java.util.List;

public record DiseaseCategoryDto(
        Long diseaseCategoryNo,
        String categoryName,
        List<DiseaseListDto> diseaseLists
) {

    public static DiseaseCategoryDto to(DiseaseCategory diseaseCategory) {
        return new DiseaseCategoryDto(
                diseaseCategory.getDiseaseCategoryNo(),
                diseaseCategory.getCategoryName(),
                diseaseCategory.getDiseaseLists().stream()
                        .map(DiseaseListDto::from)
                        .toList()
        );
    }
}
