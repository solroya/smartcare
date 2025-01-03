package com.nado.smartcare.member.controller;

import com.nado.smartcare.member.domain.Member;
import com.nado.smartcare.member.dto.MemberDto;
import com.nado.smartcare.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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

    @GetMapping("join")
    public String join(ModelMap map) {
        return "member/join";
    }

    @PostMapping("join")
    public String join(ModelMap map,@Valid MemberDto memberDto) {
        log.info(memberDto);

        memberService.saveMember(memberDto);
        return "member/join";
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
}
