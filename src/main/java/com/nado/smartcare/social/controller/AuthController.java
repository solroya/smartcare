package com.nado.smartcare.social.controller;

import java.util.Map;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
@RequiredArgsConstructor
public class AuthController {
	
	@GetMapping("/auth/kakao/callback")
	public String kakaoCallback(OAuth2AuthenticationToken token, HttpSession session) {
		log.info("카카오 컨트롤러 입구");
		try {
			Map<String, Object> attributes = token.getPrincipal().getAttributes();
			log.info("attributes 안의 값은? ===> {}", attributes);
			
			String email = (String)((Map<String, Object>) attributes.get("kakao_account")).get("email");
			String userName = (String)((Map<String, Object>)((Map<String, Object>) attributes.get("kakao_account")).get("profile")).get("nickname");
			
			Long socialIdLong = (Long) attributes.get("id");
			String socialId = String.valueOf(socialIdLong);
			
			return "redirect:/main";
		} catch (Exception e) {
			log.error("카카오 로그인 처리 실패 : {}", e.getMessage());
			return "redirect:/member/login?error";
		}
	}
	
	
	
}
