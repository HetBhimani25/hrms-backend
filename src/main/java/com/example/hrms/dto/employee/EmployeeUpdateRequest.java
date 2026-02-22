package com.example.hrms.dto.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EmployeeUpdateRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String fullName;
    private String phone;
    private String department;
    private String designation;
    private LocalDate joiningDate;
}
