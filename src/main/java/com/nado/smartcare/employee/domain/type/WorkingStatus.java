package com.nado.smartcare.employee.domain.type;

public enum WorkingStatus {
    WORKING("진료"), // 진료
    VACATION("휴가"), // 휴가
    SURGERY("수술"); // 수술

    private final String displayName;

    WorkingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
