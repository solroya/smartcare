package com.nado.smartcare.employee.domain;

import com.nado.smartcare.config.BaseEntity;
import com.nado.smartcare.employee.domain.type.EmployeeStatus;
import com.nado.smartcare.employee.domain.type.TypeOfEmployee;
import com.nado.smartcare.employee.domain.type.WorkingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@ToString
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

    private LocalDate employeeBirthday;

    private String employeePhoneNumber;

    private boolean isSocial;

    @Enumerated(EnumType.STRING)
    private EmployeeStatus employeeStatus;

    @Enumerated(EnumType.STRING)
    private TypeOfEmployee typeOfEmployee;

    @Enumerated(EnumType.STRING)
    private WorkingStatus workingStatus;
}
