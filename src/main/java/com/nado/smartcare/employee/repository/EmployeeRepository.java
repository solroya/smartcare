package com.nado.smartcare.employee.repository;

import com.nado.smartcare.employee.domain.Department;
import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.employee.domain.type.TypeOfEmployee;
import com.nado.smartcare.employee.domain.type.WorkingStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeNo(Long EmployeeNo);
    
    Optional<Employee> findByEmployeeId(String EmployeeId);

    Optional<Employee> findByEmployeeEmail(String employeeEmail);

    Optional<Employee> findByEmployeeName(String employeeName);

    List<Employee> findByDepartment_DepartmentNameAndTypeOfEmployee(String departmentName, TypeOfEmployee typeOfEmployee);

    /*@Query("SELECT e FROM Employee e WHERE e.department.departmentName = :departmentName AND e.typeOfEmployee = :typeOfEmployee")
    List<Employee> findDoctorsByDepartment(@Param("departmentName") String departmentName, @Param("typeOfEmployee") TypeOfEmployee typeOfEmployee);*/

    @Query("SELECT e FROM Employee e WHERE e.department.departmentName = :departmentName")
    List<Employee> findDoctorsByDepartmentName(@Param("departmentName") String departmentName);

    // 직원 번호로 Employee를 조회하면서 Department도 함께 가져오기
    @Query("SELECT e.department FROM Employee e WHERE e.employeeNo = :employeeNo")
    Optional<Department> findDepartmentByEmployeeNo(@Param("employeeNo") Long employeeNo);

    // BaseEntity 를 불러오는 내용때문에 
    List<Employee> findByDepartment_DepartmentName(String departmentName);
}
