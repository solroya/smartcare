package com.nado.smartcare.employee.service;

import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.employee.domain.dto.DepartmentDto;
import com.nado.smartcare.employee.domain.dto.EmployeeDto;
import com.nado.smartcare.employee.domain.type.WorkingStatus;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {

    Optional<EmployeeDto> findById(Long employeeNo);

    Optional<EmployeeDto> searchEmployee(String employeeId);

    Optional<EmployeeDto> searchEmployeeEmail(String employeeEmail);

    EmployeeDto saveEmployee(EmployeeDto employeeDto);

    EmployeeDto updateEmployee(Long EmployeeNo, String newEmployeePass, String newEmployeePhoneNumber);

    void deleteEmployee(Long employeeNo);
    
    Employee login(String employeeId, String employePass);

    List<EmployeeDto> findDoctorsByDepartment(String departmentType);

    List<DepartmentDto> getAllDepartmentsWithDoctors();

}
