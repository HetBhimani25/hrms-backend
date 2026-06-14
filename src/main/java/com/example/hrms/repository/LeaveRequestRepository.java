package com.example.hrms.repository;

import com.example.hrms.entity.LeaveRequest;
import com.example.hrms.entity.EmployeeProfile;

import com.example.hrms.entity.LeaveStatus;
import com.example.hrms.entity.ManagerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployee(EmployeeProfile employee);

    List<LeaveRequest> findByStatus(LeaveStatus status);

    long countByStatus(LeaveStatus status);

    List<LeaveRequest> findByEmployee_User_Email(String email);

    long countByEmployee_User_EmailAndStatus(String email, LeaveStatus status);

    List<LeaveRequest> findByEmployee_Department(String department);
    
    List<LeaveRequest> findByEmployee_ReportingManager(ManagerProfile manager);
}