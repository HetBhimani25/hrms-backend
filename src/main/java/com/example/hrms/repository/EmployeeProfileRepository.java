package com.example.hrms.repository;

import com.example.hrms.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeProfileRepository extends JpaRepository<EmployeeProfile, Long> {

    Optional<EmployeeProfile> findByUser(User user);

    long countByStatus(UserStatus status);

    long countByDeletedFalse();

    Optional<EmployeeProfile> findByEmployeeCode(String employeeCode);

    Optional<EmployeeProfile> findByIdAndStatus(Long id, UserStatus status);

    Optional<EmployeeProfile> findByUserEmail(String email);

    Page<EmployeeProfile> findByDeletedFalse(Pageable pageable);

    Page<EmployeeProfile> findByFullNameContainingIgnoreCaseAndDeletedFalse(String fullName, Pageable pageable);
    
    Page<EmployeeProfile> findByDepartmentAndDeletedFalse(String department, Pageable pageable);

    Page<EmployeeProfile> findByFullNameContainingIgnoreCaseAndDepartmentAndDeletedFalse(String fullName, String department, Pageable pageable);
}
