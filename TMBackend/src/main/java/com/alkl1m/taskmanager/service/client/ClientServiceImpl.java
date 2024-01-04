package com.alkl1m.taskmanager.service.client;

import com.alkl1m.taskmanager.dto.ProjectDto;
import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.repository.ProjectRepository;
import com.alkl1m.taskmanager.repository.TaskRepository;
import com.alkl1m.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService{
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream().map(Project::getProjectDto).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDto> getProjectsByName(String name) {
        return projectRepository.findAllByNameContaining(name).stream().map(Project::getProjectDto).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDto> getProjectsByUserEmail(String email) {
        User user = userRepository.findByEmail(email);
        List<Project> projects = projectRepository.findByUser(user);
        List<ProjectDto> projectDtos = projects.stream()
                .map(Project::getProjectDto)
                .collect(Collectors.toList());
        return projectDtos;
    }
}
