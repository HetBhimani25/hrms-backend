package com.example.hrms.controller.manager;

import com.example.hrms.dto.leave.LeaveResponse;
import com.example.hrms.service.LeaveManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager/leave")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
public class ManagerLeaveController {

    private final LeaveManagementService leaveManagementService;

    @GetMapping
    public List<LeaveResponse> getAllLeaves() {
        return leaveManagementService.getAllLeaves();
    }

    @PatchMapping("/{leaveId}/approve/{managerId}")
    public LeaveResponse approveLeave(
            @PathVariable Long leaveId,
            @PathVariable Long managerId) {

        return leaveManagementService.managerApproveLeave(leaveId, managerId);
    }

    @PatchMapping("/{leaveId}/reject/{managerId}")
    public LeaveResponse rejectLeave(
            @PathVariable Long leaveId,
            @PathVariable Long managerId) {

        return leaveManagementService.managerRejectLeave(leaveId, managerId);
    }
}