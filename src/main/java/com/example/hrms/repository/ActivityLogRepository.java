package com.example.hrms.repository;

import com.example.hrms.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ActivityLogRepository
        extends JpaRepository<ActivityLog, Long>,
                JpaSpecificationExecutor<ActivityLog> {

    List<ActivityLog> findTop5ByOrderByTimestampDesc();
}
