package com.example.hrms.controller.hr;

import com.example.hrms.dto.employee.EmployeeCreateRequest;
import com.example.hrms.dto.employee.EmployeeResponse;
import com.example.hrms.dto.employee.EmployeeUpdateRequest;
import com.example.hrms.service.EmployeeManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hr/employee")
@PreAuthorize("hasRole('HR')")
@RequiredArgsConstructor
public class HrEmployeeController {

    private final EmployeeManagementService employeeManagementService;

    @PostMapping("/create")
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(employeeManagementService.createEmployee(request));
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployee(){
        return ResponseEntity.ok(employeeManagementService.getAllEmployees());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeManagementService.getEmployeeById(id));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable Long id,@Valid @RequestBody EmployeeUpdateRequest request) {
        return ResponseEntity.ok(employeeManagementService.updateEmployee(id, request));
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disableEmployee(@PathVariable Long id) {
        employeeManagementService.disableEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeManagementService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
