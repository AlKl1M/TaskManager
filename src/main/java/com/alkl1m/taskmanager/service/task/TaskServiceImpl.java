package com.alkl1m.taskmanager.service.task;

import com.alkl1m.taskmanager.dto.task.*;
import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.Task;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.enums.Status;
import com.alkl1m.taskmanager.exception.ProjectNotFoundException;
import com.alkl1m.taskmanager.exception.TaskNotFoundException;
import com.alkl1m.taskmanager.exception.UnauthorizedAccessException;
import com.alkl1m.taskmanager.repository.ProjectRepository;
import com.alkl1m.taskmanager.repository.TaskRepository;
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
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final HttpServletRequest httpServletRequest;
    private final JwtUtil jwtUtil;

    @Override
    public TasksPagedResult<CreateBackTaskRequest> findTasks(FindTasksQuery query, Long projectId) {
        Long userId = getUserIdFromToken();
        Optional<User> user = userRepository.findById(userId);
        Optional<Project> project = projectRepository.findById(projectId);
        if (user.isPresent() && project.isPresent()) {
            Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
            int pageNo = query.pageNo() > 0 ? query.pageNo() - 1 : 0;
            Pageable pageable = PageRequest.of(pageNo, query.pageSize(), sort);
            Page<TaskDto> page = taskRepository.findTasks(user.get(), project.get(), pageable);
            ArrayList<CreateBackTaskRequest> newData = new ArrayList<>(query.pageSize());
            for(TaskDto req: page.getContent()){
                newData.add(CreateBackTaskRequest.from(req));
            }
            return new TasksPagedResult<>(
                    newData,
                    page.getTotalElements(),
                    page.getNumber() + 1,
                    page.getTotalPages(),
                    page.isFirst(),
                    page.isLast(),
                    page.hasNext(),
                    page.hasPrevious()
            );
        } else {
            return new TasksPagedResult<>(Collections.emptyList(), 0, 0, 0,
                    true, true, false, false);
        }
    }

    @Override
    @Transactional
    public TaskDto create(CreateTaskCommand cmd, Long projectId) {
        Long userId = getUserIdFromToken();
        Optional<User> user = userRepository.findById(userId);
        Optional<Project> project = projectRepository.findById(projectId);
        if (user.isPresent() && project.isPresent()) {
            Task task = new Task();
            task.setName(cmd.name());
            task.setDescription(cmd.description());
            task.setCreatedAt(Instant.now());
            task.setStatus(Status.IN_WORK);
            task.setUser(user.get());
            task.setProject(project.get());
            task.setTags(String.join("$", cmd.tags()));
            return TaskDto.from(taskRepository.save(task));
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public TaskDto update(UpdateTaskCommand cmd, Long projectId) {
        Long userId = getUserIdFromToken();
        Task task = taskRepository.findById(cmd.id())
                .orElseThrow(() -> TaskNotFoundException.of(cmd.id()));
        task.setName(cmd.name());
        task.setDescription(cmd.description());
        if (task.getUser().getId().equals(userId)) {
            return TaskDto.from(taskRepository.save(task));
        } else {
            throw new UnauthorizedAccessException();
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Long userId = getUserIdFromToken();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> TaskNotFoundException.of(id));
        if (task.getUser().getId().equals(userId)) {
            taskRepository.delete(task);
        } else {
            throw new UnauthorizedAccessException();
        }
    }

    @Override
    @Transactional
    public void changeStatus(Long id) {
        Long userId = getUserIdFromToken();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> ProjectNotFoundException.of(id));
        if (task.getUser().getId().equals(userId)) {
            task.setStatus(task.getStatus().equals(Status.IN_WORK) ? Status.DONE : Status.IN_WORK);
            taskRepository.save(task);
        } else {
            throw new UnauthorizedAccessException();
        }
    }

    private Long getUserIdFromToken() {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        return jwtUtil.extractId(token);
    }
}
