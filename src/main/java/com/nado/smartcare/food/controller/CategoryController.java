package com.nado.smartcare.food.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nado.smartcare.food.dto.CategoryDTO;
import com.nado.smartcare.food.service.ICategoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/api/diagnosis")
@RequiredArgsConstructor
@Log4j2
public class CategoryController {
	
	private final ICategoryService iCategoryService;
	
	@GetMapping("/categories")
	public String getDiagnosisWithCategories(Model model) {
		log.info("categoryController에 들어왔나?");
		List<CategoryDTO> categories = iCategoryService.getSampleDiagnosisWithCategories();
		log.info("컨트롤러에서 받아온 categories 데이터: {}", categories);
		model.addAttribute("categories", categories);
		return "health/health";
	}
}
