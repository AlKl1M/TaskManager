package com.alkl1m.taskmanager.service.project;

import com.alkl1m.taskmanager.dto.project.CreateProjectCommand;
import com.alkl1m.taskmanager.dto.project.UpdateProjectCommand;
import com.alkl1m.taskmanager.dto.project.ProjectDto;
import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.enums.Status;
import com.alkl1m.taskmanager.exception.ProjectNotFoundException;
import com.alkl1m.taskmanager.exception.TaskNotFoundException;
import com.alkl1m.taskmanager.repository.ProjectRepository;
import com.alkl1m.taskmanager.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Override
    public List<ProjectDto> getAllProjects(Long userId) {
        List<Project> projects = projectRepository.findAllByUserId(userId);
        return projects.stream()
                .map(ProjectDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProjectDto create(CreateProjectCommand cmd) {
        Long userId = cmd.id();
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            int projectCount = userRepository.countUserProjects(userId);
            if (projectCount >= 20) {
                throw new IllegalStateException("User has reached the maximum number of projects.");
            }
            Project project = Project.builder()
                    .name(cmd.name())
                    .description(cmd.description())
                    .createdAt(Instant.now())
                    .status(Status.IN_WORK)
                    .user(user.get())
                    .build();
            return ProjectDto.from(projectRepository.save(project));
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public ProjectDto update(UpdateProjectCommand cmd) {
        Project project = projectRepository.findById(cmd.id())
                .orElseThrow(() -> ProjectNotFoundException.of(cmd.id()));
        project.setName(cmd.name());
        project.setDescription(cmd.description());
        return ProjectDto.from(projectRepository.save(project));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> ProjectNotFoundException.of(id));
        projectRepository.delete(project);
    }

    @Override
    @Transactional
    public void changeStatus(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> ProjectNotFoundException.of(id));
        project.setStatus(project.getStatus().equals(Status.IN_WORK) ? Status.DONE : Status.IN_WORK);
        projectRepository.save(project);
    }

}
