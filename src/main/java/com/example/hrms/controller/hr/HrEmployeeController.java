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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hr/employee")
@PreAuthorize("hasRole('HR')")
@RequiredArgsConstructor
public class HrEmployeeController {

    private final EmployeeManagementService employeeManagementService;

    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(employeeManagementService.createEmployee(request));
    }

    @GetMapping
    public ResponseEntity<Page<EmployeeResponse>> getAllEmployee(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort) {
        
        String[] sortParams = sort[0].split(",");
        Sort sorting = Sort.by(sortParams[0]);
        if (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")) {
            sorting = sorting.descending();
        } else if (sort.length > 1 && sort[1].equalsIgnoreCase("desc")) {
            sorting = sorting.descending();
        }
        
        Pageable pageable = PageRequest.of(page, size, sorting);
        return ResponseEntity.ok(employeeManagementService.getAllEmployees(search, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeManagementService.getEmployeeById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable Long id,@Valid @RequestBody EmployeeUpdateRequest request) {
        return ResponseEntity.ok(employeeManagementService.updateEmployee(id, request));
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disableEmployee(@PathVariable Long id) {
        employeeManagementService.disableEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeManagementService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
