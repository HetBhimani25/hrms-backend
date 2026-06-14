package com.example.hrms.service;

import com.example.hrms.dto.hr.HrCreateRequest;
import com.example.hrms.dto.hr.HrResponse;
import com.example.hrms.dto.hr.HrUpdateRequest;
import com.example.hrms.entity.*;
import com.example.hrms.repository.HrProfileRepository;
import com.example.hrms.repository.RoleRepository;
import com.example.hrms.repository.UserRepository;
import com.example.hrms.exception.ResourceNotFoundException;
import com.example.hrms.exception.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class HrManagementService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HrProfileRepository hrProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActivityLogService activityLogService;

    public HrResponse createHr(HrCreateRequest request) {

        System.out.println("-- CREATE HR API HIT --");
        System.out.println("Email: " + request.getEmail());

        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            if (user.isDeleted()) {
                throw new BadRequestException("HR with the email ' " + request.getEmail() + " ' is already exists in system but deleted");
            }
            throw new BadRequestException("Email already exists");
        });

        Role hrRole = roleRepository.findByRoleName("ROLE_HR").orElseThrow(() -> new ResourceNotFoundException("ROLE_HR not found"));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.getRoles().add(hrRole);

        user =  userRepository.save(user);
        System.out.println("-- User saved with ID: " + user.getId() + " --");

        HrProfile profile = new HrProfile();
        profile.setUser(user);
        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setDepartment("Human Resource");
        profile.setDesignation(request.getDesignation());
        profile.setJoiningDate(request.getJoiningDate());
        profile.setEmployeeCode("HR-" + UUID.randomUUID().toString().substring(0,8).toUpperCase());
        profile.setStatus(UserStatus.ACTIVE);

//        HrProfile profile = new HrProfile();
//        profile.setUser(user);
//        profile.setFullName(request.getFullName());
//        profile.setPhone(request.getPhone());
//        profile.setDepartment(request.getDepartment());
//        profile.setDesignation(request.getDesignation());
//        profile.setJoiningDate(request.getJoiningDate());
//        profile.setEmployeeCode(generateEmployeeCode());
//        profile.setStatus(HrStatus.ACTIVE);
//        profile.setCreatedAt(Instant.now());
//        profile.setUpdatedAt(Instant.now());

        hrProfileRepository.save(profile);
        System.out.println("-- HR Profile saved with ID: " + profile.getId() + " --");

        activityLogService.logActivity("Created new HR: " + profile.getFullName(), "HR", "CREATE", profile.getFullName());

        return mapToResponse(profile);
    }

    @Transactional(readOnly = true)
    public Page<HrResponse> getAllHrs(String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return hrProfileRepository.findByFullNameContainingIgnoreCaseAndDeletedFalse(search, pageable)
                    .map(this::mapToResponse);
        }
        return hrProfileRepository.findByDeletedFalse(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public HrResponse getHrById(Long id) {
        HrProfile profile = hrProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HR not found"));
        return mapToResponse(profile);
    }

    public HrResponse updateHr(Long hrId, HrUpdateRequest request) {

        HrProfile profile = hrProfileRepository.findById(hrId)
                .orElseThrow(() -> new ResourceNotFoundException("HR not found"));

        if (!profile.getUser().getEmail().equals(request.getEmail())) {
            userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
                if (user.isDeleted()) {
                    throw new BadRequestException("HR with the email ->' " + request.getEmail() + " ' is already exists in system but deleted");
                }
                throw new BadRequestException("Email already exists");
            });
        }

        if (profile.getStatus() == UserStatus.INACTIVE) {
            throw new BadRequestException("Cannot update inactive HR");
        }

        profile.getUser().setEmail(request.getEmail());
        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setDepartment(request.getDepartment());
        profile.setDesignation(request.getDesignation());
        profile.setJoiningDate(request.getJoiningDate());

        hrProfileRepository.save(profile);
        activityLogService.logActivity("Updated HR details: " + profile.getFullName(), "HR", "UPDATE", profile.getFullName());
        return mapToResponse(profile);
    }

    public void disableHr(Long hrId) {
        HrProfile profile = hrProfileRepository.findById(hrId)
                .orElseThrow(() -> new RuntimeException("HR not found"));

        User user = profile.getUser();
        profile.setStatus(UserStatus.INACTIVE);
        user.setEnabled(false);
        user.setStatus(UserStatus.INACTIVE);

        hrProfileRepository.save(profile);
        userRepository.save(user);
        activityLogService.logActivity("Disabled HR account: " + profile.getFullName(), "HR", "DISABLE", profile.getFullName());
    }

    public void enableHr(Long hrId) {
        HrProfile profile = hrProfileRepository.findById(hrId)
                .orElseThrow(() -> new RuntimeException("HR not found"));

        User user = profile.getUser();
        profile.setStatus(UserStatus.ACTIVE);
        user.setEnabled(true);
        user.setStatus(UserStatus.ACTIVE);

        hrProfileRepository.save(profile);
        userRepository.save(user);
        activityLogService.logActivity("Enabled HR account: " + profile.getFullName(), "HR", "ENABLE", profile.getFullName());
    }

    public void deleteHr(Long id) {

        HrProfile profile = hrProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HR not found"));

        User user = profile.getUser();
        profile.setStatus(UserStatus.INACTIVE);
        profile.setDeleted(true);
        user.setEnabled(false);
        user.setStatus(UserStatus.INACTIVE);
        user.setDeleted(true);

        hrProfileRepository.save(profile);
        userRepository.save(user);
        activityLogService.logActivity("Deleted HR account: " + profile.getFullName(), "HR", "DELETE", profile.getFullName());
    }
//
//    private String generateEmployeeCode() {
//        return "HR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
//    }

    private HrResponse mapToResponse(HrProfile profile) {
        HrResponse response = new HrResponse();
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
