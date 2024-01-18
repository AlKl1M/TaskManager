package com.alkl1m.taskmanager.service.task;

import com.alkl1m.taskmanager.dto.task.CreateTaskCommand;
import com.alkl1m.taskmanager.dto.task.UpdateTaskCommand;
import com.alkl1m.taskmanager.dto.task.FindTasksQuery;
import com.alkl1m.taskmanager.dto.task.TaskDto;
import com.alkl1m.taskmanager.dto.task.TasksPagedResult;
import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.Task;
import com.alkl1m.taskmanager.enums.Status;
import com.alkl1m.taskmanager.exception.ProjectNotFoundException;
import com.alkl1m.taskmanager.exception.TaskNotFoundException;
import com.alkl1m.taskmanager.exception.UnauthorizedAccessException;
import com.alkl1m.taskmanager.repository.ProjectRepository;
import com.alkl1m.taskmanager.repository.TaskRepository;
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
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskServiceImpl(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public TasksPagedResult<TaskDto> findTasks(FindTasksQuery query, Long projectId, Long userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        int pageNo = query.pageNo() > 0 ? query.pageNo() - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, query.pageSize(), sort);
        Optional<Page<TaskDto>> page = taskRepository.findTasks(userId, projectId, pageable);
        if (page.isPresent()) {
            return new TasksPagedResult<>(
                    page.get().getContent(),
                    page.get().getTotalElements(),
                    page.get().getNumber() + 1,
                    page.get().getTotalPages(),
                    page.get().isFirst(),
                    page.get().isLast(),
                    page.get().hasNext(),
                    page.get().hasPrevious()
            );
        }
        return new TasksPagedResult<>(Collections.emptyList(), 0, 0, 0, true, true, false, false);
    }

    @Override
    @Transactional
    public TaskDto create(CreateTaskCommand cmd, Long projectId, Long userId) {
        Optional<Project> project = projectRepository.findByIdAndUserId(projectId, userId);
        if (project.isPresent()) {
            Task task = new Task();
            task.setName(cmd.name());
            task.setDescription(cmd.description());
            task.setCreatedAt(Instant.now());
            task.setStatus(Status.IN_WORK);
            task.setUser(project.get().getUser());
            task.setProject(project.get());
            return TaskDto.from(taskRepository.save(task));
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public void update(UpdateTaskCommand cmd, Long projectId, Long userId) {
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
    public void delete(Long id, Long userId) {
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
    public void changeStatus(Long id, Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> ProjectNotFoundException.of(id));
        if (task.getUser().getId().equals(userId)) {
            if (task.getStatus().equals(Status.IN_WORK)) {
                task.setStatus(Status.DONE);
                task.setDoneAt(Instant.now());
            } else {
                task.setStatus(Status.IN_WORK);
                task.setDoneAt(null);
            }
            taskRepository.save(task);
        } else {
            throw new UnauthorizedAccessException();
        }
    }
}
