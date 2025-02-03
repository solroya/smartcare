package com.nado.smartcare.employee.controller;

import com.nado.smartcare.employee.domain.Department;
import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Employee 통합 테스트")
@SpringBootTest
@TestPropertySource(properties = "jasypt.encryptor.password=your-secret-password")
public class EmployeeCRUDTEST {
    @Autowired
    private EmployeeRepository employeeRepository;

    @DisplayName("직원 저장 테스트")
    @Test
    void givenEmployee_whenSave_thenStoredInDatabase() {
        // Given
        Department department = new Department(); // 더미 Department 객체 생성

        Employee employee = Employee.of(
                "E123",
                "John Doe",
                "password123",
                "johndoe@example.com",
                true,
                LocalDate.of(1990, 1, 1),
                "010-1234-5678",
                false,
                department
        );

        // When
        Employee savedEmployee = employeeRepository.save(employee);

        // Then
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getEmployeeId()).isEqualTo("E123");
        assertThat(savedEmployee.getDepartment()).isNotNull();
    }

}
