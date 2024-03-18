package com.alkl1m.taskmanager.controller.payload.dashboard;

import com.alkl1m.taskmanager.entity.Task;

import java.util.List;

public record DashboardDto(List<DashboardProjectDto> projects,
                           List<Task> tasks) {
}
