package com.example.hrms.controller.employee;

import com.example.hrms.dto.leave.LeaveCreateRequest;
import com.example.hrms.dto.leave.LeaveResponse;
import com.example.hrms.service.LeaveManagementService;
import com.example.hrms.repository.EmployeeProfileRepository;
import com.example.hrms.entity.EmployeeProfile;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee/leave")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveManagementService leaveManagementService;
    private final EmployeeProfileRepository employeeProfileRepository;

    @PostMapping
    public LeaveResponse applyLeave(@RequestBody LeaveCreateRequest request, Authentication authentication) {
        EmployeeProfile profile = employeeProfileRepository.findByUserEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Employee profile not found"));
        request.setEmployeeId(profile.getId());
        return leaveManagementService.applyLeave(request);
    }

    @GetMapping
    public List<LeaveResponse> getAllLeaves(Authentication authentication) {
        return leaveManagementService.getLeavesByEmployeeEmail(authentication.getName());
    }
}