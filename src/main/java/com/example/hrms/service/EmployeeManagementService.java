package com.example.hrms.service;

import com.example.hrms.dto.employee.EmployeeCreateRequest;
import com.example.hrms.dto.employee.EmployeeResponse;
import com.example.hrms.dto.employee.EmployeeUpdateRequest;
import com.example.hrms.entity.*;
import com.example.hrms.repository.EmployeeProfileRepository;
import com.example.hrms.repository.ManagerProfileRepository;
import com.example.hrms.repository.RoleRepository;
import com.example.hrms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hrms.exception.BadRequestException;
import com.example.hrms.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeManagementService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final ManagerProfileRepository managerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActivityLogService activityLogService;

    public EmployeeResponse createEmployee(EmployeeCreateRequest request) {

        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            if (user.isDeleted()) {
                throw new BadRequestException("Employee with the email ->' " + request.getEmail() + " ' is already exists in system but deleted");
            }
            throw new BadRequestException("Email already exists");
        });

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

        if (request.getManagerId() != null) {
            ManagerProfile manager = managerProfileRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
            profile.setReportingManager(manager);
        }

        employeeProfileRepository.save(profile);
        activityLogService.logActivity("Created new Employee: " + profile.getFullName(), "EMPLOYEE", "CREATE", profile.getFullName());

        return mapToResponse(profile);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getAllEmployees(String search, String department, Pageable pageable) {
        if (search != null && !search.isBlank() && department != null && !department.isBlank()) {
            return employeeProfileRepository.findByFullNameContainingIgnoreCaseAndDepartmentAndDeletedFalse(search, department, pageable)
                    .map(this::mapToResponse);
        } else if (search != null && !search.isBlank()) {
            return employeeProfileRepository.findByFullNameContainingIgnoreCaseAndDeletedFalse(search, pageable)
                    .map(this::mapToResponse);
        } else if (department != null && !department.isBlank()) {
            return employeeProfileRepository.findByDepartmentAndDeletedFalse(department, pageable)
                    .map(this::mapToResponse);
        }
        return employeeProfileRepository.findByDeletedFalse(pageable)
                .map(this::mapToResponse);
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

        if (!profile.getUser().getEmail().equals(request.getEmail())) {
            userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
                if (user.isDeleted()) {
                    throw new BadRequestException("Employee with the email ->' " + request.getEmail() + " ' is already exists in system but deleted");
                }
                throw new BadRequestException("Email already exists");
            });
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
        activityLogService.logActivity("Updated Employee details: " + profile.getFullName(), "EMPLOYEE", "UPDATE", profile.getFullName());

        return mapToResponse(profile);
    }

    public void disableEmployee(Long employeeId) {

        EmployeeProfile profile = employeeProfileRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        User user = profile.getUser();

        profile.setStatus(UserStatus.INACTIVE);
        user.setEnabled(false);
        user.setStatus(UserStatus.INACTIVE);

        employeeProfileRepository.save(profile);
        userRepository.save(user);
        activityLogService.logActivity("Disabled Employee account: " + profile.getFullName(), "EMPLOYEE", "DISABLE", profile.getFullName());
    }

    public void deleteEmployee(Long id) {

        EmployeeProfile profile = employeeProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        User user = profile.getUser();

        profile.setStatus(UserStatus.INACTIVE);
        profile.setDeleted(true);
        user.setEnabled(false);
        user.setStatus(UserStatus.INACTIVE);
        user.setDeleted(true);

        employeeProfileRepository.save(profile);
        userRepository.save(user);
        activityLogService.logActivity("Deleted Employee account: " + profile.getFullName(), "EMPLOYEE", "DELETE", profile.getFullName());
    }

    @Transactional(readOnly = true)
    public List<ManagerProfile> getManagersByDepartment(String department) {
        return managerProfileRepository.findByDepartmentAndStatus(department, UserStatus.ACTIVE);
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

        if (profile.getReportingManager() != null) {
            response.setManagerId(profile.getReportingManager().getId());
            response.setManagerName(profile.getReportingManager().getFullName());
        }
        return response;
    }
}