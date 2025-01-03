package com.nado.smartcare.employee.dto;

import com.nado.smartcare.employee.domain.Employee;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EmployeeDto(
        @NotBlank(message = "employeeId is Required")
        String employeeId,

        @NotBlank(message = "Password is required.")
        String employeeName,

        @NotBlank(message = "Name is required.")
        String employeePass,

        @Email(message = "Email must be valid.")
        String employeeEmail,

        @NotNull(message = "Birthday is required.")
        LocalDate employeeBirthday,

        @Pattern(regexp = "\\d{3}-\\d{3,4}-\\d{4}", message = "Phone number must follow the format 010-1234-5678.")
        String employeePhoneNumber,

        boolean isSocial,

        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

        public static EmployeeDto from(Employee employee) {
                return new EmployeeDto(
                        employee.getEmployeeId(),
                        employee.getEmployeeName(),
                        employee.getEmployeePass(),
                        employee.getEmployeeEmail(),
                        employee.getEmployeeBirthday(),
                        employee.getEmployeePhoneNumber(),
                        employee.isSocial(),
                        employee.getCreatedAt(),
                        employee.getUpdatedAt()
                );
        }
}
