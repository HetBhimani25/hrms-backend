package com.example.hrms.service;

import com.example.hrms.dto.employee.EmployeeCreateRequest;
import com.example.hrms.dto.employee.EmployeeResponse;
import com.example.hrms.dto.employee.EmployeeUpdateRequest;
import com.example.hrms.entity.*;
import com.example.hrms.repository.EmployeeProfileRepository;
import com.example.hrms.repository.RoleRepository;
import com.example.hrms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeManagementService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeResponse createEmployee(EmployeeCreateRequest request) {

        System.out.println("-- CREATE Employee API HIT --");
        System.out.println("Email: " + request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) throw new RuntimeException("Email already exists");

        Role employeeRole = roleRepository.findByRoleName("ROLE_EMPLOYEE").orElseThrow(() -> new RuntimeException("ROLE_EMPLOYEE not found"));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.getRoles().add(employeeRole);

        user =  userRepository.save(user);
        System.out.println("-- User saved with ID: " + user.getId() + " --");

        EmployeeProfile profile = new EmployeeProfile();
        profile.setUser(user);
        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setDepartment(request.getDepartment());
        profile.setDesignation(request.getDesignation());
        profile.setJoiningDate(request.getJoiningDate());
        profile.setEmployeeCode("EMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        profile.setStatus(EmployeeStatus.ACTIVE);
        profile.setCreatedAt(Instant.now());
        profile.setUpdatedAt(Instant.now());

        employeeProfileRepository.save(profile);
        System.out.println("-- Employee Profile saved --");

        return mapToResponse(profile);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeProfileRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        EmployeeProfile profile = employeeProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return mapToResponse(profile);
    }

    public EmployeeResponse updateEmployee(Long employeeId, EmployeeUpdateRequest request) {

        EmployeeProfile profile = employeeProfileRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (!profile.getUser().getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (profile.getStatus() == EmployeeStatus.INACTIVE) {
            throw new RuntimeException("Cannot update inactive Employee");
        }

        profile.getUser().setEmail(request.getEmail());
        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setDepartment(request.getDepartment());
        profile.setDesignation(request.getDesignation());
        profile.setJoiningDate(request.getJoiningDate());
        profile.setUpdatedAt(Instant.now());

        employeeProfileRepository.save(profile);
        return mapToResponse(profile);
    }

    public void disableEmployee(Long employeeId) {

        EmployeeProfile profile = employeeProfileRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        profile.setStatus(EmployeeStatus.DISABLED);
        profile.getUser().setEnabled(false);

        User user = profile.getUser();
        user.setEnabled(false);

        employeeProfileRepository.save(profile);
        userRepository.save(user);

//        profile.getUser().setEnabled(false);
    }

    public void deleteEmployee(Long id) {

        EmployeeProfile profile = employeeProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        User user = profile.getUser();
        employeeProfileRepository.delete(profile);
        userRepository.delete(user);

//        hrProfileRepository.delete(profile);
//        userRepository.delete(profile.getUser());
    }
//
//    private String generateEmployeeCode() {
//        return "HR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
//    }

    private EmployeeResponse mapToResponse(EmployeeProfile profile) {
        EmployeeResponse response = new EmployeeResponse();
        response.setId(profile.getId());
        response.setEmail(profile.getUser().getEmail());
        response.setEnabled(profile.getUser().isEnabled());
        response.setFullName(profile.getFullName());
        response.setPhone(profile.getPhone());
        response.setDepartment(profile.getDepartment());
        response.setDesignation(profile.getDesignation());
        response.setEmployeeCode(profile.getEmployeeCode());
        response.setJoiningDate(profile.getJoiningDate());
        response.setStatus(profile.getStatus().toString());
        return response;
    }

}
