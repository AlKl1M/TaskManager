package com.alkl1m.taskmanager.service.task;

import com.alkl1m.taskmanager.dto.TaskDto;
import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.Task;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.repository.ProjectRepository;
import com.alkl1m.taskmanager.repository.TaskRepository;
import com.alkl1m.taskmanager.repository.UserRepository;
import com.alkl1m.taskmanager.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final HttpServletRequest httpServletRequest;
    private final JwtUtil jwtUtil;
    @Override
    public List<TaskDto> getTasksByProjectId(Long projectId) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.extractId(token);
        Optional<User> optionalUser = userRepository.findById(userId);
        List<Task> tasks = taskRepository.findByProjectIdAndUser(projectId, optionalUser.get());
        return tasks.stream()
                .map(Task::getTaskDto)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDto postTask(TaskDto taskDto, Long projectId) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.extractId(token);
        Optional<User> optionalUser = userRepository.findById(userId);
        Task task = new Task();
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();
            task.setName(taskDto.getName());
            task.setDescription(taskDto.getDescription());
            task.setCreationDate(taskDto.getCreationDate());
            task.setCompletionDate(taskDto.getCompletionDate());
            task.setStatus(taskDto.getStatus());
            task.setUser(optionalUser.get());
            task.setProject(project);
            Task postedTask = taskRepository.save(task);
            TaskDto postedTaskDto = new TaskDto();
            postedTaskDto.setId(postedTask.getId());
            return postedTaskDto;
        }
        return null;
    }

    @Override
    public void deleteTask(Long taskId) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.extractId(token);
        Optional<Task> optionalTask = taskRepository.findByUserIdAndId(userId, taskId);
        if (optionalTask.isPresent()) {
            taskRepository.deleteById(optionalTask.get().getId());
        } else {
            throw new IllegalArgumentException("Task with id: " + taskId + " not found");
        }
    }

    @Override
    public TaskDto updateTask(Long taskId, TaskDto taskDto) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.extractId(token);
        Optional<Task> optionalTask = taskRepository.findByUserIdAndId(userId, taskId);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setName(taskDto.getName());
            task.setDescription(taskDto.getDescription());
            task.setCreationDate(taskDto.getCreationDate());
            task.setCompletionDate(taskDto.getCompletionDate());
            task.setStatus(taskDto.getStatus());
            Task updatedTask = taskRepository.save(task);
            return updatedTask.getTaskDto();
        }
        return null;
    }
}
