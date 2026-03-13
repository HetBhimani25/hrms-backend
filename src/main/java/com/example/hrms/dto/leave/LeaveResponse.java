package com.example.hrms.dto.leave;

import com.example.hrms.entity.LeaveStatus;
import com.example.hrms.entity.LeaveType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

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
}