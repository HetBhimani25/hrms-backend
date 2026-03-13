package com.example.hrms.controller.employee;

import com.example.hrms.dto.leave.LeaveCreateRequest;
import com.example.hrms.dto.leave.LeaveResponse;
import com.example.hrms.service.LeaveManagementService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee/leave")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveManagementService leaveManagementService;

    @PostMapping("/apply")
    public LeaveResponse applyLeave(@RequestBody LeaveCreateRequest request) {
        return leaveManagementService.applyLeave(request);
    }

    @GetMapping
    public List<LeaveResponse> getAllLeaves() {
        return leaveManagementService.getAllLeaves();
    }
}