package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.dto.project.UpdateProjectRequest;
import com.alkl1m.taskmanager.dto.task.*;
import com.alkl1m.taskmanager.service.task.TaskService;
import com.alkl1m.taskmanager.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/client")
public class TaskController {
    private final TaskService taskService;
    private final HttpServletRequest httpServletRequest;
    private final JwtUtil jwtUtil;

    public TaskController(TaskService taskService, HttpServletRequest httpServletRequest, JwtUtil jwtUtil) {
        this.taskService = taskService;
        this.httpServletRequest = httpServletRequest;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/projects/{projectId}/tasks")
    TasksPagedResult<TaskDto> findTasks(
            @PathVariable Long projectId,
            @RequestParam(name = "page", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "size", defaultValue = "10") Integer pageSize) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.extractId(token);
        FindTasksQuery query = new FindTasksQuery(pageNo, pageSize);
        return taskService.findTasks(query, projectId, userId);
    }

    @PostMapping("/projects/{projectId}/tasks")
    ResponseEntity<TaskDto> create(@PathVariable Long projectId,
                                   @RequestBody @Validated CreateTaskRequest request) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.extractId(token);
        CreateTaskCommand cmd = new CreateTaskCommand(request.name(), request.description());
        TaskDto task = taskService.create(cmd, projectId, userId);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/api/projects/{projectId}/tasks/{taskId}")
                .buildAndExpand(projectId, task.id()).toUri();
        return ResponseEntity.created(location).body(task);
    }

    @PutMapping("/projects/{projectId}/tasks/{id}")
    void update(@PathVariable Long projectId,
                @PathVariable Long id,
                @RequestBody @Validated UpdateProjectRequest request) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.extractId(token);
        UpdateTaskCommand cmd = new UpdateTaskCommand(id, request.name(), request.description());
        taskService.update(cmd, projectId, userId);
    }

    @DeleteMapping("/projects/{projectId}/tasks/{id}")
    void delete(@PathVariable Long projectId,
                @PathVariable Long id) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.extractId(token);
        taskService.delete(id, userId);
    }

    @PutMapping("/projects/{taskId}/tasks/{id}/done")
    void changeStatus(@PathVariable Long projectId,
                      @PathVariable Long id) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.extractId(token);
        taskService.changeStatus(id, userId);
    }
}
