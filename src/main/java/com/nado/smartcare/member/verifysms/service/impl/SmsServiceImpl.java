package com.nado.smartcare.member.verifysms.service.impl;

import com.nado.smartcare.member.verifysms.domain.SmsDto;
import com.nado.smartcare.member.verifysms.service.SmsService;
import com.nado.smartcare.member.verifysms.util.CoolSMSConfig;
import lombok.extern.log4j.Log4j2;
import net.nurigo.sdk.message.model.Balance;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class SmsServiceImpl implements SmsService {

    private final CoolSMSConfig coolSMSConfig;

    private final RedisTemplate<String, String> redisTemplate;
    
    public SmsServiceImpl(CoolSMSConfig coolSMSConfig, RedisTemplate<String, String> redisTemplate) {
        this.coolSMSConfig = coolSMSConfig;
        this.redisTemplate = redisTemplate;
    }


    @Override
    public void sendSms(SmsDto smsDto) {
        String phone = smsDto.getPhone();
        String certificationCode = Integer.toString((int)(Math.random() * (999999 - 100000 + 1)) + 100000); // 6자리 인증 코드를 랜덤으로 생성
        // TODO : 요금 절약을 위해 닫아 놓음
        coolSMSConfig.sendOne(phone, certificationCode);

        // Reids 인증 코드 저장
        redisTemplate.opsForValue().set(phone, certificationCode, 5, TimeUnit.MINUTES);
        
        String storedCode = redisTemplate.opsForValue().get(phone);
        log.info("Redis에 저장된 인증 코드 : {}", storedCode);
        
        Set<String> keys = redisTemplate.keys("*");
        log.info("Redis에서 현재 저장된 키 : {}", keys);
        
        log.info("전화번호 : {}, 인증 코드 : {}", phone, certificationCode);
        
    }

    @Override
    public void getBalance() {
        Balance balance = coolSMSConfig.getBalance();
        log.info("현재 남은 잔액: {}", balance);
    }

    @Override
    public boolean verifySms(String phone, String code) {
        String storedCode = redisTemplate.opsForValue().get(phone);

        if (storedCode == null) {
            log.warn("인증 코드가 존재하지 않음. 전화번호: {}", phone);
            return false; // 인증 코드 없음
        }
        if (storedCode.equals(code)) {
            redisTemplate.delete(phone);
            return true; // 인증 성공
        } else {
            log.warn("인증 코드 불일치. 전화번호: {}, 입력된 코드: {}", phone, code);
            return false; // 인증 코드 불일치
        }
    }


	@Override
	public Long getTime(String phone) {
		Long ttl = redisTemplate.getExpire(phone, TimeUnit.SECONDS);
		
		if (ttl == null) {
	        log.error("Redis 키가 존재하지 않음. 전화번호 : {}", phone);
	        throw new IllegalStateException("해당 전화번호의 인증 시간이 만료되었거나 존재하지 않습니다.");
	    }

	    if (ttl <= 0) {
	        log.error("TTL 값이 만료됨. 전화번호 : {}", phone);
	        throw new IllegalStateException("해당 전화번호의 인증 시간이 만료되었거나 존재하지 않습니다.");
	    }

	    log.info("TTL 조회 성공. 전화번호 : {}, 남은 TTL : {}초", phone, ttl);
	    return ttl;
	}
	
}
