package com.nado.smartcare.patient.entity;

import com.nado.smartcare.config.BaseEntity;
import com.nado.smartcare.member.domain.Member;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class PatientRecordCard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "patient_seq")
    private Long patientNo;

    private String DiseaseCode; // 질병코드

    private String clinicName; // 진료명

    private LocalDate clinicDate; // 진료일자

    private LocalDateTime clinicReservationDate; // 진료 예약일

    private String clinicManifestation; // 진료 소견

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
