package com.example.hrms.controller.hr;

import com.example.hrms.dto.leave.LeaveResponse;
import com.example.hrms.service.LeaveManagementService;
import com.example.hrms.repository.HrProfileRepository;
import com.example.hrms.entity.HrProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hr/leave")
@RequiredArgsConstructor
@PreAuthorize("hasRole('HR')")
public class HrLeaveController {

    private final LeaveManagementService leaveManagementService;
    private final HrProfileRepository hrProfileRepository;

    @GetMapping
    public List<LeaveResponse> getAllLeaves() {
        return leaveManagementService.getAllLeaves();
    }

    @PatchMapping("/{leaveId}/approve")
    public LeaveResponse approveLeave(
            @PathVariable Long leaveId, Authentication auth) {
        HrProfile profile = hrProfileRepository.findByUserEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return leaveManagementService.hrApproveLeave(leaveId, profile.getId());
    }

    @PatchMapping("/{leaveId}/reject")
    public LeaveResponse rejectLeave(
            @PathVariable Long leaveId, Authentication auth) {
        HrProfile profile = hrProfileRepository.findByUserEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return leaveManagementService.hrRejectLeave(leaveId, profile.getId());
    }
}