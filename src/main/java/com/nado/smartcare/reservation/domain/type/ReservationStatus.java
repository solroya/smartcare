package com.nado.smartcare.reservation.domain.type;

public enum ReservationStatus {
    CONFIRMED("예약 완료"),
    CANCELLED("예약 취소"),
    COMPLETED("진료 완료");

    private final String displayName;

    ReservationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
