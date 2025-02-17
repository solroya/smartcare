package com.nado.smartcare.openai.entity;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MedicalDataChangeEvent {
    private final String type;          // 이벤트 유형
    private final Long recordId;        // 변경된 레코드의 ID
    private final LocalDateTime timestamp;  // 이벤트 발생 시간
    private final String changeDescription; // 변경 내용 설명

    public MedicalDataChangeEvent(String type, Long recordId, String changeDescription) {
        this.type = type;
        this.recordId = recordId;
        this.timestamp = LocalDateTime.now();
        this.changeDescription = changeDescription;
    }

    // 편의를 위한 정적 팩토리 메서드들
    public static MedicalDataChangeEvent createMedicalRecordEvent(Long recordId) {
        return new MedicalDataChangeEvent("MEDICAL_RECORD", recordId, "새로운 진료 기록 생성");
    }

    public static MedicalDataChangeEvent updateMedicalRecordEvent(Long recordId) {
        return new MedicalDataChangeEvent("MEDICAL_RECORD", recordId, "진료 기록 업데이트");
    }

    public static MedicalDataChangeEvent createReservationEvent(Long recordId) {
        return new MedicalDataChangeEvent("RESERVATION", recordId, "새로운 예약 생성");
    }

    public static MedicalDataChangeEvent updateReservationEvent(Long recordId) {
        return new MedicalDataChangeEvent("RESERVATION", recordId, "예약 정보 업데이트");
    }
}
