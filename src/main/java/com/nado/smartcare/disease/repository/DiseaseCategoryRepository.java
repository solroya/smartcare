package com.nado.smartcare.disease.repository;

import com.nado.smartcare.disease.domain.DiseaseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiseaseCategoryRepository extends JpaRepository<DiseaseCategory, Long> {
}
