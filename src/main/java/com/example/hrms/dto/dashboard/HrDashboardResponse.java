package com.example.hrms.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HrDashboardResponse {
    private long totalEmployees;
    private long pendingLeaves;
}
