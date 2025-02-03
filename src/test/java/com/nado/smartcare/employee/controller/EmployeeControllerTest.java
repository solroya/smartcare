package com.nado.smartcare.employee.controller;

import com.nado.smartcare.employee.domain.Department;
import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.employee.domain.type.EmployeeStatus;
import com.nado.smartcare.employee.domain.type.TypeOfEmployee;
import com.nado.smartcare.employee.domain.type.WorkingStatus;
import com.nado.smartcare.employee.service.EmployeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Employee Controller 테스트")
@WebMvcTest(value = EmployeeController.class)
class EmployeeControllerTest {

    private final MockMvc mvc;

    @MockBean
    private EmployeeService employeeService;



    public EmployeeControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[View] [GET] 직원 등록 페이지 호출")
    @Test
    void givenNothing_whenRequestJoinView_thenReturnsJoinView() throws Exception {
        mvc.perform(get("/employee/join"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("employee/join"));
    }

    @DisplayName("[Static Factory Method] Employee 생성 테스트")
    @Test
    void givenValidParams_whenCreateEmployeeUsingFactoryMethod_thenEmployeeCreated() {
        // Given
        String employeeId = "E123";
        String employeeName = "John Doe";
        String employeePass = "password123";
        String employeeEmail = "johndoe@example.com";
        boolean employeeGender = true;
        LocalDate employeeBirthday = LocalDate.of(1990, 1, 1);
        String employeePhoneNumber = "010-1234-5678";
        boolean isSocial = false;
        Department department = new Department();

        // When
        Employee employee = Employee.of(
                employeeId,
                employeeName,
                employeePass,
                employeeEmail,
                employeeGender,
                employeeBirthday,
                employeePhoneNumber,
                isSocial,
                department
        );

        // Then
        assertThat(employee).isNotNull();
        assertThat(employee.getEmployeeId()).isEqualTo(employeeId);
        assertThat(employee.getEmployeeName()).isEqualTo(employeeName);
        assertThat(employee.getEmployeePass()).isEqualTo(employeePass);
        assertThat(employee.getEmployeeEmail()).isEqualTo(employeeEmail);
        assertThat(employee.isEmployeeGender()).isEqualTo(employeeGender);
        assertThat(employee.getEmployeeBirthday()).isEqualTo(employeeBirthday);
        assertThat(employee.getEmployeePhoneNumber()).isEqualTo(employeePhoneNumber);
        assertThat(employee.isSocial()).isEqualTo(isSocial);
        assertThat(employee.getEmployeeStatus()).isEqualTo(EmployeeStatus.PENDING);
        assertThat(employee.getTypeOfEmployee()).isEqualTo(TypeOfEmployee.STAFF);
        assertThat(employee.getWorkingStatus()).isEqualTo(WorkingStatus.WORKING);
        assertThat(employee.getDepartment()).isEqualTo(department);
    }

    @DisplayName("[Update Method] Employee 정보 업데이트 테스트")
    @Test
    void givenValidParams_whenUpdateEmployee_thenEmployeeUpdated() {
        // Given
        Employee employee = new Employee(
                "E123",
                "John Doe",
                "password123",
                "johndoe@example.com",
                true,
                LocalDate.of(1990, 1, 1),
                "010-1234-5678",
                false,
                EmployeeStatus.APPROVED,
                TypeOfEmployee.STAFF,
                WorkingStatus.WORKING,
                new Department()
        );

        String newEmployeePass = "newPassword456";
        String newEmployeePhoneNumber = "010-9876-5432";

        // When
        employee.updateEmployee(newEmployeePass, newEmployeePhoneNumber);

        // Then
        assertThat(employee.getEmployeePass()).isEqualTo(newEmployeePass);
        assertThat(employee.getEmployeePhoneNumber()).isEqualTo(newEmployeePhoneNumber);
    }

    @DisplayName("[Update Method] null 또는 빈 문자열 업데이트 시 기존 값 유지 테스트")
    @Test
    void givenNullOrEmptyParams_whenUpdateEmployee_thenEmployeeNotUpdated() {
        // Given
        Employee employee = new Employee(
                "E123",
                "John Doe",
                "password123",
                "johndoe@example.com",
                true,
                LocalDate.of(1990, 1, 1),
                "010-1234-5678",
                false,
                EmployeeStatus.APPROVED,
                TypeOfEmployee.STAFF,
                WorkingStatus.WORKING,
                new Department()
        );

        String newEmployeePass = null;
        String newEmployeePhoneNumber = "";

        // When
        employee.updateEmployee(newEmployeePass, newEmployeePhoneNumber);

        // Then
        assertThat(employee.getEmployeePass()).isEqualTo("password123");
        assertThat(employee.getEmployeePhoneNumber()).isEqualTo("010-1234-5678");
    }
}