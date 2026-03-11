package com.example.hrms.service;

import com.example.hrms.dto.manager.ManagerCreateRequest;
import com.example.hrms.dto.manager.ManagerResponse;
import com.example.hrms.dto.manager.ManagerUpdateRequest;
import com.example.hrms.entity.*;
import com.example.hrms.repository.ManagerProfileRepository;
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
public class ManagerManagementService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ManagerProfileRepository managerProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public ManagerResponse createManager(ManagerCreateRequest request) {

        System.out.println("-- CREATE MANAGER API HIT --");
        System.out.println("Email: " + request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) throw new RuntimeException("Email already exists");

        Role managerRole = roleRepository.findByRoleName("ROLE_MANAGER").orElseThrow(() -> new RuntimeException("ROLE_MANAGER not found"));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.getRoles().add(managerRole);

        user = userRepository.save(user);
        System.out.println("-- User saved with ID: " + user.getId() + " --");

        ManagerProfile profile = new ManagerProfile();
        profile.setUser(user);
        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setDepartment(request.getDepartment() == null || request.getDepartment().isBlank() ? "Human Resource" : request.getDepartment());
        profile.setDesignation(request.getDesignation());
        profile.setJoiningDate(request.getJoiningDate());
        profile.setEmployeeCode("MGR-" + UUID.randomUUID().toString().substring(0,8).toUpperCase());
        profile.setStatus(UserStatus.ACTIVE);
        profile.setCreatedAt(Instant.now());
        profile.setUpdatedAt(Instant.now());

        managerProfileRepository.save(profile);
        System.out.println("-- Manager profile saved with ID: " + profile.getId() + " --");

        return mapToResponse(profile);

    }

    private ManagerResponse mapToResponse(ManagerProfile profile) {

        ManagerResponse response = new ManagerResponse();
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


    @Transactional(readOnly = true)
    public List<ManagerResponse> getAllManagers() {
        return managerProfileRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public ManagerResponse getManagerById(Long id) {
        ManagerProfile profile = managerProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MANAGER not found"));
        return mapToResponse(profile);
    }

    public ManagerResponse updateManager(Long managerId, ManagerUpdateRequest request) {

        ManagerProfile profile = managerProfileRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("MANAGER not found"));

        if (!profile.getUser().getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (profile.getStatus() == UserStatus.INACTIVE) {
            throw new RuntimeException("Cannot update inactive MANAGER");
        }

        profile.getUser().setEmail(request.getEmail());
        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setDepartment(request.getDepartment());
        profile.setDesignation(request.getDesignation());
        profile.setUpdatedAt(Instant.now());

        managerProfileRepository.save(profile);
        return mapToResponse(profile);
    }

    public void disableManager(Long managerId) {

        ManagerProfile profile = managerProfileRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("MANAGER not found"));

        profile.setStatus(UserStatus.DISABLED);
        profile.setUpdatedAt(Instant.now());

        User user = profile.getUser();
        user.setEnabled(false);

        managerProfileRepository.save(profile);
        userRepository.save(user);

//        profile.getUser().setEnabled(false);
    }

    public void deleteManager(Long id) {

        ManagerProfile profile = managerProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HR not found"));

        User user = profile.getUser();

        profile.setStatus(UserStatus.DISABLED);
        user.setEnabled(false);

        managerProfileRepository.save(profile);
        userRepository.save(user);
    }
//
//    private String generateEmployeeCode() {
//        return "HR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
//    }
}
