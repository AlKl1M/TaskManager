package com.alkl1m.taskmanager.controller.payload.dashboard;

import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.enums.Status;

import java.time.Instant;

public record DashboardProjectDto(Long id,
                                  String name,
                                  String description,
                                  Instant createdAt,
                                  Instant doneAt,
                                  int completedTasks,
                                  int totalTasks,
                                  Status status) {
    public static DashboardProjectDto from(Project project, int completedTasks, int totalTasks) {
        return new DashboardProjectDto(project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt(),
                project.getDoneAt(),
                completedTasks,
                totalTasks,
                project.getStatus()
        );
    }
}
