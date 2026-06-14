package com.example.hrms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class LoginResponseDTO {
    private List<String> roles;
    private String fullName;
}