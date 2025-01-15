package com.nado.smartcare.employee.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nado.smartcare.config.BaseEntity;
import com.nado.smartcare.employee.domain.type.EmployeeStatus;
import com.nado.smartcare.employee.domain.type.TypeOfEmployee;
import com.nado.smartcare.employee.domain.type.WorkingStatus;
import com.nado.smartcare.patient.domain.PatientRecordCard;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Entity
public class Employee extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "employee_seq")
    private Long employeeNo;

    @Column(nullable = false, unique = true)
    private String employeeId;

    private String employeeName;

    private String employeePass;

    private String employeeEmail;
    
    private boolean employeeGender;

    private LocalDate employeeBirthday;

    private String employeePhoneNumber;

    private boolean isSocial;

    @Enumerated(EnumType.STRING)
    private EmployeeStatus employeeStatus;

    @Enumerated(EnumType.STRING)
    private TypeOfEmployee typeOfEmployee;

    @Enumerated(EnumType.STRING)
    private WorkingStatus workingStatus;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    @JsonIgnore
    private Department department;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<PatientRecordCard> patientRecordCards; // 이 의사가 담당한 진료 기록

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
        TypeOfEmployee typeOfEmployee = TypeOfEmployee.STAFF;
        WorkingStatus workingStatus = WorkingStatus.WORKING;
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
}
