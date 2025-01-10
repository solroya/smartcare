package com.nado.smartcare.member.verifysms.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Balance;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class CoolSMSConfig {
/*
* CoolSMS API 개발 문서
* https://github.com/coolsms/coolsms-java-examples/blob/main/gradle-spring-demo/src/main/java/net/nurigo/gradlespringdemo/ExampleController.java
* */


    @Value("${coolsms.apikey}")
    private String apiKey;

    @Value("${coolsms.apisecret}")
    private String apiSecret;

    @Value("${coolsms.fromnumber}")
    private String fromNumber;

    DefaultMessageService messageService;

    @PostConstruct
    public void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    /**
     * 단일 메시지 발송 예제
     */
    public void sendOne(String to, String certificationCode) {
        try {
            Message message = new Message();
            // 발신번호 및 수신번호는 반드시 01012345678 형태로 입력되어야 합니다.
            message.setFrom(fromNumber);
            message.setTo(to);
            message.setText("SMARTCARE 본인확인 인증 번호는 [" + certificationCode + "]입니다.");

            SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
            log.info("CoolSMS 발신 결과 : {}", response);
        } catch (Exception e) {
            log.error("SMS 전송 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("SMS 전송에 실패했습니다.");
        }
    }

    /**
     * 잔액 조회 예제
     */
    public Balance getBalance() {
        Balance balance = this.messageService.getBalance();
        System.out.println(balance);

        return balance;
    }
}
