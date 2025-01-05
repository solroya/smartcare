package com.nado.smartcare.employee.service.impl;

import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.employee.dto.EmployeeDto;
import com.nado.smartcare.employee.repository.EmployeeRepository;
import com.nado.smartcare.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    @Override
    public Optional<EmployeeDto> searchEmployee(String employeeId) {
        return employeeRepository.findByEmployeeId(employeeId)
                .map(EmployeeDto::from);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<EmployeeDto> searchEmployeeEmail(String employeeEmail) {
        return employeeRepository.findByEmployeeEmail(employeeEmail)
                .map(EmployeeDto::from);
    }

    @Override
    public EmployeeDto saveEmployee(EmployeeDto employeeDto) {
        Employee employee = Employee.of(
                employeeDto.employeeId(),
                employeeDto.employeePass(),
                employeeDto.employeeName(),
                employeeDto.employeeEmail(),
                employeeDto.employeeBirthday(),
                employeeDto.employeePhoneNumber(),
                employeeDto.isSocial()
        );
        return EmployeeDto.from(employeeRepository.save(employee));
    }

    @Transactional
    @Override
    public EmployeeDto updateEmployee(Long EmployeeNo, String newEmployeePass, String newEmployeePhoneNumber) {
        Employee employee = employeeRepository.findByEmployeeNo(EmployeeNo)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        employee.updateEmployee(newEmployeePass, newEmployeePhoneNumber);
        Employee updatedEmployee = employeeRepository.save(employee);
        return EmployeeDto.from(updatedEmployee);
    }

    @Transactional
    @Override
    public void deleteEmployee(Long employeeNo) {
        Employee employee = employeeRepository.findByEmployeeNo(employeeNo)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        employeeRepository.delete(employee);
    }
}
