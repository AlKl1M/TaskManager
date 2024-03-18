package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.controller.payload.auth.MessageResponse;
import com.alkl1m.taskmanager.controller.payload.project.*;
import com.alkl1m.taskmanager.service.auth.UserDetailsImpl;
import com.alkl1m.taskmanager.service.project.ProjectService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
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
    ResponseEntity<MessageResponse> createProject(@Valid @RequestBody CreateProjectRequest request,
                                                  @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                  BindingResult bindingResult) throws BindException {
        checkBindingErrors(bindingResult);
        CreateProjectCommand cmd = CreateProjectCommand.builder()
                .id(userDetails.getId())
                .name(request.name())
                .description(request.description())
                .build();
        ProjectDto project = projectService.create(cmd);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/api/projects/{id}")
                .buildAndExpand(project.id()).toUri();
        return ResponseEntity.created(location)
                        .body(new MessageResponse("Project created successfully"));
    }

    @PutMapping("/projects/{id}")
    @PreAuthorize("@accessChecker.isProjectBelongToUser(principal, #id)")
    ResponseEntity<?> updateProject(@PathVariable(name = "id") Long id,
                                    @RequestBody @Valid UpdateProjectRequest request,
                                    BindingResult bindingResult) throws BindException {
        checkBindingErrors(bindingResult);
        UpdateProjectCommand cmd = new UpdateProjectCommand(id, request.name(), request.description());
        projectService.update(cmd);
        log.info("Project updated");
        return ResponseEntity.ok("Project updated successfully");
    }

    @DeleteMapping("/projects/{id}")
    @PreAuthorize("@accessChecker.isProjectBelongToUser(principal, #id)")
    ResponseEntity<?> deleteProject(@PathVariable(name = "id") Long id) {
        projectService.delete(id);
        log.info("Project deleted");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/projects/{id}/changeStatus")
    @PreAuthorize("@accessChecker.isProjectBelongToUser(principal, #id)")
    ResponseEntity<?> changeProjectStatus(@PathVariable(name = "id") Long id) {
        projectService.changeStatus(id);
        return ResponseEntity.ok("Project status changed successfully");
    }

    private void checkBindingErrors(BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        }
    }
}
