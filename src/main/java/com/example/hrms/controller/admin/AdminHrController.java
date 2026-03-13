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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/hr")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")   
public class AdminHrController {

    private final HrManagementService hrManagementService;

    @PostMapping("/create")
    public ResponseEntity<HrResponse> createHr(@Valid @RequestBody HrCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(hrManagementService.createHr(request));
    }

    @GetMapping
    public ResponseEntity<List<HrResponse>> getAllHr(){
        return ResponseEntity.ok(hrManagementService.getAllHrs());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<HrResponse> getHrById(@PathVariable Long id) {
        return ResponseEntity.ok(hrManagementService.getHrById(id));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<HrResponse> updateHr(@PathVariable Long id,@Valid @RequestBody HrUpdateRequest request) {
        return ResponseEntity.ok(hrManagementService.updateHr(id, request));
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disableHr(@PathVariable Long id) {
        hrManagementService.disableHr(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteHr(@PathVariable Long id) {
        hrManagementService.deleteHr(id);
        return ResponseEntity.noContent().build();
    }

}
