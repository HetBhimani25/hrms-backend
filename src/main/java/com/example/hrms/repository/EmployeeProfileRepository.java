package com.example.hrms.repository;

import com.example.hrms.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeProfileRepository extends JpaRepository<EmployeeProfile, Long> {

    Optional<EmployeeProfile> findByUser(User user);

    long countByStatus(UserStatus status);

    Optional<EmployeeProfile> findByEmployeeCode(String employeeCode);

    Optional<EmployeeProfile> findByIdAndStatus(Long id, UserStatus status);

    Optional<EmployeeProfile> findByUserEmail(String email);
}
