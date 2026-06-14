package com.example.hrms.controller;

import com.example.hrms.dto.request.LoginRequestDTO;
import com.example.hrms.dto.response.LoginResponseDTO;
import com.example.hrms.entity.RefreshToken;
import com.example.hrms.entity.Role;
import com.example.hrms.entity.User;
import com.example.hrms.entity.UserStatus;
import com.example.hrms.repository.*;
import com.example.hrms.security.JwtService;
import com.example.hrms.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
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

    private void addTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(false); // SET TO TRUE IN PRODUCTION (HTTPS)
        accessCookie.setPath("/");
        accessCookie.setMaxAge(24 * 60 * 60); // 1 day
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false); // SET TO TRUE IN PRODUCTION (HTTPS)
        refreshCookie.setPath("/api/auth/refresh"); // Only sent to refresh endpoint
        refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        response.addCookie(refreshCookie);
    }

    private void clearTokenCookies(HttpServletResponse response) {
        Cookie accessCookie = new Cookie("accessToken", null);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(false);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);
        refreshCookie.setPath("/api/auth/refresh");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request, HttpServletResponse response) {

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

        addTokenCookies(response, accessToken, refreshToken);

        return new LoginResponseDTO(
                roles,
                getFullName(request.getEmail(), roles)
        );
    }

    @PostMapping("/refresh")
    public LoginResponseDTO refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshTokenString,
            HttpServletResponse response) {

        if (refreshTokenString == null || refreshTokenString.isBlank()) {
            throw new RuntimeException("Refresh token is missing");
        }

        RefreshToken refreshToken =
                refreshTokenService.verifyRefreshToken(refreshTokenString);

        User user = refreshToken.getUser();

        if (user.getStatus() == UserStatus.INACTIVE || user.getStatus() == UserStatus.DELETED) {
            throw new DisabledException("Account is disabled or inactive");
        }

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

        addTokenCookies(response, newAccessToken, refreshToken.getToken());

        return new LoginResponseDTO(
                rolesList,
                getFullName(user.getEmail(), rolesList)
        );
    }

    @PostMapping("/logout")
    public Map<String, String> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken != null) {
            refreshTokenRepository
                    .findByToken(refreshToken)
                    .ifPresent(refreshTokenRepository::delete);
        }

        clearTokenCookies(response);

        return Map.of("message", "Logged out successfully");
    }

}
