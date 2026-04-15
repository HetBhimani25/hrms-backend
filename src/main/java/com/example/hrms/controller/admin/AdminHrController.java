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
            @RequestParam(defaultValue = "id,desc") String[] sort) {
        
        String[] sortParams = sort[0].split(",");
        Sort sorting = Sort.by(sortParams[0]);
        if (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")) {
            sorting = sorting.descending();
        } else if (sort.length > 1 && sort[1].equalsIgnoreCase("desc")) {
            sorting = sorting.descending();
        }
        
        Pageable pageable = PageRequest.of(page, size, sorting);
        return ResponseEntity.ok(hrManagementService.getAllHrs(search, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HrResponse> getHrById(@PathVariable Long id) {
        return ResponseEntity.ok(hrManagementService.getHrById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HrResponse> updateHr(@PathVariable Long id,@Valid @RequestBody HrUpdateRequest request) {
        return ResponseEntity.ok(hrManagementService.updateHr(id, request));
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disableHr(@PathVariable Long id) {
        hrManagementService.disableHr(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHr(@PathVariable Long id) {
        hrManagementService.deleteHr(id);
        return ResponseEntity.noContent().build();
    }

}
