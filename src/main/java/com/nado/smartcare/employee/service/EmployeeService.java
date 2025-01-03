package com.nado.smartcare.employee.service;

import com.nado.smartcare.employee.dto.EmployeeDto;

import java.util.Optional;

public interface EmployeeService {

    Optional<EmployeeDto> searchEmployee(String employeeId);

    Optional<EmployeeDto> searchEmployeeEmail(String employeeEmail);

    EmployeeDto saveEmployee(EmployeeDto employeeDto);

    EmployeeDto updateEmployee(Long no, String newEmployeePass, String newEmployeePhoneNumber);

    void deleteEmployee(Long employeeNo);
}
