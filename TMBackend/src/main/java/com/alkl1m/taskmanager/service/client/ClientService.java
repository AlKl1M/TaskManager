package com.alkl1m.taskmanager.service.client;

import com.alkl1m.taskmanager.dto.ProjectDto;
import com.alkl1m.taskmanager.dto.TaskDto;

import java.io.IOException;
import java.util.List;

public interface ClientService {
    List<ProjectDto> getAllProjects();
    List<ProjectDto> getProjectsByName(String name);
    List<ProjectDto> getProjectsByUserEmail(String email);
    List<TaskDto> getTasksByProjectId(Long projectId, String email);
    ProjectDto postProject(ProjectDto projectDto, String email);
    void deleteProject(Long projectId, String email);
    ProjectDto updateProject(Long projectId, ProjectDto projectDto, String email);
}
