package com.nado.smartcare.member.controller;

import com.nado.smartcare.member.domain.Member;
import com.nado.smartcare.member.domain.dto.MemberDto;
import com.nado.smartcare.member.service.MemberService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    
    @GetMapping("memberIndex")
    public String member(ModelMap map) {
        map.addAttribute("members", List.of());
        return "member/memberIndex";
    }

    @GetMapping("register")
    public String join() {
        return "member/register";
    }

    @PostMapping("/register")
    public String join(@Valid MemberDto memberDto, BindingResult bindingResult) {
    	 if (bindingResult.hasErrors()) {
    	        log.error("Validation Errors: {}", bindingResult.getAllErrors());
    	        return "redirect:/member/register";
    	    }
    	
        log.info("memberDto register : {} ", memberDto);

        memberService.saveMember(memberDto);
        return "redirect:/main";
    }

    @ResponseBody
    @GetMapping("search-memberId")
    public Map<String, Boolean> checkId(@RequestParam("memberId") String memberId) {
        boolean result = memberService.searchMember(memberId).isPresent();
        return Map.of("result", result);
    }

    @ResponseBody
    @GetMapping("search-memberEmail")
    public Map<String, Boolean> searchMemberEmail(@RequestParam("memberEmail") String memberEmail) {
        boolean result = memberService.searchMemberEmail(memberEmail).isPresent();
        return Map.of("result", result);
    }
    
    @GetMapping("/login")
	public String loginForm() {
		return "member/login";
	}
	
    @PostMapping("/login")
    public String login(@RequestParam("memberId") String memberId,
                        @RequestParam("memberPass") String memberPass,
                        HttpSession session,
                        Model model) {
        try {
        	Member member = memberService.login(memberId, memberPass);
        	
			session.setAttribute("member", member);
			
			return "redirect:/main";
		} catch (IllegalArgumentException e) {
			model.addAttribute("error", "아이디 또는 비밀번호가 다릅니다.");
			return "member/login";
		}
    }
    
    @GetMapping("/division")
    public String divisionForm() {
    	return "member/division";
    }
}
