package com.nado.smartcare.employee.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nado.smartcare.employee.domain.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
	
	Optional<Department> findByDepartmentName(String departmentName);

	@Query("SELECT DISTINCT d FROM Department d " +
			"LEFT JOIN FETCH d.employees e " +
			"WHERE d.departmentName != 'HUMAN_RESOURCE' " +
			"AND d.departmentId IN (SELECT DISTINCT dept.departmentId FROM Department dept)")
	List<Department> findAllWithEmployees();


}