package com.nado.smartcare.disease.domain;

import com.nado.smartcare.disease.domain.dto.DiseaseListDto;
import com.nado.smartcare.patient.domain.PatientRecordCard;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Getter
@Entity
public class DiseaseList {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long diseaseListNo;

    private String diseaseName;

    @ManyToOne
    @JoinColumn(name = "disease_category_no")
    private DiseaseCategory diseaseCategory;

    @OneToMany(mappedBy = "disease", cascade = CascadeType.ALL)
    private List<PatientRecordCard> patientRecordCards; // 이 질병에 해당하는 진료 기록

    public static DiseaseList of(DiseaseListDto dto) {
        DiseaseList diseaseList = new DiseaseList();
        diseaseList.diseaseListNo = dto.diseaseListNo();
        diseaseList.diseaseName = dto.diseaseName();
        return diseaseList;
    }
}
