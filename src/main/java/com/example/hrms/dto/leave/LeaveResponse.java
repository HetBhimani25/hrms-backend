package com.example.hrms.dto.leave;

import com.example.hrms.entity.LeaveStatus;
import com.example.hrms.entity.LeaveType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Instant;

@Getter
@Setter
public class LeaveResponse {

    private Long id;

    private String employeeName;

    private LeaveType leaveType;

    private LocalDate startDate;

    private LocalDate endDate;

    private String reason;

    private LeaveStatus status;

    private Instant createdAt;

    private String managerName;

    private Instant managerApprovedAt;

    private String hrName;

    private Instant hrApprovedAt;
}