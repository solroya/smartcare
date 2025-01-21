package com.nado.smartcare.patient.domain;

import com.nado.smartcare.config.BaseEntity;
import com.nado.smartcare.disease.domain.DiseaseCategory;
import com.nado.smartcare.disease.domain.DiseaseList;
import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.member.domain.Member;
import com.nado.smartcare.patient.domain.dto.type.ClinicStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
public class PatientRecordCard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "patient_record_seq")
    private Long patientRecordNo;

    private String clinicName; // 진료명

    private LocalDate clinicDate; // 진료일자

    private LocalDateTime clinicReservationDate; // 진료 예약일

    private String clinicManifestation; // 진료 소견

    @Enumerated(EnumType.STRING)
    private ClinicStatus clinicStatus; // 진료 상태 (예: SCHEDULED, COMPLETED, CANCELLED)

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; //

    @ManyToOne
    @JoinColumn(name = "employee_no", nullable = false)
    private Employee employee; // 의사 정보

    @ManyToOne
    @JoinColumn(name = "disease_category_id", nullable = false)
    private DiseaseCategory diseaseCategory; // 진료 카테고리

    @ManyToOne
    @JoinColumn(name = "disease_list_id", nullable = false)
    private DiseaseList diseaseList; // 질병 코드 및 이름

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "reception_no", nullable = false)
    private Reception reception;

    // 기본 생성자에서 clinicStatus 초기화
    public PatientRecordCard() {
        this.clinicStatus = ClinicStatus.SCHEDULED;
    }

    // 필드 초기화 생성자
    public PatientRecordCard(String clinicName, LocalDate clinicDate, LocalDateTime clinicReservationDate,
                             String clinicManifestation, ClinicStatus clinicStatus, Reception reception, Member member,
                             Employee employee, DiseaseCategory diseaseCategory, DiseaseList diseaseList) {
        this.clinicName = clinicName;
        this.clinicDate = clinicDate;
        this.clinicReservationDate = clinicReservationDate;
        this.clinicManifestation = clinicManifestation;
        this.clinicStatus = clinicStatus;
        this.reception = reception;
        this.member = member;
        this.employee = employee;
        this.diseaseCategory = diseaseCategory;
        this.diseaseList = diseaseList;
    }


    // 정적 팩토리 메서드
    public static PatientRecordCard of(String clinicName, LocalDate clinicDate, LocalDateTime reservationDate,
                                       String clinicManifestation, ClinicStatus clinicStatus, Reception reception, Member member,
                                       Employee employee, DiseaseCategory category, DiseaseList disease) {
        return new PatientRecordCard(
                clinicName, clinicDate, reservationDate, clinicManifestation, clinicStatus, reception, member, employee, category, disease
        );
    }
}


