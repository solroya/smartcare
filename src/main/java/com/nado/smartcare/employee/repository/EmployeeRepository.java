package com.nado.smartcare.employee.repository;

import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.employee.domain.type.TypeOfEmployee;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeNo(Long EmployeeNo);
    
    Optional<Employee> findByEmployeeId(String EmployeeId);

    Optional<Employee> findByEmployeeEmail(String employeeEmail);

    List<Employee> findByDepartment_DepartmentNameAndTypeOfEmployee(String departmentName, TypeOfEmployee typeOfEmployee);

    @Query("SELECT e FROM Employee e WHERE e.department.departmentName = :departmentName AND e.typeOfEmployee = :typeOfEmployee")
    List<Employee> findDoctorsByDepartment(@Param("departmentName") String departmentName, @Param("typeOfEmployee") TypeOfEmployee typeOfEmployee);
}
