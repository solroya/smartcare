package com.nado.smartcare.employee.domain;

import com.nado.smartcare.config.BaseEntity;
import com.nado.smartcare.employee.domain.type.EmployeeStatus;
import com.nado.smartcare.employee.domain.type.TypeOfEmployee;
import com.nado.smartcare.employee.domain.type.WorkingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

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

    public Employee(String employeeId, String employeeName, String employeePass, String employeeEmail, LocalDate employeeBirthday, String employeePhoneNumber, boolean isSocial, EmployeeStatus employeeStatus, TypeOfEmployee typeOfEmployee, WorkingStatus workingStatus) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.employeePass = employeePass;
        this.employeeEmail = employeeEmail;
        this.employeeBirthday = employeeBirthday;
        this.employeePhoneNumber = employeePhoneNumber;
        this.isSocial = isSocial;
        this.employeeStatus = employeeStatus;
        this.typeOfEmployee = typeOfEmployee;
        this.workingStatus = workingStatus;
    }

    public Employee() {
    }

    public static Employee of(String employeeId, String employeeName, String employeePass, String employeeEmail, LocalDate employeeBirthday,
                              String employeePhoneNumber, boolean isSocial) {
        EmployeeStatus employeeStatus = EmployeeStatus.PENDING;
        TypeOfEmployee typeOfEmployee = TypeOfEmployee.STAFF;
        WorkingStatus workingStatus = WorkingStatus.WORKING;
        return new Employee(employeeId, employeeName, employeePass, employeeEmail, employeeBirthday, employeePhoneNumber,
                isSocial, employeeStatus, typeOfEmployee, workingStatus);
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
