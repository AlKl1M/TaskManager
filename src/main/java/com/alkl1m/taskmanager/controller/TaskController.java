package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.dto.project.UpdateProjectRequest;
import com.alkl1m.taskmanager.dto.task.*;
import com.alkl1m.taskmanager.service.auth.UserDetailsImpl;
import com.alkl1m.taskmanager.service.task.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/client")
public class TaskController {
    private final TaskService taskService;
    @GetMapping("/projects/{projectId}/getAllTasks")
    List<CreateBackTaskRequest> getAllTasks(
            @PathVariable Long projectId,
            @RequestParam(name = "tag", defaultValue = "") FindTasksTags request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return taskService.getAllTasks(userDetails.getId(), request, projectId);
    }

    @PostMapping("/projects/{projectId}/tasks")
    ResponseEntity<TaskDto> create(@PathVariable Long projectId,
                                   @RequestBody @Validated CreateTaskRequest request,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CreateTaskCommand cmd = CreateTaskCommand.builder()
                .id(userDetails.getId())
                .name(request.name())
                .description(request.description())
                .tags(request.tags())
                .build();
        TaskDto task = taskService.create(cmd, projectId);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/api/projects/{projectId}/tasks/{taskId}")
                .buildAndExpand(projectId, task.id()).toUri();
        return ResponseEntity.created(location).body(task);
    }
    @PutMapping("/projects/{projectId}/tasks/{id}")
    @PreAuthorize("@accessChecker.isTaskBelongToUser(principal, #id)")
    TaskDto update(@PathVariable Long projectId,
                @PathVariable Long id,
                @RequestBody @Validated UpdateTaskRequest request) {
        UpdateTaskCommand cmd = new UpdateTaskCommand(id, request.name(), request.description(), request.tags());
        return taskService.update(cmd, projectId);
    }

    @DeleteMapping("/projects/{projectId}/tasks/{id}")
    @PreAuthorize("@accessChecker.isTaskBelongToUser(principal, #id)")
    ResponseEntity<String> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.ok("Task deleted successfully");
    }
    @PutMapping("/projects/{taskId}/tasks/{id}/done")
    @PreAuthorize("@accessChecker.isTaskBelongToUser(principal, #id)")
    ResponseEntity<String> changeStatus(@PathVariable Long id) {
        taskService.changeStatus(id);
        return ResponseEntity.ok("Task status changed successfully");
    }
}
