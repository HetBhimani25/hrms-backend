package com.example.hrms.repository;

import com.example.hrms.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ManagerProfileRepository extends JpaRepository<ManagerProfile, Long> {

    Optional<ManagerProfile> findByUser(User user);
    
    Optional<ManagerProfile> findByUserEmail(String email);

    long countByStatus(UserStatus status);

    long countByDeletedFalse();

    Optional<ManagerProfile> findByEmployeeCode(String employeeCode);

    Optional<ManagerProfile> findByIdAndStatus(Long id, UserStatus status);

    Page<ManagerProfile> findByDeletedFalse(Pageable pageable);

    Page<ManagerProfile> findByFullNameContainingIgnoreCaseAndDeletedFalse(String fullName, Pageable pageable);

    List<ManagerProfile> findByDepartmentAndStatus(String department, UserStatus status);
}
