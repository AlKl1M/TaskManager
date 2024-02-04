package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.dto.project.UpdateProjectRequest;
import com.alkl1m.taskmanager.dto.task.*;
import com.alkl1m.taskmanager.service.task.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/client")
public class TaskController {
    private final TaskService taskService;
    @GetMapping("/projects/{projectId}/getAllTasks")
    TasksPagedResult<CreateBackTaskRequest> getAllTasks(
            @PathVariable Long projectId,
            @RequestParam(name = "tag", defaultValue = "") FindTasksTags request) {
        return taskService.getAllTasks(request, projectId);
    }

    @PostMapping("/projects/{projectId}/tasks")
    ResponseEntity<TaskDto> create(@PathVariable Long projectId,
                                   @RequestBody @Validated CreateTaskRequest request) {
        CreateTaskCommand cmd = new CreateTaskCommand(request.name(), request.description(), request.tags());
        TaskDto task = taskService.create(cmd, projectId);
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
        UpdateTaskCommand cmd = new UpdateTaskCommand(id, request.name(), request.description());
        taskService.update(cmd, projectId);
    }

    @DeleteMapping("/projects/{projectId}/tasks/{id}")
    ResponseEntity<String> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.ok("Task deleted successfully");
    }
    @PutMapping("/projects/{taskId}/tasks/{id}/done")
    ResponseEntity<String> changeStatus(@PathVariable Long id) {
        taskService.changeStatus(id);
        return ResponseEntity.ok("Task status changed successfully");
    }
}
