package com.example.hrms.repository;

import com.example.hrms.entity.LeaveRequest;
import com.example.hrms.entity.EmployeeProfile;

import com.example.hrms.entity.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployee(EmployeeProfile employee);

    List<LeaveRequest> findByStatus(LeaveStatus status);


}