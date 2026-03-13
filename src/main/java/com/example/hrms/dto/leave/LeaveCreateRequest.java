package com.example.hrms.dto.leave;

import com.example.hrms.entity.LeaveType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LeaveCreateRequest {

    private Long employeeId;

    private LeaveType leaveType;

    private LocalDate startDate;

    private LocalDate endDate;

    private String reason;
}