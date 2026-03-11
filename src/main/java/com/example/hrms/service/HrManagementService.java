package com.example.hrms.service;

import com.example.hrms.dto.hr.HrCreateRequest;
import com.example.hrms.dto.hr.HrResponse;
import com.example.hrms.dto.hr.HrUpdateRequest;
import com.example.hrms.entity.*;
import com.example.hrms.repository.HrProfileRepository;
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
public class HrManagementService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HrProfileRepository hrProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public HrResponse createHr(HrCreateRequest request) {

        System.out.println("-- CREATE HR API HIT --");
        System.out.println("Email: " + request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) throw new RuntimeException("Email already exists");

        Role hrRole = roleRepository.findByRoleName("ROLE_HR").orElseThrow(() -> new RuntimeException("ROLE_HR not found"));

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
        profile.setDepartment(
                request.getDepartment() == null || request.getDepartment().isBlank()
                        ? "Human Resource"
                        : request.getDepartment()
        );
        profile.setDesignation(request.getDesignation());
        profile.setJoiningDate(request.getJoiningDate());
        profile.setEmployeeCode("HR-" + UUID.randomUUID().toString().substring(0,8).toUpperCase());
        profile.setStatus(UserStatus.ACTIVE);
        profile.setCreatedAt(Instant.now());
        profile.setUpdatedAt(Instant.now());

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
        System.out.println("-- HR Profile saved --");

        return mapToResponse(profile);
    }

    @Transactional(readOnly = true)
    public List<HrResponse> getAllHrs() {
        return hrProfileRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public HrResponse getHrById(Long id) {
        HrProfile profile = hrProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HR not found"));
        return mapToResponse(profile);
    }

    public HrResponse updateHr(Long hrId, HrUpdateRequest request) {

        HrProfile profile = hrProfileRepository.findById(hrId)
                .orElseThrow(() -> new RuntimeException("HR not found"));

        if (!profile.getUser().getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (profile.getStatus() == UserStatus.INACTIVE) {
            throw new RuntimeException("Cannot update inactive HR");
        }

        profile.getUser().setEmail(request.getEmail());
        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setDepartment(request.getDepartment());
        profile.setDesignation(request.getDesignation());
        profile.setJoiningDate(request.getJoiningDate());
        profile.setUpdatedAt(Instant.now());

        hrProfileRepository.save(profile);
        return mapToResponse(profile);
    }

    public void disableHr(Long hrId) {

        HrProfile profile = hrProfileRepository.findById(hrId)
                .orElseThrow(() -> new RuntimeException("HR not found"));

        profile.setStatus(UserStatus.DISABLED);
        profile.getUser().setEnabled(false);
//        profile.setUpdatedAt(Instant.now());

        User user = profile.getUser();
        user.setEnabled(false);

        hrProfileRepository.save(profile);
        userRepository.save(user);

//        profile.getUser().setEnabled(false);
    }

    public void deleteHr(Long id) {

        HrProfile profile = hrProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HR not found"));

        User user = profile.getUser();
        profile.setStatus(UserStatus.DISABLED);
        user.setEnabled(false);

        hrProfileRepository.save(profile);
        userRepository.save(user);
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
