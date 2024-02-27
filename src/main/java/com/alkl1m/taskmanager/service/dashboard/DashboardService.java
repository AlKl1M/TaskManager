package com.alkl1m.taskmanager.service.dashboard;

import com.alkl1m.taskmanager.dto.dashboard.DashboardDto;

public interface DashboardService {
    DashboardDto getDashboardData(Long userId);
}
