package com.nado.smartcare.member.verifysms.controller;

import com.nado.smartcare.member.verifysms.domain.SmsDto;
import com.nado.smartcare.member.verifysms.service.SmsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sms")
@Log4j2
public class CoolSMSController {

    private final SmsService smsService;
    private final RedisTemplate<String, String> redisTemplate;
    
    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendSms(@RequestBody @Valid SmsDto smsDto) {
        log.info("번호 값 상태 ? : {}" , smsDto.getPhone());
        smsService.sendSms(smsDto);

        Map<String, String> response = new HashMap<>();
        response.put("message", "인증 코드가 전송 되었습니다.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/balance")
    public void getBalance() {
        smsService.getBalance();
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifySms(@RequestBody @Valid SmsDto smsDto) {
        String phone = smsDto.getPhone();
        String code = smsDto.getCode();
        String storedCode = redisTemplate.opsForValue().get(phone);

        Map<String, String> response = new HashMap<>();

        if (storedCode == null) {
            response.put("message", "인증 코드가 존재하지 않습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (storedCode.equals(code)) {
            redisTemplate.delete(phone);
            response.put("message", "인증 성공");
            response.put("result", "true");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "인증 코드가 일치하지 않습니다.");
            response.put("result", "false");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
