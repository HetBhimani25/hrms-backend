package com.example.hrms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "hr_profiles",
        indexes = {
                @Index(name="idx_employee_code_hr", columnList="employeeCode")
        })
@Getter
@Setter
public class HrProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id" , nullable = false , unique = true)
    private User user;

    private String fullName;

    @Column(length = 15)
    private String phone;

    private String department;

    private String designation;

    @Column(unique = true)
    private String employeeCode;

    private LocalDate joiningDate;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean deleted = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status =  UserStatus.ACTIVE;

    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

}
