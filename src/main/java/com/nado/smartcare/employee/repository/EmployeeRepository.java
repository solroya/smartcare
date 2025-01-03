package com.nado.smartcare.employee.repository;

import com.nado.smartcare.employee.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeNo(Long EmployeeNo);
    
    Optional<Employee> findByEmployeeId(String EmployeeId);

    Optional<Employee> findByEmployeeEmail(String employeeEmail);

}
