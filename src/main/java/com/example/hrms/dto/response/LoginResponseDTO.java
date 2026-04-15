package com.example.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class LoginResponseDTO {
    private String accessToken;
    private String refreshToken;
    private List<String> roles;
    private String fullName;

}