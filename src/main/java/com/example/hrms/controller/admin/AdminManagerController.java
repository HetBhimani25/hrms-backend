package com.example.hrms.controller.admin;

import com.example.hrms.dto.manager.ManagerCreateRequest;
import com.example.hrms.dto.manager.ManagerResponse;
import com.example.hrms.dto.manager.ManagerUpdateRequest;
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
@RequestMapping("/api/admin/manager")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminManagerController {

    private final ManagerManagementService managerManagementService;

    @PostMapping
    public ResponseEntity<ManagerResponse> createManager(@Valid @RequestBody ManagerCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(managerManagementService.createManager(request));
    }

    @GetMapping
    public ResponseEntity<Page<ManagerResponse>> getAllManagers(
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
        return ResponseEntity.ok(managerManagementService.getAllManagers(search, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ManagerResponse> getManagerById(@PathVariable Long id) {
        return ResponseEntity.ok(managerManagementService.getManagerById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ManagerResponse> updateManager(@PathVariable Long id, @RequestBody ManagerUpdateRequest request) {
        return ResponseEntity.ok(managerManagementService.updateManager(id, request));
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disableManager(@PathVariable Long id) {
        managerManagementService.disableManager(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteManager(@PathVariable Long id) {
        managerManagementService.deleteManager(id);
        return ResponseEntity.noContent().build();
    }
}