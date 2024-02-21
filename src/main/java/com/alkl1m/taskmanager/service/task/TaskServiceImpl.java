package com.alkl1m.taskmanager.service.task;

import com.alkl1m.taskmanager.dto.task.*;
import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.Task;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.enums.Status;
import com.alkl1m.taskmanager.exception.ProjectNotFoundException;
import com.alkl1m.taskmanager.exception.TaskNotFoundException;
import com.alkl1m.taskmanager.repository.ProjectRepository;
import com.alkl1m.taskmanager.repository.TaskRepository;
import com.alkl1m.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Override
    public List<CreateBackTaskDto> getAllTasksBySearchWord(Long userId, Long projectId, String searchWord) {
        log.info("Getting all tasks for user with ID: {}", userId);
        Optional<User> user = userRepository.findById(userId);
        Optional<Project> project = projectRepository.findById(projectId);
        if (Objects.equals(searchWord, null)){
            return project
                    .map(value -> getCreateBackTaskDto(taskRepository.getAllTasks(user.get().getId(), value)))
                    .orElse(Collections.emptyList());
        }
        else {
            return project
                    .map(value -> getCreateBackTaskDto(taskRepository.getAllTasksBySearchWord(user.get().getId(), value, searchWord)))
                    .orElse(Collections.emptyList());
        }
    }

    @Override
    public List<CreateBackTaskDto> getAllTasksByTag(Long userId, Long projectId, String tag) {
        log.info("Getting all tasks for user with ID: {}", userId);
        Optional<User> user = userRepository.findById(userId);
        Optional<Project> project = projectRepository.findById(projectId);
        if (Objects.equals(tag, null)){
            return project
                    .map(value -> getCreateBackTaskDto(taskRepository.getAllTasks(user.get().getId(), value)))
                    .orElse(Collections.emptyList());
        }
        else {
            return project
                    .map(value -> getCreateBackTaskDto(taskRepository.getAllTasksByTag(user.get().getId(), value, tag)))
                    .orElse(Collections.emptyList());
        }
    }

    @Override
    @Transactional
    public TaskDto create(CreateTaskCommand cmd, Long projectId) {
        log.info("Creating a new task: {}", cmd.id());
        if (IsValidTags(cmd.tags())) {
            Optional<User> user = userRepository.findById(cmd.id());
            Optional<Project> project = projectRepository.findById(projectId);
            if (project.isPresent()) {
                Task task = Task.builder()
                        .name(cmd.name())
                        .description(cmd.description())
                        .createdAt(Instant.now())
                        .deadline(cmd.deadline())
                        .status(Status.IN_WORK)
                        .user(user.get())
                        .project(project.get())
                        .tags(String.join("&#/!&", cmd.tags()))
                        .build();
                log.info("Task created: {}", task.getName());
                return TaskDto.from(taskRepository.save(task));
            } else {
                log.warn("Project not found for create task");
                return null;
            }
        }else {
            log.warn("Tags size should be > 2 and < 20, maxTags = 3, now {}", cmd.tags().size());
            throw new IllegalStateException("Tags size should be > 2 and < 20, maxTags = 3");
        }
    }

    @Override
    @Transactional
    public TaskDto update(UpdateTaskCommand cmd) {
        log.info("Updating task with ID: {}", cmd.id());
        Task task = taskRepository.findById(cmd.id())
                .orElseThrow(() -> TaskNotFoundException.of(cmd.id()));
        task.setName(cmd.name());
        task.setDescription(cmd.description());
        task.setDeadline(cmd.deadline());
        task.setTags(String.join("&#/!&", cmd.tags()));
        log.info("Updated task with ID: {}", cmd.id());
        return TaskDto.from(taskRepository.save(task));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting task with ID: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> TaskNotFoundException.of(id));
        taskRepository.delete(task);
        log.info("Deleted task with ID: {}", id);
    }

    @Override
    @Transactional
    public void changeStatus(Long id) {
        log.info("Changing status for task with ID: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> ProjectNotFoundException.of(id));
        if (task.getStatus().equals(Status.IN_WORK)) {
            task.setStatus(Status.DONE);
            task.setDoneAt(Instant.now());
        } else {
            task.setStatus(Status.IN_WORK);
            task.setDoneAt(null);
        }
        taskRepository.save(task);
        log.info("Changed status for project with ID: {}", id);
    }


    private static List<CreateBackTaskDto> getCreateBackTaskDto(List<TaskDto> tasks) {
        List<CreateBackTaskDto> sortNewData = new ArrayList<>(tasks.size());
        for (TaskDto request : tasks) sortNewData.add(CreateBackTaskDto.from(request));
        return sortNewData;
    }

    private boolean IsValidTags(List<String> tags) {
        if (tags.size() > 3)
            return false;
        for (String tag: tags)
            if (tag.length() > 20 || tag.length() < 2 || tag.contains("&#/!&"))
                return false;
        return true;
    }
}
