package com.alkl1m.taskmanager.service.client;

import com.alkl1m.taskmanager.dto.ProjectDto;

import java.util.List;

public interface ClientService {
    List<ProjectDto> getAllProjects();
    List<ProjectDto> getProjectsByName(String name);
    List<ProjectDto> getProjectsByUserEmail(String email);
}
