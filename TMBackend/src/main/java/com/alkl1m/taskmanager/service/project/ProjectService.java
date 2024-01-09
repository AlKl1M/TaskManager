package com.alkl1m.taskmanager.service.project;

import com.alkl1m.taskmanager.dto.ProjectDto;

import java.util.List;

public interface ProjectService {
    List<ProjectDto> getAllProjects();
    List<ProjectDto> getProjectsByName(String name);
    List<ProjectDto> getProjectsByUserId();
    ProjectDto postProject(ProjectDto projectDto);
    void deleteProject(Long projectId);
    ProjectDto updateProject(Long projectId, ProjectDto projectDto);
}
