package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.dto.ProjectDto;
import com.alkl1m.taskmanager.dto.TaskDto;
import com.alkl1m.taskmanager.service.client.ClientService;
import com.alkl1m.taskmanager.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService userService;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        String email = jwtUtil.extractUserName(token);
        List<ProjectDto> projectDtoList = userService.getProjectsByUserEmail(email);
        if (projectDtoList == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(projectDtoList);
    }

    @PostMapping("/projects")
    public ResponseEntity<?> postProject(@RequestBody ProjectDto projectDto) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        String email = jwtUtil.extractUserName(token);
        ProjectDto cratedProjectDto = userService.postProject(projectDto, email);
        if (cratedProjectDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");
        }
        return ResponseEntity.ok(projectDto);
    }

    @DeleteMapping("/projects")
    public ResponseEntity<Void> deleteProject(@RequestParam("projectId") Long projectId) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        String email = jwtUtil.extractUserName(token);
        userService.deleteProject(projectId, email);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/projects")
    public ResponseEntity<?> updateProject(@RequestParam("projectId") Long projectId,
                                           @RequestBody ProjectDto projectDto) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        String email = jwtUtil.extractUserName(token);
        ProjectDto updatedProjectDto = userService.updateProject(projectId, projectDto, email);
        if (updatedProjectDto == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");
        return ResponseEntity.status(HttpStatus.OK).body(updatedProjectDto);
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskDto>> getAllTasks(@PathVariable Long projectId) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        String email = jwtUtil.extractUserName(token);
        List<TaskDto> taskDtoList = userService.getTasksByProjectId(projectId, email);
        if (taskDtoList == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(taskDtoList);
    }
}
