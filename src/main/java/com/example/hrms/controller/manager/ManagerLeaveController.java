package com.example.hrms.controller.manager;

import com.example.hrms.dto.leave.LeaveResponse;
import com.example.hrms.service.LeaveManagementService;
import com.example.hrms.repository.ManagerProfileRepository;
import com.example.hrms.entity.ManagerProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager/leave")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
public class ManagerLeaveController {

    private final LeaveManagementService leaveManagementService;
    private final ManagerProfileRepository managerProfileRepository;

    @GetMapping
    public List<LeaveResponse> getAllLeaves() {
        return leaveManagementService.getAllLeaves();
    }

    @PatchMapping("/{leaveId}/approve")
    public LeaveResponse approveLeave(
            @PathVariable Long leaveId, Authentication auth) {
        ManagerProfile profile = managerProfileRepository.findByUserEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return leaveManagementService.managerApproveLeave(leaveId, profile.getId());
    }

    @PatchMapping("/{leaveId}/reject")
    public LeaveResponse rejectLeave(
            @PathVariable Long leaveId, Authentication auth) {
        ManagerProfile profile = managerProfileRepository.findByUserEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return leaveManagementService.managerRejectLeave(leaveId, profile.getId());
    }
}