package com.alkl1m.taskmanager.service.project;

import com.alkl1m.taskmanager.dto.project.CreateProjectCommand;
import com.alkl1m.taskmanager.dto.project.UpdateProjectCommand;
import com.alkl1m.taskmanager.dto.project.ProjectDto;
import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.enums.Status;
import com.alkl1m.taskmanager.exception.ProjectNotFoundException;
import com.alkl1m.taskmanager.exception.UnauthorizedAccessException;
import com.alkl1m.taskmanager.repository.ProjectRepository;
import com.alkl1m.taskmanager.repository.UserRepository;
import com.alkl1m.taskmanager.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
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
    private final HttpServletRequest httpServletRequest;
    private final JwtUtil jwtUtil;

    @Override
    public List<ProjectDto> getAllProjects() {
        Long userId = getCurrentUserId();
        List<Project> projects = projectRepository.findAllByUserId(userId);
        return projects.stream()
                .map(ProjectDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProjectDto create(CreateProjectCommand cmd) {
        Long userId = getCurrentUserId();
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            int projectCount = userRepository.countUserProjects(userId);
            if (projectCount >= 20) {
                throw new IllegalStateException("User has reached the maximum number of projects.");
            }
            Project project = new Project();
            project.setName(cmd.name());
            project.setDescription(cmd.description());
            project.setCreatedAt(Instant.now());
            project.setStatus(Status.IN_WORK);
            project.setUser(user.get());
            return ProjectDto.from(projectRepository.save(project));
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public void update(UpdateProjectCommand cmd) {
        Long userId = getCurrentUserId();
        Project project = projectRepository.findById(cmd.id())
                .orElseThrow(() -> ProjectNotFoundException.of(cmd.id()));
        project.setName(cmd.name());
        project.setDescription(cmd.description());
        if (project.getUser().getId().equals(userId)) {
            projectRepository.save(project);
        } else {
            throw new UnauthorizedAccessException();
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Long userId = getCurrentUserId();
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> ProjectNotFoundException.of(id));
        if (project.getUser().getId().equals(userId)) {
            projectRepository.delete(project);
        } else {
            throw new UnauthorizedAccessException();
        }
    }

    @Override
    @Transactional
    public void changeStatus(Long id) {
        Long userId = getCurrentUserId();
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> ProjectNotFoundException.of(id));
        if (project.getUser().getId().equals(userId)) {
            project.setStatus(project.getStatus().equals(Status.IN_WORK) ? Status.DONE : Status.IN_WORK);
            projectRepository.save(project);
        } else {
            throw new UnauthorizedAccessException();
        }
    }

    @Override
    public List<ProjectDto> search(String query) {
        Long userId = getCurrentUserId();
        List<Project> projects = projectRepository.findByQueryAndUserId(query, userId);
        return projects.stream()
                .map(ProjectDto::from)
                .collect(Collectors.toList());
    }

    public Long getCurrentUserId() {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        return jwtUtil.extractId(token);
    }
}
