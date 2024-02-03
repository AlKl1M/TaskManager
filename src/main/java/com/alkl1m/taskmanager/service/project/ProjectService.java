package com.alkl1m.taskmanager.service.project;

import com.alkl1m.taskmanager.dto.project.CreateProjectCommand;
import com.alkl1m.taskmanager.dto.project.UpdateProjectCommand;
import com.alkl1m.taskmanager.dto.project.FindProjectsQuery;
import com.alkl1m.taskmanager.dto.project.ProjectDto;
import com.alkl1m.taskmanager.dto.project.ProjectsPagedResult;

import java.util.List;

public interface ProjectService {
    ProjectsPagedResult<ProjectDto> findProjects(FindProjectsQuery query);
    ProjectDto create(CreateProjectCommand cmd);
    void update(UpdateProjectCommand cmd);
    void delete(Long id);
    void changeStatus(Long id);
    List<ProjectDto> search(String query);
}
