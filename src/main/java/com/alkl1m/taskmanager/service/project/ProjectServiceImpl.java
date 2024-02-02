package com.alkl1m.taskmanager.service.project;

import com.alkl1m.taskmanager.dto.project.CreateProjectCommand;
import com.alkl1m.taskmanager.dto.project.UpdateProjectCommand;
import com.alkl1m.taskmanager.dto.project.FindProjectsQuery;
import com.alkl1m.taskmanager.dto.project.ProjectDto;
import com.alkl1m.taskmanager.dto.project.ProjectsPagedResult;
import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.enums.Status;
import com.alkl1m.taskmanager.exception.ProjectNotFoundException;
import com.alkl1m.taskmanager.exception.UnauthorizedAccessException;
import com.alkl1m.taskmanager.repository.ProjectRepository;
import com.alkl1m.taskmanager.repository.UserRepository;
import com.alkl1m.taskmanager.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final HttpServletRequest httpServletRequest;
    private final JwtUtil jwtUtil;

    @Override
    public ProjectsPagedResult<ProjectDto> findProjects(FindProjectsQuery query) {
        Long userId = getUserIdFromToken();
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
            int pageNo = query.pageNo() > 0 ? query.pageNo() - 1 : 0;
            Pageable pageable = PageRequest.of(pageNo, query.pageSize(), sort);
            Page<ProjectDto> page = projectRepository.findProjects(user.get(), pageable);
            return new ProjectsPagedResult<>(
                    page.getContent(),
                    page.getTotalElements(),
                    page.getNumber() + 1,
                    page.getTotalPages(),
                    page.isFirst(),
                    page.isLast(),
                    page.hasNext(),
                    page.hasPrevious()
            );
        } else {
            return new ProjectsPagedResult<>(Collections.emptyList(), 0, 0, 0, true, true, false, false);
        }
    }

    @Override
    @Transactional
    public ProjectDto create(CreateProjectCommand cmd) {
        Long userId = getUserIdFromToken();
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
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
    public ProjectDto update(UpdateProjectCommand cmd) {
        Long userId = getUserIdFromToken();
        Project project = projectRepository.findById(cmd.id())
                .orElseThrow(() -> ProjectNotFoundException.of(cmd.id()));
        project.setName(cmd.name());
        project.setDescription(cmd.description());
        if (project.getUser().getId().equals(userId)) {
            return ProjectDto.from(projectRepository.save(project));
        } else {
            throw new UnauthorizedAccessException();
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Long userId = getUserIdFromToken();
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
        Long userId = getUserIdFromToken();
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> ProjectNotFoundException.of(id));
        if (project.getUser().getId().equals(userId)) {
            project.setStatus(project.getStatus().equals(Status.IN_WORK) ? Status.DONE : Status.IN_WORK);
            projectRepository.save(project);
        } else {
            throw new UnauthorizedAccessException();
        }
    }

    private Long getUserIdFromToken() {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        return jwtUtil.extractId(token);
    }
}
