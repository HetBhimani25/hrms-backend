package com.example.hrms.service;

import com.example.hrms.dto.leave.LeaveCreateRequest;
import com.example.hrms.dto.leave.LeaveResponse;
import com.example.hrms.entity.*;
import com.example.hrms.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveManagementService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final ManagerProfileRepository managerProfileRepository;
    private final HrProfileRepository hrProfileRepository;

    public LeaveResponse applyLeave(LeaveCreateRequest request) {

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new RuntimeException("Start date cannot be after end date");
        }

        EmployeeProfile employee = employeeProfileRepository
                .findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LeaveRequest leave = new LeaveRequest();

        leave.setEmployee(employee);
        leave.setLeaveType(request.getLeaveType());
        leave.setStartDate(request.getStartDate());
        leave.setEndDate(request.getEndDate());
        leave.setReason(request.getReason());
        leave.setStatus(LeaveStatus.PENDING);

        leaveRequestRepository.save(leave);

        return mapToResponse(leave);
    }

    public List<LeaveResponse> getAllLeaves() {

        return leaveRequestRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public LeaveResponse managerApproveLeave(Long leaveId, Long managerId) {

        LeaveRequest leave = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Leave already processed");
        }

        ManagerProfile manager = managerProfileRepository
                .findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        leave.setApprovedByManager(manager);
        leave.setStatus(LeaveStatus.MANAGER_APPROVED);

        leaveRequestRepository.save(leave);

        return mapToResponse(leave);
    }

    public LeaveResponse managerRejectLeave(Long leaveId, Long managerId) {

        LeaveRequest leave = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Leave already processed");
        }

        ManagerProfile manager = managerProfileRepository
                .findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        leave.setApprovedByManager(manager);
        leave.setStatus(LeaveStatus.REJECTED);

        leaveRequestRepository.save(leave);

        return mapToResponse(leave);
    }

    public LeaveResponse hrApproveLeave(Long leaveId, Long hrId) {

        LeaveRequest leave = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        if (leave.getStatus() != LeaveStatus.MANAGER_APPROVED) {
            throw new RuntimeException("Manager must approve first");
        }

        HrProfile hr = hrProfileRepository
                .findById(hrId)
                .orElseThrow(() -> new RuntimeException("HR not found"));

        leave.setApprovedByHr(hr);
        leave.setStatus(LeaveStatus.HR_APPROVED);

        leaveRequestRepository.save(leave);

        return mapToResponse(leave);
    }

    public LeaveResponse hrRejectLeave(Long leaveId, Long hrId) {

        LeaveRequest leave = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        if (leave.getStatus() != LeaveStatus.MANAGER_APPROVED) {
            throw new RuntimeException("Manager approval required before HR rejection");
        }

        HrProfile hr = hrProfileRepository
                .findById(hrId)
                .orElseThrow(() -> new RuntimeException("HR not found"));

        leave.setApprovedByHr(hr);
        leave.setStatus(LeaveStatus.REJECTED);

        leaveRequestRepository.save(leave);

        return mapToResponse(leave);
    }

    private LeaveResponse mapToResponse(LeaveRequest leave) {

        LeaveResponse res = new LeaveResponse();

        res.setId(leave.getId());
        res.setEmployeeName(leave.getEmployee().getFullName());
        res.setLeaveType(leave.getLeaveType());
        res.setStartDate(leave.getStartDate());
        res.setEndDate(leave.getEndDate());
        res.setReason(leave.getReason());
        res.setStatus(leave.getStatus());

        return res;
    }
}