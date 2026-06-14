package com.example.hrms.service;

import com.example.hrms.dto.dashboard.AdminDashboardResponse;
import com.example.hrms.dto.dashboard.EmployeeDashboardResponse;
import com.example.hrms.dto.dashboard.HrDashboardResponse;
import com.example.hrms.dto.dashboard.ManagerDashboardResponse;
import com.example.hrms.entity.LeaveStatus;
import com.example.hrms.entity.UserStatus;
import com.example.hrms.repository.EmployeeProfileRepository;
import com.example.hrms.repository.HrProfileRepository;
import com.example.hrms.repository.ManagerProfileRepository;
import com.example.hrms.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final HrProfileRepository hrProfileRepository;
    private final ManagerProfileRepository managerProfileRepository;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    public AdminDashboardResponse getAdminDashboardStats() {

        long totalHrs = hrProfileRepository.countByDeletedFalse();
        long totalManagers = managerProfileRepository.countByDeletedFalse();
        long totalEmployees = employeeProfileRepository.countByDeletedFalse();
        long activeEmployees = employeeProfileRepository.countByStatus(UserStatus.ACTIVE);

        return new AdminDashboardResponse(
                totalHrs,
                totalManagers,
                totalEmployees,
                activeEmployees
        );
    }

    public HrDashboardResponse getHrDashboardStats() {
        long activeEmployees = employeeProfileRepository.countByStatus(UserStatus.ACTIVE);
        long pendingLeaves = leaveRequestRepository.countByStatus(LeaveStatus.MANAGER_APPROVED);
        return new HrDashboardResponse(activeEmployees, pendingLeaves);
    }

    public ManagerDashboardResponse getManagerDashboardStats() {
        long activeEmployees = employeeProfileRepository.countByStatus(UserStatus.ACTIVE);
        long pendingLeaves = leaveRequestRepository.countByStatus(LeaveStatus.PENDING);
        return new ManagerDashboardResponse(activeEmployees, pendingLeaves);
    }

    public EmployeeDashboardResponse getEmployeeDashboardStats(String email) {
        String fullName = employeeProfileRepository.findByUserEmail(email)
                .map(com.example.hrms.entity.EmployeeProfile::getFullName)
                .orElse("Employee");
        long totalLeaves = leaveRequestRepository.findByEmployee_User_Email(email).size();
        long approvedLeaves = leaveRequestRepository.countByEmployee_User_EmailAndStatus(email, LeaveStatus.HR_APPROVED);
        long pendingLeaves = leaveRequestRepository.countByEmployee_User_EmailAndStatus(email, LeaveStatus.PENDING) +
                leaveRequestRepository.countByEmployee_User_EmailAndStatus(email, LeaveStatus.MANAGER_APPROVED);
        return new EmployeeDashboardResponse(fullName, totalLeaves, approvedLeaves, pendingLeaves);
    }
}