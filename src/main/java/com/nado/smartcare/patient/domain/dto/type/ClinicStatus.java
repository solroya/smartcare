package com.nado.smartcare.patient.domain.dto.type;

import lombok.Getter;

@Getter
public enum ClinicStatus {
    SCHEDULED("예약됨"),
    COMPLETED("완료"),
    CANCELLED("취소");

    private final String displayName;

    ClinicStatus(String displayName) {
        this.displayName = displayName;
    }

    public static ClinicStatus fromString(String text) {
        for (ClinicStatus status : ClinicStatus.values()) {
            if (status.name().equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown ClinicStatus: " + text);
    }
}
