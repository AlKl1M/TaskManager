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
    public TasksPagedResult<CreateBackTaskRequest> getAllTasks(FindTasksTags tag, Long projectId){
        Long userId = getUserIdFromToken();
        Optional<User> user = userRepository.findById(userId);
        Optional<Project> project = projectRepository.findById(projectId);
        if (user.isPresent() && project.isPresent()) {
            ArrayList<TaskDto> page = taskRepository.getAllTasks(user.get(), project.get());
            ArrayList<CreateBackTaskRequest> newData = new ArrayList<>(page.size());
            for(TaskDto req: page){
                newData.add(CreateBackTaskRequest.from(req));
            }
            ArrayList<CreateBackTaskRequest> sortNewData = getCreateBackTaskRequests(tag, newData, page);
            return new TasksPagedResult<>(sortNewData);
        } else {
            return new TasksPagedResult<>(Collections.emptyList());
        }
    }

    private static ArrayList<CreateBackTaskRequest> getCreateBackTaskRequests(FindTasksTags tag, ArrayList<CreateBackTaskRequest> newData, ArrayList<TaskDto> page) {
        ArrayList<CreateBackTaskRequest> sortNewData = new ArrayList<>(newData.size());
        if (Objects.equals(tag.tag(), "")){
            for(int i = 0; i < page.size(); i++){
                sortNewData.add(newData.get(i));
            }
        }
        else {
            for (CreateBackTaskRequest request: newData){
                if (Arrays.asList(request.tags()).contains(tag.tag())) {
                    sortNewData.add(request);
                }
            }
        }
        return sortNewData;
    }

    @Override
    @Transactional
    public TaskDto create(CreateTaskCommand cmd, Long projectId) {
        if (IsValidTags(cmd.tags())) {
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
                task.setTags(String.join("&#/!&", cmd.tags()));
                return TaskDto.from(taskRepository.save(task));
            } else {
                return null;
            }
        }else {
            throw new IllegalStateException("ERROR: Tag size should be > 2 and < 20, maxTags = 3");
        }
    }
    private boolean IsValidTags(ArrayList<String> tags) {
        if (tags.size() > 3) return false;
        for (String tag: tags) if (tag.length() > 20 || tag.length() < 2 || tag.contains("&#/!&")) return false;
        return true;
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
