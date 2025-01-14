package com.nado.smartcare.employee.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.nado.smartcare.employee.domain.Department;
import com.nado.smartcare.employee.repository.DepartmentRepository;

@SpringBootTest
class DepartmentRepositoryTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    void testFindById() {
        Long departmentId = 1L;
        Optional<Department> departmentOpt = departmentRepository.findById(departmentId);
        assertTrue(departmentOpt.isPresent(), "Department with ID 1 should exist");
        Department department = departmentOpt.get();
        System.out.println("Found Department: " + department.getDepartmentName());
    }
}