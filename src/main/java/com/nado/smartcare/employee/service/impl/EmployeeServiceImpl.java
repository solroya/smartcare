package com.nado.smartcare.employee.service.impl;

import com.nado.smartcare.employee.dto.EmployeeDto;
import com.nado.smartcare.employee.service.EmployeeService;

import java.util.Optional;

public class EmployeeServiceImpl implements EmployeeService {
    
    @Override
    public Optional<EmployeeDto> searchEmployee(String employeeId) {
        return Optional.empty();
    }

    @Override
    public Optional<EmployeeDto> searchEmployeeEmail(String employeeEmail) {
        return Optional.empty();
    }

    @Override
    public EmployeeDto saveEmployee(EmployeeDto employeeDto) {
        return null;
    }

    @Override
    public EmployeeDto updateEmployee(Long no, String newEmployeePass, String newEmployeePhoneNumber) {
        return null;
    }

    @Override
    public void deleteEmployee(Long employeeNo) {

    }
}
