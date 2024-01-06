package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.dto.ProjectDto;
import com.alkl1m.taskmanager.dto.TaskDto;
import com.alkl1m.taskmanager.service.client.ClientService;
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
    private final ClientService clientService;
    private final HttpServletRequest httpServletRequest;
    private final JwtUtil jwtUtil;

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        String email = jwtUtil.extractUserName(token);
        List<ProjectDto> projectDtoList = clientService.getProjectsByUserEmail(email);
        if (projectDtoList == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(projectDtoList);
    }

    @PostMapping("/projects")
    public ResponseEntity<?> postProject(@RequestBody ProjectDto projectDto) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        String email = jwtUtil.extractUserName(token);
        ProjectDto cratedProjectDto = clientService.postProject(projectDto, email);
        if (cratedProjectDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");
        }
        return ResponseEntity.ok(projectDto);
    }

    @DeleteMapping("/projects")
    public ResponseEntity<Void> deleteProject(@RequestParam("projectId") Long projectId) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        String email = jwtUtil.extractUserName(token);
        clientService.deleteProject(projectId, email);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/projects")
    public ResponseEntity<?> updateProject(@RequestParam("projectId") Long projectId,
                                           @RequestBody ProjectDto projectDto) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        String email = jwtUtil.extractUserName(token);
        ProjectDto updatedProjectDto = clientService.updateProject(projectId, projectDto, email);
        if (updatedProjectDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");
        }
        return ResponseEntity.status(HttpStatus.OK).body(updatedProjectDto);
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskDto>> getAllTasks(@PathVariable Long projectId) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        String email = jwtUtil.extractUserName(token);
        List<TaskDto> taskDtoList = clientService.getTasksByProjectId(projectId, email);
        if (taskDtoList == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(taskDtoList);
    }

    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<?> postTask(@PathVariable Long projectId, @RequestBody TaskDto taskDto) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        String email = jwtUtil.extractUserName(token);
        TaskDto createdTaskDto = clientService.postTask(taskDto, email, projectId);
        if (createdTaskDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");
        }
        return ResponseEntity.ok(taskDto);
    }

    @DeleteMapping("/projects/{projectId}/tasks")
    public ResponseEntity<Void> deleteTask(@RequestParam("taskId") Long taskId, @PathVariable String projectId) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        String email = jwtUtil.extractUserName(token);
        clientService.deleteTask(taskId, email);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/projects/{projectId}/tasks")
    public ResponseEntity<?> updateTask(@RequestParam("taskId") Long taskId,
                                        @RequestBody TaskDto taskDto, @PathVariable String projectId) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        String email = jwtUtil.extractUserName(token);
        TaskDto updatedTaskDto = clientService.updateTask(taskId, taskDto, email);
        if (updatedTaskDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");
        }
        return ResponseEntity.status(HttpStatus.OK).body(updatedTaskDto);
    }
}
