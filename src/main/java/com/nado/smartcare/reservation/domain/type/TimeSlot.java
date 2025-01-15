package com.nado.smartcare.reservation.domain.type;

public enum TimeSlot {
    MORNING("오전"),
    AFTERNOON("오후");

    private String displayName;

    TimeSlot(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
