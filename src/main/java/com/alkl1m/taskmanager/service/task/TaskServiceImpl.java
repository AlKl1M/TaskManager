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

    @Override
    public List<CreateBackTaskRequest> getAllTasks(Long userId, FindTasksTags tag, Long projectId){
        Optional<User> user = userRepository.findById(userId);
        Optional<Project> project = projectRepository.findById(projectId);
        if (user.isPresent() && project.isPresent()) {
            return getCreateBackTaskRequests(tag,
                    taskRepository.getAllTasks(user.get(), project.get()));
        } else {
            return Collections.emptyList();
        }
    }


    @Override
    @Transactional
    public TaskDto create(CreateTaskCommand cmd, Long projectId) {
        if (IsValidTags(cmd.tags())) {
            Optional<User> user = userRepository.findById(cmd.id());
            Optional<Project> project = projectRepository.findById(projectId);
            if (user.isPresent() && project.isPresent()) {
                Task task = new Task();
                task.setName(cmd.name());
                task.setDescription(cmd.description());
                task.setCreatedAt(Instant.now());
                task.setStatus(Status.IN_WORK);
                task.setUser(user.get());
                task.setProject(project.get());
                task.setTags(String.join("&#/!&", cmd.tags()));
                return TaskDto.from(taskRepository.save(task));
            } else {
                return null;
            }
        }else {
            throw new IllegalStateException("ERROR: Tag size should be > 2 and < 20, maxTags = 3");
        }
    }

    @Override
    @Transactional
    public TaskDto update(UpdateTaskCommand cmd, Long projectId) {
        Task task = taskRepository.findById(cmd.id())
                .orElseThrow(() -> TaskNotFoundException.of(cmd.id()));
        task.setName(cmd.name());
        task.setDescription(cmd.description());
        task.setTags(String.join("&#/!&", cmd.tags()));
        return TaskDto.from(taskRepository.save(task));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> TaskNotFoundException.of(id));
        taskRepository.delete(task);
    }

    @Override
    @Transactional
    public void changeStatus(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> ProjectNotFoundException.of(id));
        task.setStatus(task.getStatus().equals(Status.IN_WORK) ? Status.DONE : Status.IN_WORK);
        taskRepository.save(task);
    }


    private static List<CreateBackTaskRequest> getCreateBackTaskRequests(FindTasksTags tag, List<TaskDto> page) {
        List<CreateBackTaskRequest> sortNewData = new ArrayList<>(page.size());
        if (Objects.equals(tag.tag(), "")) {
            for (TaskDto request : page)
                sortNewData.add(CreateBackTaskRequest.from(request));
        }
        else {
            for (TaskDto request: page)
                if (Arrays.asList(request.tags().split("&#/!&")).contains(tag.tag()))
                    sortNewData.add(CreateBackTaskRequest.from(request));
        }
        return sortNewData;
    }

    private boolean IsValidTags(List<String> tags) {
        if (tags.size() > 3) return false;
        for (String tag: tags)
            if (tag.length() > 20 || tag.length() < 2 || tag.contains("&#/!&"))
                return false;
        return true;
    }
}
