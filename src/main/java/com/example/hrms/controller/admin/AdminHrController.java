package com.example.hrms.controller.admin;

import com.example.hrms.dto.hr.HrCreateRequest;
import com.example.hrms.dto.hr.HrResponse;
import com.example.hrms.dto.hr.HrUpdateRequest;
import com.example.hrms.service.HrManagementService;
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
@RequestMapping("/api/admin/hr")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminHrController {

    private final HrManagementService hrManagementService;

    @PostMapping
    public ResponseEntity<HrResponse> createHr(@Valid @RequestBody HrCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(hrManagementService.createHr(request));
    }

    @GetMapping
    public ResponseEntity<Page<HrResponse>> getAllHr(
            @RequestParam(required = false) String search,
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
                hrManagementService.getAllHrs(search, pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<HrResponse> getHrById(@PathVariable Long id) {
        return ResponseEntity.ok(hrManagementService.getHrById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HrResponse> updateHr(
            @PathVariable Long id,
            @Valid @RequestBody HrUpdateRequest request) {

        return ResponseEntity.ok(hrManagementService.updateHr(id, request));
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<?> disableHr(@PathVariable Long id) {
        hrManagementService.disableHr(id);
        return ResponseEntity.ok("HR disabled");
    }

    @PatchMapping("/{id}/enable")
    public ResponseEntity<?> enableHr(@PathVariable Long id) {
        hrManagementService.enableHr(id);
        return ResponseEntity.ok("HR enabled");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHr(@PathVariable Long id) {
        hrManagementService.deleteHr(id);
        return ResponseEntity.noContent().build();
    }
}