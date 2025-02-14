package com.nado.smartcare.employee.service;

import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.employee.domain.dto.EmployeeDto;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {

    Optional<EmployeeDto> findById(Long employeeNo);

    Optional<EmployeeDto> searchEmployee(String employeeId);

    Optional<EmployeeDto> searchEmployeeEmail(String employeeEmail);

    EmployeeDto saveEmployee(EmployeeDto employeeDto);

    Employee login(String employeeId, String employePass);

    List<EmployeeDto> findDoctorsByDepartment(String departmentType);

}
