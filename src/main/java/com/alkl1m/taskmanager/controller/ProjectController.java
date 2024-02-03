package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.dto.project.*;
import com.alkl1m.taskmanager.service.project.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/projects")
    ProjectsPagedResult<ProjectDto> findProjects(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "size", defaultValue = "10") Integer pageSize) {
        FindProjectsQuery query = new FindProjectsQuery(pageNo, pageSize);
        return projectService.findProjects(query);
    }

    @GetMapping("projects/search")
    public List<ProjectDto> searchProjects(@RequestParam("query") String query) {
        return projectService.search(query);
    }

    @PostMapping("/projects")
    ResponseEntity<ProjectDto> create(@RequestBody @Validated CreateProjectRequest request) {
        CreateProjectCommand cmd = new CreateProjectCommand(
                request.name(),
                request.description()
        );
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

    @PutMapping("/projects/{id}/done")
    void changeStatus(@PathVariable(name = "id") Long id) {
        projectService.changeStatus(id);
    }
}
