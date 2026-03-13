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

import com.example.hrms.exception.BadRequestException;
import com.example.hrms.exception.ResourceNotFoundException;

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

        if (userRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException("Email already exists");

        Role employeeRole = roleRepository.findByRoleName("ROLE_EMPLOYEE")
                .orElseThrow(() -> new ResourceNotFoundException("ROLE_EMPLOYEE not found"));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.getRoles().add(employeeRole);

        user = userRepository.save(user);

        EmployeeProfile profile = new EmployeeProfile();
        profile.setUser(user);
        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setDepartment(request.getDepartment());
        profile.setDesignation(request.getDesignation());
        profile.setJoiningDate(request.getJoiningDate());
        profile.setEmployeeCode("EMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        profile.setStatus(UserStatus.ACTIVE);

        employeeProfileRepository.save(profile);

        return mapToResponse(profile);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeProfileRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        EmployeeProfile profile = employeeProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        return mapToResponse(profile);
    }

    public EmployeeResponse updateEmployee(Long employeeId, EmployeeUpdateRequest request) {

        EmployeeProfile profile = employeeProfileRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (!profile.getUser().getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        if (profile.getStatus() != UserStatus.ACTIVE) {
            throw new BadRequestException("Only ACTIVE employees can be updated");
        }

        profile.getUser().setEmail(request.getEmail());
        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setDepartment(request.getDepartment());
        profile.setDesignation(request.getDesignation());
        profile.setJoiningDate(request.getJoiningDate());

        employeeProfileRepository.save(profile);

        return mapToResponse(profile);
    }

    public void disableEmployee(Long employeeId) {

        EmployeeProfile profile = employeeProfileRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        User user = profile.getUser();

        profile.setStatus(UserStatus.DISABLED);
        user.setEnabled(false);

        employeeProfileRepository.save(profile);
        userRepository.save(user);
    }

    public void deleteEmployee(Long id) {

        EmployeeProfile profile = employeeProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        User user = profile.getUser();

        profile.setStatus(UserStatus.DISABLED);
        user.setEnabled(false);

        employeeProfileRepository.save(profile);
        userRepository.save(user);
    }

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
        response.setStatus(profile.getStatus().name());
        return response;
    }
}