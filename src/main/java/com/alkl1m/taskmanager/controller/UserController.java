package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.dto.ProjectDto;
import com.alkl1m.taskmanager.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        List<ProjectDto> projectDtoList = userService.getAllProjects();
        if (projectDtoList == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(projectDtoList);
    }
}
