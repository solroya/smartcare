package com.nado.smartcare.health.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nado.smartcare.food.dto.CategoryDTO;
import com.nado.smartcare.food.service.ICategoryService;
import com.nado.smartcare.member.domain.Member;
import com.nado.smartcare.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/health")
@RequiredArgsConstructor
@Log4j2
public class HealthController {
	
	private final ICategoryService iCategoryService; 
	private final MemberRepository memberRepository;
	
	@GetMapping("/health")
	public String healthForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		log.info("healthController에 들어왔나?");
		try {
			Member currentMember = memberRepository.findByMemberId(userDetails.getUsername())
					.orElseThrow(() -> new IllegalArgumentException("Member not found"));
			log.info("currentMember의 값은? ==> {}", currentMember);
			
			List<CategoryDTO> categories = iCategoryService.getCategoriesForMember(currentMember.getMemberNo());
			log.info("회원 {}에게 추천된 카테고리: {}", currentMember.getMemberNo(), categories);
			
			List<String> categoryNames = categories.stream()
					.map(CategoryDTO::getCategoryName)
					.distinct()
					.collect(Collectors.toList());
			
			log.info("중복 제거된 카테고리 이름들 : {}", categoryNames);
			
			model.addAttribute("categories", categories);
			log.info("healthControll 끝났다.");
			
			return "health/health";
		} catch (Exception e) {
			return "member/login";
		}
	}
}
