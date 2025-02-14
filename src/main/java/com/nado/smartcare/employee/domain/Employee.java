package com.nado.smartcare.employee.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nado.smartcare.config.BaseEntity;
import com.nado.smartcare.employee.domain.dto.EmployeeDto;
import com.nado.smartcare.employee.domain.type.EmployeeStatus;
import com.nado.smartcare.employee.domain.type.TypeOfEmployee;
import com.nado.smartcare.employee.domain.type.WorkingStatus;
import com.nado.smartcare.patient.domain.PatientRecordCard;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Getter
@Entity
@ToString
public class Employee extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "employee_seq")
    private Long employeeNo; // 직원 사번(시퀀스)

    @Column(nullable = false, unique = true)
    private String employeeId; // 직원 아이디

    private String employeeName; // 직원 이름

    private String employeePass; // 비밀번호

    private String employeeEmail; // 이메일
    
    private boolean employeeGender; // 성별(남, 여)

    private LocalDate employeeBirthday; // 생년월일

    private String employeePhoneNumber; // 전화번호

    private boolean isSocial; // 소셜 여부

    @Enumerated(EnumType.STRING)
    private EmployeeStatus employeeStatus;  // 직원 상태: PENDING 승인 대기중 / APPROVED 승인 / DENIED 승인 거절

    @Enumerated(EnumType.STRING)
    private TypeOfEmployee typeOfEmployee;  // STAFF 사원 / HUMAN_RESOURCE 인사팀 / NURSE 간호사 / DOCTOR 의사

    @Enumerated(EnumType.STRING)
    private WorkingStatus workingStatus; // WORKING("진료") VACATION("휴가") SURGERY("수술")

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    @JsonIgnore
    private Department department; // 부서명

    @OneToMany(mappedBy = "employee")
    @ToString.Exclude
    private List<PatientRecordCard> patientRecordCards; // 이 의사가 담당한 진료 기록

    public void setPatientRecordCards(List<PatientRecordCard> patientRecordCards) {
        this.patientRecordCards = patientRecordCards;
    }

    public Employee(String employeeId, String employeeName, String employeePass, String employeeEmail, boolean employeeGender, LocalDate employeeBirthday, String employeePhoneNumber, boolean isSocial, EmployeeStatus employeeStatus, TypeOfEmployee typeOfEmployee, WorkingStatus workingStatus, Department department) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.employeePass = employeePass;
        this.employeeEmail = employeeEmail;
        this.employeeGender = employeeGender;
        this.employeeBirthday = employeeBirthday;
        this.employeePhoneNumber = employeePhoneNumber;
        this.isSocial = isSocial;
        this.employeeStatus = employeeStatus;
        this.typeOfEmployee = typeOfEmployee;
        this.workingStatus = workingStatus;
        this.department = department;
    }

    public Employee() {
    }

    public static Employee of(String employeeId, String employeeName, String employeePass, String employeeEmail, boolean employeeGender, LocalDate employeeBirthday,
                              String employeePhoneNumber, boolean isSocial, Department department) {
        EmployeeStatus employeeStatus = EmployeeStatus.PENDING;
        WorkingStatus workingStatus = WorkingStatus.WORKING;

        // Department 이름을 기반으로 직원 타입 결정
        TypeOfEmployee typeOfEmployee = TypeOfEmployee.STAFF;

        // Doctor 여부 판단하는 로직(1~3번이 의사)
        long deptId = department.getDepartmentId();
        log.info("deptId: {}", deptId);
        if (deptId < 4) {
            typeOfEmployee = TypeOfEmployee.DOCTOR;
        }

        return new Employee(employeeId, employeeName, employeePass, employeeEmail, employeeGender, employeeBirthday, employeePhoneNumber,
                isSocial, employeeStatus, typeOfEmployee, workingStatus, department);
    }

    public Employee updateEmployee(String newEmployeePass, String newEmployeePhoneNumber) {
        if (newEmployeePass != null && !newEmployeePass.isBlank()) {
            this.employeePass = newEmployeePass;
        }
        if (newEmployeePhoneNumber != null && !newEmployeePhoneNumber.isBlank()) {
            this.employeePhoneNumber = newEmployeePhoneNumber;
        }
        return this;
    }

    public static Employee toEntity(EmployeeDto employeeDto) {
        return new Employee(
                employeeDto.employeeId(),
                employeeDto.employeeName(),
                employeeDto.employeePass(),
                employeeDto.employeeEmail(),
                employeeDto.employeeGender(),
                employeeDto.employeeBirthday(),
                employeeDto.employeePhoneNumber(),
                employeeDto.isSocial(),
                employeeDto.employeeStatus(),
                employeeDto.typeOfEmployee(),
                employeeDto.workingStatus(),
                employeeDto.departmentId()
        );
    }
}
