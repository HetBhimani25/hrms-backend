package com.example.hrms.controller.manager;

import com.example.hrms.dto.dashboard.ManagerDashboardResponse;
import com.example.hrms.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/manager/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
public class ManagerDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ManagerDashboardResponse getStats() {
        return dashboardService.getManagerDashboardStats();
    }
}
