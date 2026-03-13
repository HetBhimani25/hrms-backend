package com.example.hrms.controller.admin;

import com.example.hrms.dto.dashboard.AdminDashboardResponse;
import com.example.hrms.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public AdminDashboardResponse getDashboardStats() {
        return dashboardService.getAdminDashboardStats();
    }
}
