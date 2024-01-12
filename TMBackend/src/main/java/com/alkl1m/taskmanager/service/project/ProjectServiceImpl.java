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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final HttpServletRequest httpServletRequest;
    private final JwtUtil jwtUtil;

    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository, HttpServletRequest httpServletRequest, JwtUtil jwtUtil) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.httpServletRequest = httpServletRequest;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public ProjectsPagedResult<ProjectDto> findProjects(FindProjectsQuery query) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.extractId(token);
        Optional<User> user = userRepository.findById(userId);
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
    }

    @Override
    @Transactional
    public ProjectDto create(CreateProjectCommand cmd) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.extractId(token);
        Optional<User> user = userRepository.findById(userId);
        Project project = new Project();
        project.setName(cmd.name());
        project.setDescription(cmd.description());
        project.setCreatedAt(Instant.now());
        project.setStatus(Status.IN_WORK);
        project.setUser(user.get());
        return ProjectDto.from(projectRepository.save(project));
    }

    @Override
    @Transactional
    public void update(UpdateProjectCommand cmd) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.extractId(token);
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
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.extractId(token);
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> ProjectNotFoundException.of(id));
        if (project.getUser().getId().equals(userId)) {
            projectRepository.delete(project);
        } else {
            throw new UnauthorizedAccessException();
        }
    }
}
