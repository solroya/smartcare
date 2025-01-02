package com.nado.smartcare.member.verifysms.service;

import com.nado.smartcare.member.verifysms.domain.SmsDto;
import com.nado.smartcare.member.verifysms.service.impl.SmsServiceImpl;
import com.nado.smartcare.member.verifysms.util.CoolSMSConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@DisplayName("비지니스 로직 - 외부 사용자 SMS 인증절차")
@ExtendWith(MockitoExtension.class)
class SmsServiceTest {

    @Mock
    private CoolSMSConfig coolSMSConfig;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private SmsServiceImpl smsService;

    @BeforeEach
    void setUp() {
        // RedisTemplate의 ValueOperations 모의 설정
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("SMS 전송 테스트")
    void sendSmsTest() {
        // Given
        SmsDto smsDto = new SmsDto();
        smsDto.setPhone("01012345678");

        // When
        smsService.sendSms(smsDto);

        // Then
        verify(coolSMSConfig, times(1)).sendOne(eq("01012345678"), anyString());
        verify(valueOperations, times(1)).set(eq("01012345678"), anyString(), eq(5L), eq(TimeUnit.MINUTES));
    }

    @Test
    @DisplayName("인증 코드 검증 테스트 - 성공")
    void verifySmsSuccessTest() {
        // Given
        String phone = "01012345678";
        String code = "123456";

        when(valueOperations.get(phone)).thenReturn(code);

        // When
        boolean result = smsService.verifySms(phone, code);

        // Then
        assertTrue(result);
        verify(redisTemplate, times(1)).delete(phone);
    }

    @Test
    @DisplayName("인증 코드 검증 테스트 - 실패")
    void verifySmsFailTest() {
        // Given
        String phone = "01012345678";
        String code = "123456";

        when(valueOperations.get(phone)).thenReturn("654321");

        // When
        boolean result = smsService.verifySms(phone, code);

        // Then
        assertFalse(result);
        verify(redisTemplate, never()).delete(phone);
    }
}