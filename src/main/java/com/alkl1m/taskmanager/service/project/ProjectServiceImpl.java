package com.alkl1m.taskmanager.service.project;

import com.alkl1m.taskmanager.controller.payload.project.CreateProjectCommand;
import com.alkl1m.taskmanager.controller.payload.project.UpdateProjectCommand;
import com.alkl1m.taskmanager.controller.payload.project.ProjectDto;
import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.enums.Status;
import com.alkl1m.taskmanager.exception.ProjectNotFoundException;
import com.alkl1m.taskmanager.repository.ProjectRepository;
import com.alkl1m.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Override
    public List<ProjectDto> getAllProjects(Long userId) {
        log.info("Getting all projects for user with ID: {}", userId);
        List<Project> projects = projectRepository.findAllByUserId(userId);
        return projects.stream()
                .map(ProjectDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDto> getAllProjectsByQuery(Long userId, String query) {
        log.info("Getting all projects for user with ID: {} and query: {}", userId, query);
        List<Project> projects = projectRepository.findByQueryAndUserId(query, userId);
        return projects.stream()
                .map(ProjectDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProjectDto create(CreateProjectCommand cmd) {
        log.info("Creating a new project: {}", cmd.id());
        Long userId = cmd.id();
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            int projectCount = userRepository.countUserProjects(userId);
            if (projectCount >= 20) {
                log.warn("User with id {} has reached the maximum number of projects and trying to create more", userId);
                throw new IllegalStateException("User has reached the maximum number of projects.");
            }
            Project project = Project.builder()
                    .name(cmd.name())
                    .description(cmd.description())
                    .createdAt(Instant.now())
                    .status(Status.IN_WORK)
                    .user(user.get())
                    .build();
            log.info("New project created");
            return ProjectDto.from(projectRepository.save(project));
        } else {
            log.warn("User not found for id: {}", cmd.id());
            return null;
        }
    }

    @Override
    @Transactional
    public void update(UpdateProjectCommand cmd) {
        log.info("Updating project with ID: {}", cmd.id());
        Project project = projectRepository.findById(cmd.id())
                .orElseThrow(() -> ProjectNotFoundException.of(cmd.id()));
        project.setName(cmd.name());
        project.setDescription(cmd.description());
        log.info("Updated project with ID: {}", cmd.id());
        ProjectDto.from(projectRepository.save(project));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting project with ID: {}", id);
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> ProjectNotFoundException.of(id));
        projectRepository.delete(project);
        log.info("Deleted project with ID: {}", id);
    }

    @Override
    @Transactional
    public void changeStatus(Long id) {
        log.info("Changing status for project with ID: {}", id);
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> ProjectNotFoundException.of(id));
        if (project.getStatus().equals(Status.IN_WORK)) {
            project.setStatus(Status.DONE);
            project.setDoneAt(Instant.now());
        } else {
            project.setStatus(Status.IN_WORK);
            project.setDoneAt(null);
        }
        projectRepository.save(project);
        log.info("Changed status for project with ID: {}", id);
    }
}
