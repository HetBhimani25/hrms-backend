package com.example.hrms.controller.hr;

import com.example.hrms.dto.dashboard.HrDashboardResponse;
import com.example.hrms.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hr/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('HR')")
public class HrDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public HrDashboardResponse getStats() {
        return dashboardService.getHrDashboardStats();
    }
}
