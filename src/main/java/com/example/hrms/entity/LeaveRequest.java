package com.example.hrms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "leave_requests")
@Getter
@Setter
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeProfile employee;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private ManagerProfile approvedByManager;

    @ManyToOne
    @JoinColumn(name = "hr_id")
    private HrProfile approvedByHr;

    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    private Instant managerApprovedAt;

    private Instant hrApprovedAt;

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
