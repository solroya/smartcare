package com.nado.smartcare.employee.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class Department {

    /*INTERNAL_MEDICINE, // 내과
    SURGERY,           // 외과
    PSYCHIATRY,        // 정신과
    HUMAN_RESOURCE     // 인사과*/

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "department_seq")
    private Long departmentId;

    @Column(nullable = false, unique = true)
    private String departmentName;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private List<Employee> employees = new ArrayList<>();

    public Department(String departmentName) {
        this.departmentName = departmentName;
    }

    public Department() {
    }
}
