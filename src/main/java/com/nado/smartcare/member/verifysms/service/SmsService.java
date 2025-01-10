package com.nado.smartcare.member.verifysms.service;

import com.nado.smartcare.member.verifysms.domain.SmsDto;

public interface SmsService {

    void sendSms(SmsDto smsDto);

    void getBalance();

    boolean verifySms(String phone, String code);
    
    Long getTime(String phone); // redis에서 남은 TTL 조회
}
