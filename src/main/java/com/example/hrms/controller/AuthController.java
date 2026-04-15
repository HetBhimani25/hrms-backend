package com.example.hrms.controller;


import com.example.hrms.dto.request.LoginRequestDTO;
import com.example.hrms.dto.request.LogoutRequestDTO;
import com.example.hrms.dto.request.RefreshTokenRequest;
import com.example.hrms.dto.response.LoginResponseDTO;
import com.example.hrms.entity.RefreshToken;
import com.example.hrms.entity.Role;
import com.example.hrms.entity.User;
import com.example.hrms.repository.*;
import com.example.hrms.security.JwtService;
import com.example.hrms.service.RefreshTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final HrProfileRepository hrProfileRepository;
    private final ManagerProfileRepository managerProfileRepository;
    private final EmployeeProfileRepository employeeProfileRepository;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            RefreshTokenRepository refreshTokenRepository,
            HrProfileRepository hrProfileRepository,
            ManagerProfileRepository managerProfileRepository,
            EmployeeProfileRepository employeeProfileRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.hrProfileRepository = hrProfileRepository;
        this.managerProfileRepository = managerProfileRepository;
        this.employeeProfileRepository = employeeProfileRepository;
    }

    private String getFullName(String email, List<String> roles) {
        if (roles.contains("ROLE_ADMIN")) return "Administrator";
        if (roles.contains("ROLE_HR")) {
            return hrProfileRepository.findByUserEmail(email)
                    .map(com.example.hrms.entity.HrProfile::getFullName)
                    .orElse("HR User");
        }
        if (roles.contains("ROLE_MANAGER")) {
            return managerProfileRepository.findByUserEmail(email)
                    .map(com.example.hrms.entity.ManagerProfile::getFullName)
                    .orElse("Manager");
        }
        if (roles.contains("ROLE_EMPLOYEE")) {
            return employeeProfileRepository.findByUserEmail(email)
                    .map(com.example.hrms.entity.EmployeeProfile::getFullName)
                    .orElse("Employee");
        }
        return "User";
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        String accessToken =
                jwtService.generateToken(
                        (org.springframework.security.core.userdetails.User)
                                authentication.getPrincipal()
                );

        String refreshToken =
                refreshTokenService
                        .createRefreshToken(request.getEmail())
                        .getToken();

        List<String> roles =
                authentication.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .filter(role -> role.startsWith("ROLE_"))
                        .toList();

        return new LoginResponseDTO(
                accessToken,
                refreshToken,
                roles,
                getFullName(request.getEmail(), roles)
        );
    }

    @PostMapping("/refresh")
    public LoginResponseDTO refreshToken(
            @RequestBody RefreshTokenRequest request) {

        RefreshToken refreshToken =
                refreshTokenService.verifyRefreshToken(
                        request.getRefreshToken());

        User user = refreshToken.getUser();

        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),
                        user.getRoles().stream()
                                .map(role ->
                                        new org.springframework.security.core.authority
                                                .SimpleGrantedAuthority(
                                                role.getRoleName()))
                                .toList()
                );

        String newAccessToken = jwtService.generateToken(userDetails);

        List<String> rolesList = user.getRoles().stream()
                .map(Role::getRoleName)
                .toList();

        return new LoginResponseDTO(
                newAccessToken,
                refreshToken.getToken(),
                rolesList,
                getFullName(user.getEmail(), rolesList)
        );
    }

    @PostMapping("/logout")
    public Map<String, String> logout(@RequestBody LogoutRequestDTO request) {

        refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .ifPresent(refreshTokenRepository::delete);

        return Map.of("message", "Logged out successfully");
    }

}
