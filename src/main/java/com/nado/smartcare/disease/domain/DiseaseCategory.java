package com.nado.smartcare.disease.domain;

import com.nado.smartcare.disease.domain.dto.DiseaseCategoryDto;
import com.nado.smartcare.patient.domain.PatientRecordCard;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Getter
@Entity
public class DiseaseCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long diseaseCategoryNo;

    private String categoryName;

    @OneToMany(mappedBy = "diseaseCategory")
    private List<DiseaseList> diseaseLists;

    @OneToMany(mappedBy = "diseaseCategory", cascade = CascadeType.ALL)
    private List<PatientRecordCard> patientRecordCards; // 이 카테고리에 해당하는 진료 기록

    public static DiseaseCategory of(DiseaseCategoryDto dto) {
        DiseaseCategory diseaseCategory = new DiseaseCategory();
        diseaseCategory.diseaseCategoryNo = dto.diseaseCategoryNo();
        diseaseCategory.categoryName = dto.categoryName();
        return diseaseCategory;
    }
}
