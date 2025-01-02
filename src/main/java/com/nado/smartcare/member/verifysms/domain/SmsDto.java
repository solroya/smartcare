package com.nado.smartcare.member.verifysms.domain;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
public class SmsDto {
    @Setter
    @NotEmpty(message = "인증받으실 휴대폰 번호를 입력해주세요.")
    @Pattern(regexp = "^01[0-9]{8,9}$", message = "유효한 휴대폰 번호를 입력해주세요. (예: 01012345678)")
    private String phone;

    private String code;

}
