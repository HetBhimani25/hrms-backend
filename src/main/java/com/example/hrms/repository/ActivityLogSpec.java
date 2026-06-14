package com.example.hrms.repository;

import com.example.hrms.entity.ActivityLog;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogSpec {

    /**
     * Builds a JPA Specification for ActivityLog with optional filters.
     * Any null/blank parameter means "no filter on that dimension".
     */
    public static Specification<ActivityLog> withFilters(
            String entityType,
            String actionType,
            String performedByRole,
            Instant dateFrom,
            Instant dateTo,
            String search) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // ── Entity type ─────────────────────────────────────────
            if (entityType != null && !entityType.isBlank()) {
                predicates.add(cb.equal(root.get("entityType"), entityType));
            }

            // ── Action type ──────────────────────────────────────────
            if (actionType != null && !actionType.isBlank()) {
                predicates.add(cb.equal(root.get("actionType"), actionType));
            }

            // ── Performed by role ────────────────────────────────────
            if (performedByRole != null && !performedByRole.isBlank()) {
                predicates.add(cb.equal(root.get("performedByRole"), performedByRole));
            }

            // ── Date range ───────────────────────────────────────────
            if (dateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("timestamp"), dateFrom));
            }
            if (dateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("timestamp"), dateTo));
            }

            // ── Full-text search across message, targetUser, performedBy
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("message")),     pattern),
                        cb.like(cb.lower(root.get("targetUser")),  pattern),
                        cb.like(cb.lower(root.get("performedBy")), pattern)
                ));
            }

            // ── Always order latest first ────────────────────────────
            query.orderBy(cb.desc(root.get("timestamp")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
