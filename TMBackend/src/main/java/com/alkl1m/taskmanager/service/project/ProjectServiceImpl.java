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
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ProjectsPagedResult<ProjectDto> findProjects(FindProjectsQuery query, Long userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        int pageNo = query.pageNo() > 0 ? query.pageNo() - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, query.pageSize(), sort);
        Optional<Page<ProjectDto>> optionalPage = projectRepository.findProjects(userId, pageable);
        if (optionalPage.isPresent()) {
            Page<ProjectDto> page = optionalPage.get();
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
        return new ProjectsPagedResult<>(Collections.emptyList(), 0, 0, 0, true, true, false, false);
    }

    @Override
    @Transactional
    public ProjectDto create(CreateProjectCommand cmd, Long userId) {
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
    public void update(UpdateProjectCommand cmd, Long userId) {
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
    public void delete(Long id, Long userId) {
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
    public void changeStatus(Long id, Long userId) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> ProjectNotFoundException.of(id));
        if (project.getUser().getId().equals(userId)) {
            if (project.getStatus().equals(Status.IN_WORK)){
                project.setStatus(Status.DONE);
                project.setDoneAt(Instant.now());
            } else {
                project.setStatus(Status.IN_WORK);
                project.setDoneAt(null);
            }
            projectRepository.save(project);
        } else {
            throw new UnauthorizedAccessException();
        }
    }

    @Override
    public Project getProjectById(Long id, Long userId) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        return optionalProject.orElse(null);
    }
}
