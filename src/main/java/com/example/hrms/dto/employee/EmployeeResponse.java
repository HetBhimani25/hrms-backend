package com.example.hrms.dto.employee;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EmployeeResponse {
    private Long id;
    private String email;
    private boolean enabled;
    private String fullName;
    private String phone;
    private String department;
    private String designation;
    private String employeeCode;
    private LocalDate joiningDate;
    private String status;
    private Long managerId;
    private String managerName;
}
