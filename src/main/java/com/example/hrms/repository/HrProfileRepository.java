package com.example.hrms.repository;

import com.example.hrms.entity.HrProfile;
import com.example.hrms.entity.User;
import com.example.hrms.entity.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HrProfileRepository extends JpaRepository<HrProfile, Long> {

    Optional<HrProfile> findByUser(User user);
    
    Optional<HrProfile> findByUserEmail(String email);

    long countByStatus(UserStatus status);

    long countByDeletedFalse();

    Optional<HrProfile> findByEmployeeCode(String employeeCode);

    Optional<HrProfile> findByIdAndStatus(Long id, UserStatus status);

    Page<HrProfile> findByDeletedFalse(Pageable pageable);

    Page<HrProfile> findByFullNameContainingIgnoreCaseAndDeletedFalse(String fullName, Pageable pageable);

    List<HrProfile> findByDepartmentAndStatus(String department, UserStatus status);
}