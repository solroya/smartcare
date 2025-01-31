package com.nado.smartcare.patient.domain.dto;

import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.member.domain.Member;
import com.nado.smartcare.patient.domain.PatientRecordCard;
import com.nado.smartcare.patient.domain.Reception;
import com.nado.smartcare.patient.domain.type.ReceptionStatus;

import java.time.LocalDateTime;

public record ReceptionDto (
        Long receptionNo,
        Member member,
        Employee employee,
        PatientRecordCard patientRecordCard,
        LocalDateTime receptionTime,
        ReceptionStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReceptionDto from(Reception reception) {
        return new ReceptionDto(
                reception.getReceptionNo(),
                reception.getMember(),
                reception.getEmployee(),
                reception.getPatientRecordCard(),
                reception.getReceptionTime(),
                reception.getStatus(),
                reception.getCreatedAt(),
                reception.getUpdatedAt()
        );
    }


}
