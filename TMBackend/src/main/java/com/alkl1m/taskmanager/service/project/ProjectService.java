package com.alkl1m.taskmanager.service.project;

import com.alkl1m.taskmanager.dto.project.CreateProjectCommand;
import com.alkl1m.taskmanager.dto.project.UpdateProjectCommand;
import com.alkl1m.taskmanager.dto.project.FindProjectsQuery;
import com.alkl1m.taskmanager.dto.project.ProjectDto;
import com.alkl1m.taskmanager.dto.project.ProjectsPagedResult;
import com.alkl1m.taskmanager.entity.Project;

public interface ProjectService {
    ProjectsPagedResult<ProjectDto> findProjects(FindProjectsQuery query, Long userId);
    ProjectDto create(CreateProjectCommand cmd, Long userId);
    void update(UpdateProjectCommand cmd, Long userId);
    void delete(Long id, Long userId);
    void changeStatus(Long id, Long userId);
    Project getProjectById(Long id, Long userId);
}
