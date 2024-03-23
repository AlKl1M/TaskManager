package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.controller.payload.auth.MessageResponse;
import com.alkl1m.taskmanager.controller.payload.project.*;
import com.alkl1m.taskmanager.service.auth.UserDetailsImpl;
import com.alkl1m.taskmanager.service.project.ProjectService;
import com.alkl1m.taskmanager.util.checker.BindingChecker;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/projects")
    public List<ProjectDto> getAllProjects(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @RequestParam(required = false) String query) {
        if (query != null) {
            return projectService.getAllProjectsByQuery(userDetails.getId(), query);
        } else {
            return projectService.getAllProjects(userDetails.getId());
        }
    }

    @PostMapping("/projects")
    @BindingChecker
    ResponseEntity<MessageResponse> createProject(@Valid @RequestBody CreateProjectRequest request,
                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CreateProjectCommand cmd = CreateProjectCommand.builder()
                .id(userDetails.getId())
                .name(request.name())
                .description(request.description())
                .build();
        ProjectDto project = projectService.create(cmd);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/api/projects/{id:\\d+}")
                .buildAndExpand(project.id()).toUri();
        return ResponseEntity.created(location)
                        .body(new MessageResponse("Project created successfully"));
    }

    @PutMapping("/projects/{projectId:\\d+}")
    @BindingChecker
    @PreAuthorize("@accessChecker.isProjectBelongToUser(principal, #projectId)")
    ResponseEntity<?> updateProject(@PathVariable(name = "projectId") Long projectId,
                                    @RequestBody @Valid UpdateProjectRequest request) {
        UpdateProjectCommand cmd = new UpdateProjectCommand(projectId, request.name(), request.description());
        projectService.update(cmd);
        log.info("Project updated");
        return ResponseEntity.ok("Project updated successfully");
    }

    @DeleteMapping("/projects/{projectId:\\d+}")
    @PreAuthorize("@accessChecker.isProjectBelongToUser(principal, #projectId)")
    ResponseEntity<?> deleteProject(@PathVariable(name = "projectId") Long projectId) {
        projectService.delete(projectId);
        log.info("Project deleted");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/projects/{projectId:\\d+}/changeStatus")
    @PreAuthorize("@accessChecker.isProjectBelongToUser(principal, #projectId)")
    ResponseEntity<?> changeProjectStatus(@PathVariable(name = "projectId") Long projectId) {
        projectService.changeStatus(projectId);
        return ResponseEntity.ok("Project status changed successfully");
    }
}
