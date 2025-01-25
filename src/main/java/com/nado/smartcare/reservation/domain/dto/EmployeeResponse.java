package com.nado.smartcare.reservation.domain.dto;

import com.nado.smartcare.employee.domain.Employee;

public class EmployeeResponse {
    private Long employeeNo;
    private  String employeeName;
    private  String departmentName;

    public EmployeeResponse(Long employeeNo, String employeeName, String departmentName) {

    }

    public static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(
                employee.getEmployeeNo(),
                employee.getEmployeeName(),
                employee.getDepartment().getDepartmentName()
        );
    }
}
