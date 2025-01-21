package com.nado.smartcare.employee.service.impl;

import com.nado.smartcare.employee.domain.Department;
import com.nado.smartcare.employee.domain.dto.DepartmentDto;
import com.nado.smartcare.employee.repository.DepartmentRepository;
import com.nado.smartcare.employee.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    @Override
    public List<DepartmentDto> getAllDepartmentsWithDoctors() {
        List<Department> departments = departmentRepository.findAllWithEmployees();
        log.debug("Found {} departments", departments.size());

        List<DepartmentDto> departmentDtos = departments.stream()
                .map(DepartmentDto::from)
                .collect(Collectors.toList());

        log.debug("Converted to DTOs: {}", departmentDtos);
        return departmentDtos;
    }
}
