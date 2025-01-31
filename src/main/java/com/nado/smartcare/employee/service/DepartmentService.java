package com.nado.smartcare.employee.service;

import com.nado.smartcare.employee.domain.dto.DepartmentDto;

import java.util.List;

public interface DepartmentService {

    // 부서의 의사 호출
    List<DepartmentDto> getAllDepartmentsWithDoctors();


}
