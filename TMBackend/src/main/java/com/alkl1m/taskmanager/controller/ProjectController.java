package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.dto.project.*;
import com.alkl1m.taskmanager.service.project.ProjectService;
import com.alkl1m.taskmanager.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/client")
public class ProjectController {
    private final ProjectService projectService;
    private final HttpServletRequest httpServletRequest;
    private final JwtUtil jwtUtil;

    public ProjectController(ProjectService projectService, HttpServletRequest httpServletRequest, JwtUtil jwtUtil) {
        this.projectService = projectService;
        this.httpServletRequest = httpServletRequest;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/projects")
    ProjectsPagedResult<ProjectDto> findProjects(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "size", defaultValue = "10") Integer pageSize) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.extractId(token);
        FindProjectsQuery query = new FindProjectsQuery(pageNo, pageSize);
        return projectService.findProjects(query, userId);
    }

    @PostMapping("/projects")
    ResponseEntity<ProjectDto> create(@RequestBody @Validated CreateProjectRequest request) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.extractId(token);
        CreateProjectCommand cmd = new CreateProjectCommand(
                request.name(),
                request.description()
        );
        ProjectDto project = projectService.create(cmd, userId);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/api/projects/{id}")
                .buildAndExpand(project.id()).toUri();
        return ResponseEntity.created(location).body(project);
    }

    @PutMapping("/projects/{id}")
    void update(@PathVariable(name = "id") Long id,
                @RequestBody @Validated UpdateProjectRequest request) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.extractId(token);
        UpdateProjectCommand cmd = new UpdateProjectCommand(id, request.name(), request.description());
        projectService.update(cmd, userId);
    }

    @DeleteMapping("/projects/{id}")
    void delete(@PathVariable(name = "id") Long id) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.extractId(token);
        projectService.delete(id, userId);
    }

    @PutMapping("/projects/{id}/done")
    void changeStatus(@PathVariable(name = "id") Long id) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.extractId(token);
        projectService.changeStatus(id, userId);
    }
}
