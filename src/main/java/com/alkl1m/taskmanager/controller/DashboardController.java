package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.dto.dashboard.DashboardDto;
import com.alkl1m.taskmanager.service.auth.UserDetailsImpl;
import com.alkl1m.taskmanager.service.dashboard.DashboardService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public DashboardDto getDashboardData(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return dashboardService.getDashboardData(userDetails.getId());
    }
}
