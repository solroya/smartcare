package com.nado.smartcare.patient.domain;

import com.nado.smartcare.config.BaseEntity;
import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.member.domain.Member;
import com.nado.smartcare.patient.domain.type.ReceptionStatus;
import com.nado.smartcare.patient.domain.dto.ReceptionDto;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Reception extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reception_seq")
    private Long receptionNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no", nullable = false)
    private Member member; // 환자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_no", nullable = false)
    private Employee employee; // 의사

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "patient_no")
    private PatientRecordCard patientRecordCard;

    @Column(nullable = false)
    private LocalDateTime receptionTime; // 접수 시간

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReceptionStatus status; // 예약 상태? 혹은 접수 상태? 뭘로 할까

    public static Reception of(ReceptionDto dto, Member member, Employee employee) {
        Reception reception = new Reception();
        reception.receptionNo = dto.receptionNo();
        reception.receptionTime = dto.receptionTime();
        reception.status = dto.status();
        reception.member = member;
        reception.employee = employee;
        return reception;
    }

    public void setStatus(ReceptionStatus receptionStatus) {
        this.status = receptionStatus;
    }
}
