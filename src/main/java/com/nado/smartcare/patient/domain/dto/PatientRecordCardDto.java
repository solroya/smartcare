package com.nado.smartcare.patient.domain.dto;

import com.nado.smartcare.disease.domain.DiseaseCategory;
import com.nado.smartcare.disease.domain.DiseaseList;
import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.member.domain.Member;
import com.nado.smartcare.patient.domain.Reception;
import com.nado.smartcare.patient.domain.dto.type.ClinicStatus;
import com.nado.smartcare.patient.domain.PatientRecordCard;
import com.nado.smartcare.reservation.domain.Reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PatientRecordCardDto(
        Long patientRecordNo,               // 진료 기록 번호
        String clinicName,            // 진료명
        LocalDate clinicDate,         // 진료 일자
        LocalDateTime clinicReservationDate, // 진료 예약 일자
        String clinicManifestation,   // 진료 소견
        ClinicStatus clinicStatus,    // 진료 상태
        Member member,                // 환자 번호
        Employee employee,                // 의사
        DiseaseCategory diseaseCategory,       // 진료 카테고리 번호
        DiseaseList diseaseList,            // 질병 번호
        Reception receptionNo,
        Reservation reservation
) {
    public static PatientRecordCardDto from(PatientRecordCard entity) {
        return new PatientRecordCardDto(
                entity.getPatientRecordNo(),
                entity.getClinicName(),
                entity.getClinicDate(),
                entity.getClinicReservationDate(),
                entity.getClinicManifestation(),
                entity.getClinicStatus(),
                entity.getMember(),
                entity.getEmployee(),
                entity.getDiseaseCategory(),
                entity.getDiseaseList(),
                entity.getReception(),
                entity.getReservation()
        );
    }
}
