package com.example.hrms.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdminDashboardResponse {

    private long totalHrs;
    private long totalManagers;
    private long activeEmployees;
}
