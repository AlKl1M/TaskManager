package com.alkl1m.taskmanager.service.task;

import com.alkl1m.taskmanager.dto.task.CreateTaskCommand;
import com.alkl1m.taskmanager.dto.task.UpdateTaskCommand;
import com.alkl1m.taskmanager.dto.task.FindTasksQuery;
import com.alkl1m.taskmanager.dto.task.TaskDto;
import com.alkl1m.taskmanager.dto.task.TasksPagedResult;
import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.Task;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.enums.Status;
import com.alkl1m.taskmanager.exception.TaskNotFoundException;
import com.alkl1m.taskmanager.exception.UnauthorizedAccessException;
import com.alkl1m.taskmanager.repository.ProjectRepository;
import com.alkl1m.taskmanager.repository.TaskRepository;
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
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final HttpServletRequest httpServletRequest;
    private final JwtUtil jwtUtil;

    public TaskServiceImpl(TaskRepository taskRepository, ProjectRepository projectRepository, UserRepository userRepository, HttpServletRequest httpServletRequest, JwtUtil jwtUtil) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.httpServletRequest = httpServletRequest;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public TasksPagedResult<TaskDto> findTasks(FindTasksQuery query, Long projectId) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.extractId(token);
        Optional<User> user = userRepository.findById(userId);
        Optional<Project> project = projectRepository.findById(projectId);
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        int pageNo = query.pageNo() > 0 ? query.pageNo() - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, query.pageSize(), sort);
        Page<TaskDto> page = taskRepository.findTasks(user.get(), project.get(), pageable);
        return new TasksPagedResult<>(
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
    public TaskDto create(CreateTaskCommand cmd, Long projectId) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.extractId(token);
        Optional<User> user = userRepository.findById(userId);
        Optional<Project> project = projectRepository.findById(projectId);
        Task task = new Task();
        task.setName(cmd.name());
        task.setDescription(cmd.description());
        task.setCreatedAt(Instant.now());
        task.setStatus(Status.IN_WORK);
        task.setUser(user.get());
        task.setProject(project.get());
        return TaskDto.from(taskRepository.save(task));
    }

    @Override
    @Transactional
    public void update(UpdateTaskCommand cmd, Long projectId) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.extractId(token);
        Task task = taskRepository.findById(cmd.id())
                .orElseThrow(() -> TaskNotFoundException.of(cmd.id()));
        task.setName(cmd.name());
        task.setDescription(cmd.description());
        if (task.getUser().getId().equals(userId)) {
            taskRepository.save(task);
        } else {
            throw new UnauthorizedAccessException();
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.extractId(token);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> TaskNotFoundException.of(id));
        if (task.getUser().getId().equals(userId)) {
            taskRepository.delete(task);
        } else {
            throw new UnauthorizedAccessException();
        }
    }
}
