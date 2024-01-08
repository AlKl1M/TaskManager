package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.dto.ProjectDto;
import com.alkl1m.taskmanager.dto.TaskDto;
import com.alkl1m.taskmanager.service.project.ProjectService;
import com.alkl1m.taskmanager.service.task.TaskService;
import com.alkl1m.taskmanager.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {
    private final ProjectService projectService;
    private final TaskService taskService;
    private final HttpServletRequest httpServletRequest;
    private final JwtUtil jwtUtil;

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        List<ProjectDto> projectDtoList = projectService.getProjectsByUserId();
        if (projectDtoList == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(projectDtoList);
    }

    @PostMapping("/projects")
    public ResponseEntity<?> postProject(@RequestBody ProjectDto projectDto) {
        ProjectDto cratedProjectDto = projectService.postProject(projectDto);
        if (cratedProjectDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");
        }
        return ResponseEntity.ok(projectDto);
    }

    @DeleteMapping("/projects")
    public ResponseEntity<Void> deleteProject(@RequestParam("projectId") Long projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/projects")
    public ResponseEntity<?> updateProject(@RequestParam("projectId") Long projectId,
                                           @RequestBody ProjectDto projectDto) {
        ProjectDto updatedProjectDto = projectService.updateProject(projectId, projectDto);
        if (updatedProjectDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");
        }
        return ResponseEntity.status(HttpStatus.OK).body(updatedProjectDto);
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
