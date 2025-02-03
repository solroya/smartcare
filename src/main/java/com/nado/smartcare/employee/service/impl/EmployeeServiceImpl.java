package com.nado.smartcare.employee.service.impl;

import com.nado.smartcare.employee.domain.Department;
import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.employee.domain.dto.DepartmentDto;
import com.nado.smartcare.employee.domain.type.TypeOfEmployee;
import com.nado.smartcare.employee.domain.dto.EmployeeDto;
import com.nado.smartcare.employee.repository.DepartmentRepository;
import com.nado.smartcare.employee.repository.EmployeeRepository;
import com.nado.smartcare.employee.service.EmployeeService;
import com.nado.smartcare.member.domain.Member;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<EmployeeDto> findById(Long employeeNo) {
        return employeeRepository.findByEmployeeNo(employeeNo)
                .map(EmployeeDto::from);
    }

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
    	String encodedPassword = passwordEncoder.encode(employeeDto.employeePass());
    	log.info("employee에 저장된 departmentId는? ==> : {}", employeeDto.departmentId());
/*        Department department = departmentRepository.findById(employeeDto.departmentId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Department ID"));*/

        Employee employee = Employee.of(
                employeeDto.employeeId(),
                employeeDto.employeeName(),
                encodedPassword,
                employeeDto.employeeEmail(),
                employeeDto.employeeGender(),
                employeeDto.employeeBirthday(),
                employeeDto.employeePhoneNumber(),
                employeeDto.isSocial(),
                employeeDto.departmentId()
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

    @Transactional(readOnly = true)
    @Override
    public List<EmployeeDto> findDoctorsByDepartment(String departmentName) {
        log.info("Fetching doctors for department: " + departmentName);
        List<Employee> doctors = employeeRepository.findByDepartment_DepartmentNameAndTypeOfEmployee(departmentName, TypeOfEmployee.DOCTOR);

        if (doctors.isEmpty()) {
            log.warn("No doctors found for department: " + departmentName);
        }

        return doctors.stream()
                .map(EmployeeDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentDto> getAllDepartmentsWithDoctors() {
        return departmentRepository.findAll().stream()
                .map(DepartmentDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public Department getDepartmentById(Long employeeNo) {
        return departmentRepository.findById(employeeNo)
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));
    }

    @Override
    public Department getDepartmentByEmployeeNo(Long employeeNo) {
        return employeeRepository.findDepartmentByEmployeeNo(employeeNo)
                .orElseThrow(() -> new IllegalArgumentException("Department not found for employeeNo: " + employeeNo));
    }

    @Override
    public List<Employee> findByEmployeeByDepartment(String departmentName) {
        return employeeRepository.findByDepartment_DepartmentName(departmentName);
    }


    @Override
	public Employee login(String employeeId, String employePass) {
		Employee employee = employeeRepository.findByEmployeeId(employeeId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid Id or password"));
		
		if (!employePass.equals(employee.getEmployeePass())) {
			log.info("비밀번호 불일치 : 입력된 비밀번호 ===> {}", employePass);
			throw new IllegalArgumentException("Invalid Id or password");
		}
		
		log.info("로그인 성공 : 회원 이름 ==> {}", employee.getEmployeeName());
		return employee;
	}
}
