package com.nado.smartcare.disease.controller;

import com.nado.smartcare.disease.domain.dto.DiseaseListDto;
import com.nado.smartcare.disease.service.DiseaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/disease-list")
@RequiredArgsConstructor
public class DiseaseController {
    private final DiseaseService diseaseService;

    @GetMapping("/{categoryNo}")
    public List<DiseaseListDto> getDiseaseListByCategory(@PathVariable Long categoryNo) {
        return diseaseService.findByCategoryId(categoryNo);
    }

}
