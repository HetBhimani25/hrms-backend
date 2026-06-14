package com.example.hrms.controller.admin;

import com.example.hrms.dto.dashboard.AdminDashboardResponse;
import com.example.hrms.entity.ActivityLog;
import com.example.hrms.service.ActivityLogService;
import com.example.hrms.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final ActivityLogService activityLogService;

    @GetMapping("/stats")
    public AdminDashboardResponse getDashboardStats() {
        return dashboardService.getAdminDashboardStats();
    }

    @GetMapping("/recent-activities")
    public List<ActivityLog> getRecentActivities() {
        return activityLogService.getRecentActivities();
    }

    @GetMapping("/activities")
    public Page<ActivityLog> getAllActivities(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String actionType,
            @RequestParam(required = false) String performedByRole,
            @RequestParam(required = false) String dateRange,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10) Pageable pageable) {

        return activityLogService.getAllActivities(
                entityType, actionType, performedByRole,
                dateRange, dateFrom, dateTo, search, pageable);
    }
}
