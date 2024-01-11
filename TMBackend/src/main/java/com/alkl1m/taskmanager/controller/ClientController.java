package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.dto.*;
import com.alkl1m.taskmanager.service.project.ProjectService;
import com.alkl1m.taskmanager.service.task.TaskService;
import com.alkl1m.taskmanager.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {
    private final ProjectService projectService;
    private final TaskService taskService;

    @GetMapping("/projects")
    ProjectsPagedResult<ProjectDto> findProjects(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "size", defaultValue = "10") Integer pageSize) {
        FindProjectsQuery query = new FindProjectsQuery(pageNo, pageSize);
        return projectService.findProjects(query);
    }

    @PostMapping("/projects")
    ResponseEntity<ProjectDto> create(@RequestBody @Validated CreateProjectRequest request) {
        CreateProjectCommand cmd = new CreateProjectCommand(request.name(), request.description());
        ProjectDto project = projectService.create(cmd);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/api/projects/{id}")
                .buildAndExpand(project.id()).toUri();
        return ResponseEntity.created(location).body(project);
    }

    @PutMapping("/projects/{id}")
    void update(@PathVariable(name = "id") Long id,
                @RequestBody @Validated UpdateProjectRequest request) {
        UpdateProjectCommand cmd = new UpdateProjectCommand(id, request.name(), request.description());
        projectService.update(cmd);
    }

    @DeleteMapping("/projects/{id}")
    void delete(@PathVariable(name = "id") Long id) {
        projectService.delete(id);
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskDto>> getAllTasks(@PathVariable Long projectId) {
        List<TaskDto> taskDtoList = taskService.getTasksByProjectId(projectId);
        if (taskDtoList == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(taskDtoList);
    }

    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<?> postTask(@PathVariable Long projectId, @RequestBody TaskDto taskDto) {
        TaskDto createdTaskDto = taskService.postTask(taskDto, projectId);
        if (createdTaskDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");
        }
        return ResponseEntity.ok(taskDto);
    }

    @DeleteMapping("/projects/{projectId}/tasks")
    public ResponseEntity<Void> deleteTask(@RequestParam("taskId") Long taskId, @PathVariable String projectId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/projects/{projectId}/tasks")
    public ResponseEntity<?> updateTask(@RequestParam("taskId") Long taskId,
                                        @RequestBody TaskDto taskDto, @PathVariable String projectId) {
        TaskDto updatedTaskDto = taskService.updateTask(taskId, taskDto);
        if (updatedTaskDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");
        }
        return ResponseEntity.status(HttpStatus.OK).body(updatedTaskDto);
    }
}
