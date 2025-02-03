package com.nado.smartcare.employee.domain.dto;

import com.nado.smartcare.employee.domain.Department;
import com.nado.smartcare.employee.domain.type.TypeOfEmployee;
import com.nado.smartcare.employee.domain.type.WorkingStatus;

import java.util.List;
import java.util.stream.Collectors;

public record DepartmentDto(
        Long departmentId,
        String departmentName,
        List<DoctorDto> doctors
) {
    public static DepartmentDto from(Department department) {
        List<DoctorDto> doctors = department.getEmployees().stream()
                .filter(emp -> emp.getTypeOfEmployee() == TypeOfEmployee.DOCTOR
                        && emp.getWorkingStatus() == WorkingStatus.WORKING)
                .map(DoctorDto::from)
                .collect(Collectors.toList());

        return new DepartmentDto(
                department.getDepartmentId(),
                department.getDepartmentName(),
                doctors
        );
    }
}

