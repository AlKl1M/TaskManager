package com.alkl1m.taskmanager.dto.dashboard;

import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.Task;

import java.util.List;

public record DashboardDto(List<Project> projects,
                           List<Task> tasks) {
}
