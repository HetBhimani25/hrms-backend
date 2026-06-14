package com.example.hrms.service;

import com.example.hrms.entity.ActivityLog;
import com.example.hrms.repository.ActivityLogRepository;
import com.example.hrms.repository.ActivityLogSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    /**
     * Log an activity with full context.
     *
     * @param message    Human-readable message (e.g. "Created new HR: HR One")
     * @param entityType Module/entity affected: HR, MANAGER, EMPLOYEE, LEAVE, SYSTEM
     * @param actionType Operation performed: CREATE, UPDATE, DELETE, DISABLE, ENABLE, APPROVE, REJECT, ASSIGN
     * @param targetUser Name of the affected user (e.g. "HR One")
     */
    @Transactional
    public void logActivity(String message, String entityType, String actionType, String targetUser) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String performedBy = auth.getName();
        String performedByRole = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(r -> r.startsWith("ROLE_"))
                .map(r -> r.replace("ROLE_", ""))
                .findFirst()
                .orElse("UNKNOWN");

        ActivityLog log = new ActivityLog();
        log.setMessage(message);
        log.setEntityType(entityType);
        log.setActionType(actionType);
        log.setTargetUser(targetUser);
        log.setTargetUserRole(entityType);
        log.setPerformedBy(performedBy);
        log.setPerformedByRole(performedByRole);

        activityLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<ActivityLog> getRecentActivities() {
        return activityLogRepository.findTop5ByOrderByTimestampDesc();
    }

    /**
     * Fetch paginated logs with optional filters.
     * Any null/blank/ALL value means "no filter on this dimension".
     */
    @Transactional(readOnly = true)
    public Page<ActivityLog> getAllActivities(
            String entityType,
            String actionType,
            String performedByRole,
            String dateRange,
            String dateFrom,
            String dateTo,
            String search,
            Pageable pageable) {

        // Resolve entity type
        String resolvedEntity = resolveFilter(entityType);
        String resolvedAction = resolveFilter(actionType);
        String resolvedRole   = resolveFilter(performedByRole);
        String resolvedSearch = (search != null && !search.isBlank()) ? search.trim() : null;

        // Resolve date range
        Instant from = null;
        Instant to   = null;

        if ("CUSTOM".equalsIgnoreCase(dateRange)) {
            if (dateFrom != null && !dateFrom.isBlank()) {
                from = LocalDate.parse(dateFrom).atStartOfDay().toInstant(ZoneOffset.UTC);
            }
            if (dateTo != null && !dateTo.isBlank()) {
                to = LocalDate.parse(dateTo).atTime(23, 59, 59).toInstant(ZoneOffset.UTC);
            }
        } else if (dateRange != null && !dateRange.isBlank()) {
            LocalDate today = LocalDate.now(ZoneOffset.UTC);
            switch (dateRange.toUpperCase()) {
                case "TODAY"     -> { from = today.atStartOfDay().toInstant(ZoneOffset.UTC);
                                      to   = today.atTime(23,59,59).toInstant(ZoneOffset.UTC); }
                case "YESTERDAY" -> { from = today.minusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
                                      to   = today.minusDays(1).atTime(23,59,59).toInstant(ZoneOffset.UTC); }
                case "LAST_7"    -> { from = today.minusDays(7).atStartOfDay().toInstant(ZoneOffset.UTC); }
                case "LAST_30"   -> { from = today.minusDays(30).atStartOfDay().toInstant(ZoneOffset.UTC); }
                default          -> { /* no date filter */ }
            }
        }

        return activityLogRepository.findAll(
                ActivityLogSpec.withFilters(resolvedEntity, resolvedAction, resolvedRole, from, to, resolvedSearch),
                pageable);
    }

    /** Returns null if the value is null, blank, or equals "ALL" / "ALL_*" */
    private String resolveFilter(String value) {
        if (value == null || value.isBlank()) return null;
        if (value.equalsIgnoreCase("ALL") || value.toUpperCase().startsWith("ALL_")) return null;
        return value.trim().toUpperCase();
    }
}
