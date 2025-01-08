package com.nado.smartcare.employee.service;

import com.nado.smartcare.employee.dto.EmployeeDto;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface EmployeeService {

    Optional<EmployeeDto> findById(Long employeeNo);

    Optional<EmployeeDto> searchEmployee(String employeeId);

    Optional<EmployeeDto> searchEmployeeEmail(String employeeEmail);

    EmployeeDto saveEmployee(EmployeeDto employeeDto);

    EmployeeDto updateEmployee(Long EmployeeNo, String newEmployeePass, String newEmployeePhoneNumber);

    void deleteEmployee(Long employeeNo);

    List<EmployeeDto> findDoctorsByDepartment(String departmentType);
}
