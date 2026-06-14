package com.example.hrms.controller.admin;

import com.example.hrms.dto.employee.EmployeeResponse;
import com.example.hrms.service.EmployeeManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/employees")
@RequiredArgsConstructor
public class AdminEmployeeController {

    private final EmployeeManagementService employeeManagementService;

    @GetMapping
    public Page<EmployeeResponse> getAllEmployees(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department,
            @PageableDefault() Pageable pageable) {
        return employeeManagementService.getAllEmployees(search, department, pageable);
    }

    @GetMapping("/{id}")
    public EmployeeResponse getEmployeeById(@PathVariable Long id) {
        return employeeManagementService.getEmployeeById(id);
    }
}
