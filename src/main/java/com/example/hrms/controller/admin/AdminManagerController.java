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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/manager")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminManagerController {

    private final ManagerManagementService managerManagementService;

    @PostMapping("/create")
    public ResponseEntity<ManagerResponse> createManager(@Valid @RequestBody ManagerCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(managerManagementService.createManager(request));
    }

    @GetMapping
    public ResponseEntity<List<ManagerResponse>> getAllManagers() {
        return ResponseEntity.ok(managerManagementService.getAllManagers());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ManagerResponse> getManagerById(@PathVariable Long id) {
        return ResponseEntity.ok(managerManagementService.getManagerById(id));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<ManagerResponse> updateManager(@PathVariable Long id, @RequestBody ManagerUpdateRequest request) {
        return ResponseEntity.ok(managerManagementService.updateManager(id, request));
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disableManager(@PathVariable Long id) {
        managerManagementService.disableManager(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteManager(@PathVariable Long id) {
        managerManagementService.deleteManager(id);
        return ResponseEntity.noContent().build();
    }
}