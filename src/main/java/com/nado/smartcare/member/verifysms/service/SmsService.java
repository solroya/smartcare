package com.nado.smartcare.member.verifysms.service;

import com.nado.smartcare.member.verifysms.domain.SmsDto;

public interface SmsService {

    void sendSms(SmsDto smsDto);

    void getBalance();

    boolean verifySms(String phone, String code);
}
