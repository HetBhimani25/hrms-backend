package com.example.hrms.service;

import com.example.hrms.dto.dashboard.AdminDashboardResponse;
import com.example.hrms.entity.UserStatus;
import com.example.hrms.repository.EmployeeProfileRepository;
import com.example.hrms.repository.HrProfileRepository;
import com.example.hrms.repository.ManagerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final HrProfileRepository hrProfileRepository;
    private final ManagerProfileRepository managerProfileRepository;
    private final EmployeeProfileRepository employeeProfileRepository;

    public AdminDashboardResponse getAdminDashboardStats() {

        long totalHrs = hrProfileRepository.count();

        long totalManagers = managerProfileRepository.count();

        long activeEmployees =
                employeeProfileRepository.countByStatus(UserStatus.ACTIVE);

        return new AdminDashboardResponse(
                totalHrs,
                totalManagers,
                activeEmployees
        );
    }
}