package com.example.hrms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "activity_logs")
@Getter
@Setter
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String message;

    /** Entity type: HR, MANAGER, EMPLOYEE, LEAVE, SYSTEM */
    @Column(nullable = false)
    private String entityType;

    /** Action type: CREATE, UPDATE, DELETE, DISABLE, ENABLE, APPROVE, REJECT, ASSIGN */
    @Column
    private String actionType;

    /** Who triggered the action (email) */
    @Column(nullable = false)
    private String performedBy;

    /** Role of the performer: ADMIN, HR, MANAGER */
    @Column
    private String performedByRole;

    /** Name of the user affected (e.g. "HR One") */
    @Column
    private String targetUser;

    /** Role of the target user: HR, MANAGER, EMPLOYEE */
    @Column
    private String targetUserRole;

    @Column(nullable = false)
    private Instant timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = Instant.now();
    }
}
