package com.alkl1m.taskmanager.service.user;

import com.alkl1m.taskmanager.dto.ProjectDto;

import java.util.List;

public interface UserService {
    List<ProjectDto> getAllProjects();
    List<ProjectDto> getProjectsByName(String name);
}
