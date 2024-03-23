package com.alkl1m.taskmanager.service.dashboard;

import com.alkl1m.taskmanager.controller.payload.dashboard.DashboardDto;

public interface DashboardService {
    DashboardDto getDashboardData(Long userId);
}
