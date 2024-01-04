package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.dto.ProjectDto;
import com.alkl1m.taskmanager.service.client.ClientService;
import com.alkl1m.taskmanager.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
