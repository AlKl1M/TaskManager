package com.alkl1m.taskmanager.util;

import com.alkl1m.taskmanager.repository.ProjectRepository;
import com.alkl1m.taskmanager.service.auth.UserDetailsImpl;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccessChecker {
    private final ProjectRepository projectRepository;

    public boolean isProjectBelongToUser(@NotNull UserDetailsImpl userDetails,
                                         @NotNull Long id) {
        return projectRepository.getProjectById(id)
                .getUser()
                .getId()
                .equals(userDetails.getId());
    }
}
