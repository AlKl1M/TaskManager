package com.alkl1m.taskmanager.service.project;

import com.alkl1m.taskmanager.dto.ProjectDto;
import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.repository.ProjectRepository;
import com.alkl1m.taskmanager.repository.UserRepository;
import com.alkl1m.taskmanager.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final HttpServletRequest httpServletRequest;
    private final JwtUtil jwtUtil;
    @Override
    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream().map(Project::getProjectDto).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDto> getProjectsByName(String name) {
        return projectRepository.findAllByNameContaining(name).stream().map(Project::getProjectDto).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDto> getProjectsByUserId() {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.extractId(token);
        Optional<User> optionalUser = userRepository.findById(userId);
        List<Project> projects = projectRepository.findByUser(optionalUser.get());
        return projects.stream()
                .map(Project::getProjectDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectDto postProject(ProjectDto projectDto) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.extractId(token);
        Optional<User> optionalUser = userRepository.findById(userId);
        Project project = new Project();
        project.setName(projectDto.getName());
        project.setDescription(projectDto.getDescription());
        project.setCreationDate(projectDto.getCreationDate());
        project.setCompletionDate(projectDto.getCompletionDate());
        project.setStatus(projectDto.getStatus());
        project.setUser(optionalUser.get());
        Project postedProject = projectRepository.save(project);
        ProjectDto postedProjectDto = new ProjectDto();
        postedProjectDto.setId(postedProject.getId());
        return postedProjectDto;
    }

    @Override
    public void deleteProject(Long projectId) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.extractId(token);
        Optional<Project> optionalProject = projectRepository.findByUserIdAndId(userId, projectId);
        if (optionalProject.isPresent()) {
            projectRepository.deleteById(optionalProject.get().getId());
        } else {
            throw new IllegalArgumentException("Project with id: " + projectId + " not found");
        }
    }

    @Override
    public ProjectDto updateProject(Long projectId, ProjectDto projectDto) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.extractId(token);
        Optional<Project> optionalProject = projectRepository.findByUserIdAndId(userId, projectId);
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();
            project.setName(projectDto.getName());
            project.setDescription(projectDto.getDescription());
            project.setCreationDate(projectDto.getCreationDate());
            project.setCompletionDate(projectDto.getCompletionDate());
            project.setStatus(projectDto.getStatus());
            Project updatedProject = projectRepository.save(project);
            return updatedProject.getProjectDto();
        }
        return null;
    }
}
