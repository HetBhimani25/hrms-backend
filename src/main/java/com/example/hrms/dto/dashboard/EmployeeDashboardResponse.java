package com.example.hrms.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDashboardResponse {
    private String fullName;
    private long totalLeaves;
    private long approvedLeaves;
    private long pendingLeaves;
}
