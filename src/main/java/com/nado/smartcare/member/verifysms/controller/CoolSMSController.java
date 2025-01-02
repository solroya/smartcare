package com.nado.smartcare.member.verifysms.controller;

import com.nado.smartcare.member.verifysms.domain.SmsDto;
import com.nado.smartcare.member.verifysms.service.SmsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sms")
public class CoolSMSController {

    private final SmsService smsService;
    private final RedisTemplate<String, String> redisTemplate;

    @PostMapping("/send")
    public ResponseEntity<String> sendSms(@RequestBody @Valid SmsDto smsDto) {

         smsService.sendSms(smsDto);
        return ResponseEntity.ok("인증 코드가 전송 되었습니다.");
    }

    @GetMapping("/balance")
    public void getBalance() {
        smsService.getBalance();
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifySms(@RequestBody @Valid SmsDto smsDto) {
        String phone = smsDto.getPhone();
        String code = smsDto.getCode();
        String storedCode = redisTemplate.opsForValue().get(phone);

        if (storedCode == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증 코드가 존재하지 않습니다.");
        }
        if (storedCode.equals(code)) {
            redisTemplate.delete(phone);
            return ResponseEntity.ok("인증 성공");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증 코드가 일치하지 않습니다.");
        }
    }
}
