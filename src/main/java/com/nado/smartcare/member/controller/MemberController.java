package com.nado.smartcare.member.controller;

import com.nado.smartcare.member.domain.Member;
import com.nado.smartcare.member.domain.dto.MemberDto;
import com.nado.smartcare.member.service.MemberService;
import com.nado.smartcare.member.verifysms.domain.SmsDto;
import com.nado.smartcare.member.verifysms.service.SmsService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final SmsService smsService;
    
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
    
    @GetMapping("/division")
    public String divisionForm() {
    	return "member/division";
    }
    
    // 아이디 찾기
    @GetMapping("/find-id")
    public String findIdForm() {
    	return "member/findId";
    }
    
    @ResponseBody
    @PostMapping("/find-id")
    public ResponseEntity<?> findMemberId(@RequestBody Map<String, String> requestData) {
        log.info("요청 데이터: {}", requestData);

        String rawPhoneNumber = requestData.get("phone");
        String phoneNumberForRedis = rawPhoneNumber.replaceAll("-", "");
        String code = requestData.get("code");

        log.info("정규화된 전화번호 (redis용) : {}", phoneNumberForRedis);

        boolean isCodeValid = smsService.verifySms(phoneNumberForRedis, code);
        log.info("인증 코드 유효 여부 : {}", isCodeValid);
        if (!isCodeValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "인증 코드가 유효하지 않습니다."));
        }

        List<MemberDto> members = memberService.findByPhoneNumber(rawPhoneNumber);
        log.info("찾은 회원 수: {}", members.size());
        if (members.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "해당 번호로 등록된 계정을 찾을 수 없습니다."));
        }

        return ResponseEntity.ok(Map.of("memberIds", members.stream()
                .map(MemberDto::memberId)
                .toList()));
    }
    
    // 비밀번호 찾기
    @GetMapping("/find-password")
    public String showFindPasswordForm() {
    	return "member/findPass";
    }
    
    @PostMapping("/find-password/send-code")
    @ResponseBody
    public ResponseEntity<?> sendPasswordResetCode(@RequestBody Map<String, String> requestData) {
    	log.info("sendPasswordResetCode에 들어왔나?");
    	String memberId = requestData.get("memberId");
    	String rawPhoneNumber  = requestData.get("memberPhoneNumber");
    	String redisPhoneNumber = rawPhoneNumber.replaceAll("-", "");
    	
    	log.info("비밀번호 찾기 요청 - 아이디 : {}, 전화번호 : {}", memberId, rawPhoneNumber);
    	
    	boolean isMemberValid = memberService.verifyMember(memberId, rawPhoneNumber);
    	if (!isMemberValid) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "아이디와 전화번호가 일치하지 않습니다."));
		}
    	
    	SmsDto smsDto = new SmsDto();
    	smsDto.setPhone(redisPhoneNumber);
    	smsService.sendSms(smsDto);
    	
    	return ResponseEntity.ok(Map.of("message", "인증 코드가 전송되었습니다."));
    }
    
    @PostMapping("/find-password/verify-code")
    @ResponseBody
    public ResponseEntity<?> verifyPasswordResetCode(@RequestBody Map<String, String> requestData) {
        log.info("verifyPasswordResetCode에 들어왔나?");
        String rawPhoneNumber = requestData.get("memberPhoneNumber");
        String redisPhoneNumber = rawPhoneNumber.replaceAll("-", "");
        String code = requestData.get("code");
        
        log.info("비밀번호 찾기 - 인증 코드 검증 - 전화번호 : {}, 코드 : {}", rawPhoneNumber, code);
        
        boolean isCodeValid = smsService.verifySms(redisPhoneNumber, code);
        if (!isCodeValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "인증 코드가 유효하지 않습니다."));
        }
        
        return ResponseEntity.ok(Map.of("message", "인증 코드가 유효합니다."));
    }
    
    @PostMapping("/find-password/reset")
    @ResponseBody
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> requestData) {
    	log.info("resetPassword에 들어왔나?");
    	String memberId = requestData.get("memberId");
    	String newPassword = requestData.get("newPassword");
    	String confirmNewPassword = requestData.get("confirmNewPassword");
    	
    	log.info("비밀번호 재설정 요청 - 아이디 : {}", memberId);
    	
    	if (!newPassword.equals(confirmNewPassword)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "비밀번호가 일치하지 않습니다."));
		}
    	
    	try {
			memberService.updatePassword(memberId, newPassword);
		} catch (UsernameNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "사용자를 찾을 수 없습니다."));
		}
    	
    	return ResponseEntity.ok(Map.of("message", "비밀번호가 성공적으로 변경되었습니다."));
    }
}
