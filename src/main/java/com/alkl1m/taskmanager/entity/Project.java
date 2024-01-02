package com.alkl1m.taskmanager.entity;

import com.alkl1m.taskmanager.dto.ProjectDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Data
@Table(name="projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private LocalDate creationDate;
    private LocalDate completionDate;
    private String status;

    @ManyToOne(fetch= FetchType.LAZY, optional=false)
    @JoinColumn(name="user_id", nullable = false)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;

    public ProjectDto getProjectDto() {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(id);
        projectDto.setName(name);
        projectDto.setDescription(description);
        projectDto.setCreationDate(creationDate);
        projectDto.setCompletionDate(completionDate);
        projectDto.setStatus(status);
        return projectDto;
    }
}
