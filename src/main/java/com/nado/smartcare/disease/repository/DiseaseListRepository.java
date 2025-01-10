package com.nado.smartcare.disease.repository;

import com.nado.smartcare.disease.domain.DiseaseList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiseaseListRepository extends JpaRepository<DiseaseList, Long> {
    List<DiseaseList> findByDiseaseCategory_DiseaseCategoryNo(Long diseaseCategoryNo);
}
