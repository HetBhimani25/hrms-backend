package com.example.hrms.controller.hr;

import com.example.hrms.dto.leave.LeaveResponse;
import com.example.hrms.service.LeaveManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hr/leave")
@RequiredArgsConstructor
@PreAuthorize("hasRole('HR')")
public class HrLeaveController {

    private final LeaveManagementService leaveManagementService;

    @GetMapping
    public List<LeaveResponse> getAllLeaves() {
        return leaveManagementService.getAllLeaves();
    }

    @PatchMapping("/{leaveId}/approve/{hrId}")
    public LeaveResponse approveLeave(
            @PathVariable Long leaveId,
            @PathVariable Long hrId) {

        return leaveManagementService.hrApproveLeave(leaveId, hrId);
    }

    @PatchMapping("/{leaveId}/reject/{hrId}")
    public LeaveResponse rejectLeave(
            @PathVariable Long leaveId,
            @PathVariable Long hrId) {

        return leaveManagementService.hrRejectLeave(leaveId, hrId);
    }
}