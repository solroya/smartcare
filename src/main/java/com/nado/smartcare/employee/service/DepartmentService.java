package com.nado.smartcare.employee.service;

import com.nado.smartcare.employee.domain.dto.DepartmentDto;

import java.util.List;

public interface DepartmentService {

    List<DepartmentDto> getAllDepartmentsWithDoctors();
}
