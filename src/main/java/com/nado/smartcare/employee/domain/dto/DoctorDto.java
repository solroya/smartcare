package com.nado.smartcare.employee.domain.dto;

import com.nado.smartcare.employee.domain.Employee;

// 의사 정보만을 위한 간단한 DTO
public record DoctorDto(
        Long employeeNo,
        String employeeName
) {
    public static DoctorDto from(Employee employee) {
        return new DoctorDto(
                employee.getEmployeeNo(),
                employee.getEmployeeName()
        );
    }
}
