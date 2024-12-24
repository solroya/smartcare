package com.nado.smartcare.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/member")
public class MemberController {

    @GetMapping
    public String member(ModelMap map) {
        map.addAttribute("members", List.of());
        return "member/index";
    }
}
