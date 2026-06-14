package com.example.hrms.controller.hr;

import com.example.hrms.dto.employee.EmployeeCreateRequest;
import com.example.hrms.dto.employee.EmployeeResponse;
import com.example.hrms.dto.employee.EmployeeUpdateRequest;
import com.example.hrms.dto.manager.ManagerResponse;
import com.example.hrms.service.EmployeeManagementService;
import com.example.hrms.service.ManagerManagementService;
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
    private final ManagerManagementService managerManagementService;

    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(employeeManagementService.createEmployee(request));
    }

    @GetMapping
    public ResponseEntity<Page<EmployeeResponse>> getAllEmployee(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 10;

        Sort sorting = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sorting);

        return ResponseEntity.ok(
                employeeManagementService.getAllEmployees(search, department, pageable)
        );
    }

    @GetMapping("/managers")
    public ResponseEntity<java.util.List<ManagerResponse>> getManagersByDepartment(@RequestParam String department) {
        return ResponseEntity.ok(managerManagementService.getManagersByDepartment(department));
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
