package com.example.hrms.controller.employee;

import com.example.hrms.dto.dashboard.EmployeeDashboardResponse;
import com.example.hrms.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employee/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYEE')")
public class EmployeeDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public EmployeeDashboardResponse getStats(Authentication authentication) {
        return dashboardService.getEmployeeDashboardStats(authentication.getName());
    }
}
