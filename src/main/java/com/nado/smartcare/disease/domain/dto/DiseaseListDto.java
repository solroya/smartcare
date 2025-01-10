package com.nado.smartcare.disease.domain.dto;

import com.nado.smartcare.disease.domain.DiseaseList;

public record DiseaseListDto (
        Long diseaseListNo,
        String diseaseName
) {

    public static DiseaseListDto from(DiseaseList diseaseList) {
        return new DiseaseListDto(
                diseaseList.getDiseaseListNo(),
                diseaseList.getDiseaseName()
        );
    }
}
