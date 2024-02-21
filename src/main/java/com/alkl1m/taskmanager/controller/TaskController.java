package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.dto.auth.MessageResponse;
import com.alkl1m.taskmanager.dto.task.*;
import com.alkl1m.taskmanager.service.auth.UserDetailsImpl;
import com.alkl1m.taskmanager.service.task.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class TaskController {
    private final TaskService taskService;
    @GetMapping("/projects/{projectId}/getAllTasksBySearchWord")
    List<CreateBackTaskDto> getAllTasksBySearchWord(
            @PathVariable Long projectId,
            @RequestParam(required = false) String searchWord,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return taskService.getAllTasksBySearchWord(userDetails.getId(), projectId, searchWord);
    }
    @GetMapping("/projects/{projectId}/getAllTasksByTag")
    List<CreateBackTaskDto> getAllTasksByTag(
            @PathVariable Long projectId,
            @RequestParam(required = false) String tag,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return taskService.getAllTasksByTag(userDetails.getId(), projectId, tag);
    }

    @PostMapping("/projects/{projectId}/tasks")
    ResponseEntity<MessageResponse> create(@PathVariable Long projectId,
                                   @RequestBody @Validated CreateTaskRequest request,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CreateTaskCommand cmd = CreateTaskCommand.builder()
                .id(userDetails.getId())
                .name(request.name())
                .description(request.description())
                .tags(request.tags())
                .deadline(request.deadline())
                .build();
        TaskDto task = taskService.create(cmd, projectId);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/api/projects/{projectId}/tasks/{taskId}")
                .buildAndExpand(projectId, task.id()).toUri();
        return ResponseEntity.created(location).body(new MessageResponse("Task created successfully"));
    }
    @PutMapping("/projects/{projectId}/tasks/{id}")
    @PreAuthorize("@accessChecker.isTaskBelongToUser(principal, #id)")
    TaskDto update(@PathVariable Long id,
                @RequestBody @Validated UpdateTaskRequest request) {
        UpdateTaskCommand cmd = new UpdateTaskCommand(id, request.name(), request.description(), request.deadline(), request.tags());
        log.info("UpdateTaskCommand has been created!");
        return taskService.update(cmd);
    }

    @DeleteMapping("/projects/{projectId}/tasks/{id}")
    @PreAuthorize("@accessChecker.isTaskBelongToUser(principal, #id)")
    ResponseEntity<String> delete(@PathVariable Long id) {
        taskService.delete(id);
        log.info("Task deleted successfully");
        return ResponseEntity.ok("Task deleted successfully");
    }
    @PutMapping("/projects/{taskId}/tasks/{id}/done")
    @PreAuthorize("@accessChecker.isTaskBelongToUser(principal, #id)")
    ResponseEntity<String> changeStatus(@PathVariable Long id) {
        taskService.changeStatus(id);
        log.info("Task status changed successfully");
        return ResponseEntity.ok("Task status changed successfully");
    }
}
