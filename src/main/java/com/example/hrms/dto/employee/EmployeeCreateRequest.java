package com.example.hrms.dto.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EmployeeCreateRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min=6 , max=20)
    private String password;

    @NotBlank
    @Size(min=3 , max=50)
    private String fullName;

    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid phone number")
    private String phone;

    private String department;
    private String designation;
    private LocalDate joiningDate;
}
