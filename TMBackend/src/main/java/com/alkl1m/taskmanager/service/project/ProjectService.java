package com.alkl1m.taskmanager.service.project;

import com.alkl1m.taskmanager.dto.*;

public interface ProjectService {
    ProjectsPagedResult<ProjectDto> findProjects(FindProjectsQuery query);
    ProjectDto create(CreateProjectCommand cmd);
    void update(UpdateProjectCommand cmd);
    void delete(Long id);
}
